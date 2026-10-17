<!--
 * Copyright (C) 2026 SFG545
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music es un fork de Orchard (https://github.com/SFG5453/Orchard),
 * mantenido únicamente para consumo personal y sin afiliación con el
 * proyecto original.
 *
 * Veyra Music is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Veyra Music is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
-->

<script>
import { computed } from 'vue';
import { cachedCustomArtistIndex, fetchCustomArtistConfig } from '../../app/appearance/customArtistPacks.js';
import { useI18n } from '../../app/i18n/index.js';

export default {
  name: 'SearchView',
  props: { app: { type: Object, required: true } },
  setup(props) {
    const { t } = useI18n();
    const customArtistId = (item) => item?.id || props.app.itemBrowseId(item);
    const isCustomArtistItem = (item) => {
      if (props.app.customArtistPagesEnabled?.value === false) return false;
      if (!props.app.isArtistItem(item)) return false;
      const artistId = customArtistId(item);
      return Boolean(cachedCustomArtistIndex()?.artists?.[artistId]);
    };
    const customArtistAccentColor = (config) => {
      const variables = config?.theme?.cssVariables || {};
      return variables['--custom-artist-accent'] || variables['--custom-artist-accent-dark'] || '';
    };
    const openSearchItem = async (event, item, source = []) => {
      if (props.app.customArtistPagesEnabled?.value === false) {
        await props.app.openMedia(item, source);
        return;
      }

      const artistId = isCustomArtistItem(item) ? customArtistId(item) : '';
      const sourceElement = event?.currentTarget?.querySelector?.('.custom-artist-profile-art__image');
      if (!artistId || !sourceElement || !props.app.openCustomArtistProfileCameraTransition) {
        await props.app.openMedia(item, source);
        return;
      }

      const configPromise = fetchCustomArtistConfig(artistId).catch(() => null);
      await props.app.openCustomArtistProfileCameraTransition({
        sourceElement,
        accentColorPromise: configPromise.then(customArtistAccentColor),
        open: async () => {
          await configPromise;
          await props.app.openMedia(item, source);
        }
      });
    };
    const librarySection = computed(() => {
      const sections = props.app.searchResult.value.sections || [];
      return sections.length === 1 && sections[0].key?.startsWith('library-')
        ? sections[0]
        : null;
    });
    const libraryCategoryTitle = computed(() => {
      const category = librarySection.value?.key?.replace('library-', '');
      return category ? t(`ui.search.filters.${category}`) : librarySection.value?.title || '';
    });
    const libraryEmptyState = computed(() => ({
      Albums: {
        icon: 'album',
        title: t('ui.search.albumsEmpty'),
        detail: t('ui.search.albumsHint')
      },
      Artists: {
        icon: 'person_off',
        title: t('ui.search.artistsEmpty'),
        detail: t('ui.search.artistsHint')
      },
      Songs: {
        icon: 'music_off',
        title: t('ui.search.songsEmpty'),
        detail: t('ui.search.songsHint')
      }
    }[librarySection.value?.title] || {
      icon: 'library_music',
      title: t('ui.search.nothingYet'),
      detail: t('ui.search.libraryHint')
    }));
    const isSongsLibrary = computed(() => librarySection.value?.key === 'library-songs');
    const libraryItems = computed(() => {
      const items = librarySection.value?.items || [];
      return isSongsLibrary.value ? items.filter(props.app.isPlayableTrack) : items;
    });
    const shuffleLibrarySongs = () => {
      const tracks = libraryItems.value;
      if (!tracks.length) return;

      props.app.playCollection({
        kind: 'playlist',
        title: t('nav.songs'),
        tracks
      }, { shuffle: true });
    };

    return {
      ...props.app,
      librarySection,
      libraryCategoryTitle,
      libraryEmptyState,
      isSongsLibrary,
      libraryItems,
      isLibraryCategory: computed(() => Boolean(librarySection.value)),
      isLiveShowsSearch: computed(() => props.app.searchResult.value.source === 'ticketmaster'),
      isCustomArtistItem,
      openSearchItem,
      shuffleLibrarySongs,
      t
    };
  }
};
</script>

<template>
  <div class="search-view" :class="{ 'search-view--library': isLibraryCategory }">
    <header class="search-menu">
      <div class="search-menu__summary">
        <h1 v-if="isLiveShowsSearch">{{ $t('ui.search.liveNear', { location: searchResult.location }) }}</h1>
        <h1 v-else-if="isLibraryCategory">{{ libraryCategoryTitle }}</h1>
        <h1 v-else>{{ query.trim() ? $t('ui.search.resultsFor', { query: query.trim() }) : $t('ui.search.title') }}</h1>
        <p aria-live="polite">
          <template v-if="loading && isLibraryCategory">{{ $t('ui.search.loadingCategory', { category: librarySection.title.toLowerCase() }) }}</template>
          <template v-else-if="loading">{{ $t('ui.search.updating') }}</template>
          <template v-else-if="isLiveShowsSearch && flatResults.length">{{ $t('ui.search.upcomingEvents', { count: flatResults.length }) }}</template>
          <template v-else-if="isLiveShowsSearch">{{ $t('ui.search.noEvents') }}</template>
          <template v-else-if="isLibraryCategory">{{ libraryItems.length }} {{ $t(libraryItems.length === 1 ? 'ui.search.item' : 'ui.search.items') }}</template>
          <template v-else-if="flatResults.length">{{ flatResults.length }} {{ $t('ui.search.acrossCategories', { count: searchCategorySections.length }) }}</template>
          <template v-else>{{ $t('ui.search.prompt') }}</template>
        </p>
      </div>

      <div v-if="!isLibraryCategory && !isLiveShowsSearch" class="search-filters" role="group" :aria-label="$t('ui.search.filterLabel')">
        <button
          v-for="filter in filters"
          :key="filter.value"
          type="button"
          class="search-filter"
          :class="{ 'search-filter--active': selectedFilter === filter.value }"
          :aria-pressed="selectedFilter === filter.value"
          @click="selectedFilter = filter.value"
        >
          {{ $t(`ui.search.filters.${filter.value}`) }}
        </button>
      </div>
    </header>

    <div v-if="loading && flatResults.length" class="search-progress" aria-hidden="true">
      <span />
    </div>

    <div v-if="loading && !flatResults.length" class="search-loading" role="status">
      <div v-for="index in 5" :key="index" class="search-loading__row">
        <span class="search-loading__art" />
        <span class="search-loading__copy" />
      </div>
    </div>

    <template v-else-if="isLibraryCategory ? libraryItems.length : flatResults.length">
      <section v-if="!isLibraryCategory && !isLiveShowsSearch && searchTopResults.length" class="search-top-results" aria-labelledby="search-top-heading">
        <div class="section-header">
          <h2 id="search-top-heading">{{ $t('ui.search.topMatches') }}</h2>
          <span>{{ searchTopResults.length }} {{ $t('ui.search.results') }}</span>
        </div>

        <div class="search-top-grid">
          <button
            v-if="searchTopResults[0]"
            type="button"
            class="search-top-card search-top-card--lead"
            @click="openSearchItem($event, searchTopResults[0], flatPlayableResults)"
            @keydown="onSongActionKeydown($event, searchTopResults[0])"
            @contextmenu="shareMediaSongLink(searchTopResults[0], $event)"
          >
            <q-img
              v-if="mediaThumbnail(searchTopResults[0])"
              :src="mediaThumbnail(searchTopResults[0])"
              class="search-top-card__art"
              :class="{
                'search-top-card__art--artist': isArtistItem(searchTopResults[0]),
                'custom-artist-profile-art__image': isCustomArtistItem(searchTopResults[0])
              }"
            />
            <span v-else class="search-top-card__art search-top-card__art--empty">
              <q-icon :name="isArtistItem(searchTopResults[0]) ? 'person' : 'music_note'" />
            </span>
            <span class="search-top-card__copy">
              <strong class="explicit-title">
                <span class="explicit-title__text">{{ searchTopResults[0].title }}</span>
                <ExplicitBadge :explicit="searchTopResults[0].explicit" />
                <DownloadIndicator
                  :downloaded="isTrackDownloaded(searchTopResults[0])"
                  :downloading="isTrackDownloading(searchTopResults[0])"
                />
              </strong>
              <span>{{ itemMeta(searchTopResults[0]) || itemTypeLabel(searchTopResults[0]) }}</span>
              <span>{{ itemStat(searchTopResults[0]) || itemTypeLabel(searchTopResults[0]) }}</span>
            </span>
          </button>

          <div v-if="searchTopResults.length > 1" class="search-top-list">
            <button
              v-for="item in searchTopResults.slice(1)"
              :key="`search-top-${item.id || itemBrowseId(item) || item.title}`"
              type="button"
              class="search-top-card search-top-card--compact"
              @click="openSearchItem($event, item, flatPlayableResults)"
              @keydown="onSongActionKeydown($event, item)"
              @contextmenu="shareMediaSongLink(item, $event)"
            >
              <q-img
                v-if="mediaThumbnail(item)"
                :src="mediaThumbnail(item)"
                class="search-top-card__art"
                :class="{
                  'search-top-card__art--artist': isArtistItem(item),
                  'custom-artist-profile-art__image': isCustomArtistItem(item)
                }"
              />
              <span v-else class="search-top-card__art search-top-card__art--empty">
                <q-icon :name="isArtistItem(item) ? 'person' : 'music_note'" />
              </span>
              <span class="search-top-card__copy">
                <strong class="explicit-title">
                  <span class="explicit-title__text">{{ item.title }}</span>
                  <ExplicitBadge :explicit="item.explicit" />
                  <DownloadIndicator
                    :downloaded="isTrackDownloaded(item)"
                    :downloading="isTrackDownloading(item)"
                  />
                </strong>
                <span>{{ itemMeta(item) || itemTypeLabel(item) }}</span>
              </span>
              <span class="search-top-card__type">{{ itemTypeLabel(item) }}</span>
            </button>
          </div>
        </div>
      </section>

      <section
        v-for="section in searchCategorySections"
        :key="section.key"
        class="shelf-section search-shelf"
        :class="[`search-shelf--${section.key}`, { 'search-shelf--library': isLibraryCategory }]"
      >
        <div v-if="!isLibraryCategory" class="section-header">
          <h2>{{ section.title }}</h2>
          <div class="section-header__actions">
            <span>{{ sectionCount(section) }}</span>
            <button v-if="sectionHasMore(section)" type="button" class="section-more-button" @click="openSectionMore(section)">
              {{ $t('ui.home.seeAll') }}
            </button>
            <div class="shelf-nav">
              <button type="button" :aria-label="$t('ui.home.scrollSectionLeft', { section: section.title })" @click="scrollShelf($event, -1)"><q-icon name="chevron_left" /></button>
              <button type="button" :aria-label="$t('ui.home.scrollSectionRight', { section: section.title })" @click="scrollShelf($event, 1)"><q-icon name="chevron_right" /></button>
            </div>
          </div>
        </div>

        <div class="media-rail search-media-rail">
          <button
            v-if="isSongsLibrary"
            type="button"
            class="search-media-card search-media-card--shuffle"
            @click="shuffleLibrarySongs"
          >
            <span class="search-media-card__art search-media-card__art--empty">
              <q-icon name="shuffle" />
            </span>
            <strong>{{ $t('ui.search.shuffleAll') }}</strong>
            <span>{{ $t('ui.search.song') }}</span>
          </button>
          <button
            v-for="item in isLibraryCategory ? libraryItems : sectionPreviewItems(section)"
            :key="`${section.key}-${item.id || itemBrowseId(item) || item.title}`"
            type="button"
            class="search-media-card"
            :class="{ 'search-media-card--event': item.type === 'event' }"
            @click="openSearchItem($event, item, section.items)"
            @keydown="item.type !== 'event' && onSongActionKeydown($event, item)"
            @contextmenu="item.type !== 'event' && shareMediaSongLink(item, $event)"
          >
            <q-img
              v-if="mediaThumbnail(item)"
              :src="mediaThumbnail(item)"
              class="search-media-card__art"
              :class="{
                'search-media-card__art--artist': isArtistItem(item),
                'custom-artist-profile-art__image': isCustomArtistItem(item)
              }"
            />
            <span v-else class="search-media-card__art search-media-card__art--empty">
              <q-icon :name="item.type === 'event' ? 'confirmation_number' : isArtistItem(item) ? 'person' : 'album'" />
            </span>
            <strong class="explicit-title">
              <span class="explicit-title__text">{{ item.title }}</span>
              <ExplicitBadge :explicit="item.explicit" />
              <DownloadIndicator
                v-if="isPlayableTrack(item)"
                :downloaded="isTrackDownloaded(item)"
                :downloading="isTrackDownloading(item)"
              />
            </strong>
            <span>{{ itemMeta(item) }}</span>
          </button>
        </div>
      </section>
    </template>

    <div v-else class="search-empty">
      <q-icon :name="isLibraryCategory ? libraryEmptyState.icon : isLiveShowsSearch ? 'event_busy' : query.trim() ? 'search_off' : 'search'" />
      <strong>{{ isLibraryCategory ? libraryEmptyState.title : isLiveShowsSearch ? `No live shows found near ${searchResult.location}` : query.trim() ? `No results for “${query.trim()}”` : 'Start typing to search' }}</strong>
      <span>{{ isLibraryCategory ? libraryEmptyState.detail : isLiveShowsSearch ? $t('ui.search.tryLocation') : query.trim() ? $t('ui.search.tryQuery') : $t('ui.search.resultsUpdate') }}</span>
    </div>
  </div>
</template>
