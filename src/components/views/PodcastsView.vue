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
export default {
  name: 'PodcastsView',
  props: { app: { type: Object, required: true } },
  setup(props) {
    return props.app;
  }
};
</script>

<template>
  <div v-if="podcastLoading && !podcastFeed.sections.length" class="empty-state" role="status">
    {{ $t('ui.podcasts.loading') }}
  </div>

  <!-- A real element, not a <template>: the v-for below would otherwise make
       this branch a multi-root fragment, which cannot run the view-fade leave
       transition in AppFrame and wedges it for every other view. -->
  <div v-else-if="podcastFeed.sections.length" class="podcasts-view">
    <section v-for="section in podcastFeed.sections" :key="section.key" class="shelf-section">
      <div class="section-header">
        <h2>{{ section.title }}</h2>
        <div class="section-header__actions">
          <span>{{ sectionCount(section) }}</span>
          <div class="shelf-nav">
            <button type="button" :aria-label="$t('ui.podcasts.scrollLeft', { section: section.title })" @click="scrollShelf($event, -1)"><q-icon name="chevron_left" /></button>
            <button type="button" :aria-label="$t('ui.podcasts.scrollRight', { section: section.title })" @click="scrollShelf($event, 1)"><q-icon name="chevron_right" /></button>
          </div>
        </div>
      </div>

      <div class="media-rail">
        <article
          v-for="item in section.items"
          :key="`podcast-${item.id || itemBrowseId(item) || item.title}`"
          class="media-card"
          role="button"
          tabindex="0"
          @click="openMedia(item, section.items)"
          @keydown.enter.prevent="openMedia(item, section.items)"
          @keydown.space.prevent="openMedia(item, section.items)"
        >
          <q-img v-if="item.thumbnail" :src="item.thumbnail" class="media-card__art" />
          <div v-else class="media-card__art media-card__art--empty"><q-icon name="podcasts" /></div>
          <div class="media-card__title">{{ item.title }}</div>
          <div class="media-card__meta">{{ itemMeta(item) || itemTypeLabel(item) }}</div>
          <div class="media-card__stat">{{ itemStat(item) }}</div>
        </article>
      </div>
    </section>
  </div>

  <div v-else class="empty-state">{{ $t('ui.podcasts.noResults') }}</div>
</template>
