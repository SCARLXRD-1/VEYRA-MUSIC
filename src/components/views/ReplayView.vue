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
  name: 'ReplayView',
  props: { app: { type: Object, required: true } },
  setup(props) {
    return props.app;
  }
};
</script>

<template>
  <section class="replay-view">
    <div class="replay-toolbar">
      <div class="replay-periods" role="tablist" :aria-label="$t('ui.replay.period')">
        <button
          v-for="option in replayPeriodOptions"
          :key="option.value"
          type="button"
          :class="{ 'replay-periods__button--active': replayPeriod === option.value }"
          :aria-selected="replayPeriod === option.value"
          role="tab"
          @click="replayPeriod = option.value"
        >
          {{ $t(option.value === 'month' ? 'ui.replay.days30' : option.value === 'week' ? 'ui.replay.days7' : 'ui.replay.allTime') }}
        </button>
      </div>

      <div class="replay-actions">
        <button type="button" class="replay-action" :disabled="!replaySummary.tracks.length" @click="playReplayTopTracks">
          <q-icon name="play_arrow" />
          <span>{{ $t('ui.replay.playTop') }}</span>
        </button>
        <button type="button" class="replay-action replay-action--muted" :disabled="!replayEvents.length" @click="clearReplayStats">
          <q-icon name="delete_outline" />
          <span>{{ $t('ui.replay.clear') }}</span>
        </button>
      </div>
    </div>

    <div class="replay-summary" :aria-label="$t('ui.replay.summary')">
      <div>
        <span>{{ $t('ui.replay.listens') }}</span>
        <strong>{{ replaySummary.totalPlays }}</strong>
      </div>
      <div>
        <span>{{ $t('ui.replay.time') }}</span>
        <strong>{{ replayDurationLabel(replaySummary.totalSeconds) }}</strong>
      </div>
      <div>
        <span>{{ $t('ui.replay.tracks') }}</span>
        <strong>{{ replaySummary.uniqueTracks }}</strong>
      </div>
      <div>
        <span>{{ $t('ui.replay.artists') }}</span>
        <strong>{{ replaySummary.uniqueArtists }}</strong>
      </div>
      <div>
        <span>{{ $t('ui.replay.days') }}</span>
        <strong>{{ replaySummary.activeDays }}</strong>
      </div>
    </div>

    <section class="shelf-section replay-section">
      <div class="section-header">
        <h2>{{ $t('ui.replay.topTracks') }}</h2>
        <span>{{ $t('ui.replay.trackCount', { count: replaySummary.tracks.length }) }}</span>
      </div>

      <div v-if="replaySummary.tracks.length" class="table-card replay-table">
        <button
          v-for="(entry, index) in replaySummary.tracks.slice(0, 12)"
          :key="`replay-track-${entry.key}`"
          type="button"
          class="table-row replay-row replay-row--track"
          :style="replayRankStyle(entry, replaySummary.tracks)"
          @click="playTrack(entry.item, { queueSource: replaySummary.tracks.map((item) => item.item) })"
          @keydown="onSongActionKeydown($event, entry.item)"
          @contextmenu="openSongActionMenu(entry.item, $event)"
        >
          <span class="table-index">{{ index + 1 }}</span>
          <span class="table-track">
            <q-img v-if="entry.item.thumbnail" :src="entry.item.thumbnail" class="table-cover" />
            <span v-else class="table-cover table-cover--empty"><q-icon name="music_note" /></span>
            <span class="table-copy">
              <strong class="explicit-title">
                <span class="explicit-title__text">{{ entry.item.title }}</span>
                <ExplicitBadge :explicit="entry.item.explicit" />
                <DownloadIndicator
                  :downloaded="isTrackDownloaded(entry.item)"
                  :downloading="isTrackDownloading(entry.item)"
                />
              </strong>
              <small>{{ entry.item.artist || itemMeta(entry.item) }}</small>
            </span>
          </span>
          <span class="table-album">{{ entry.item.album || '—' }}</span>
          <span class="replay-count">{{ $t('ui.replay.plays', { count: entry.plays }) }}</span>
          <span class="table-time">{{ replayDurationLabel(entry.seconds) }}</span>
        </button>
      </div>

      <div v-else class="empty-state replay-empty">
        {{ $t('ui.replay.empty') }}
      </div>
    </section>

    <div class="replay-lists">
      <section class="shelf-section replay-section">
        <div class="section-header">
          <h2>{{ $t('ui.replay.artists') }}</h2>
          <span>{{ $t('ui.replay.artistCount', { count: replaySummary.artists.length }) }}</span>
        </div>

        <div v-if="replaySummary.artists.length" class="replay-rank-list">
          <div
            v-for="(entry, index) in replaySummary.artists.slice(0, 10)"
            :key="`replay-artist-${entry.key}`"
            class="replay-rank-row"
            :style="replayRankStyle(entry, replaySummary.artists)"
          >
            <span>{{ index + 1 }}</span>
            <strong>{{ entry.item.title }}</strong>
            <em>{{ $t('ui.replay.plays', { count: entry.plays }) }}</em>
          </div>
        </div>
        <div v-else class="replay-mini-empty">{{ $t('ui.replay.noArtists') }}</div>
      </section>

      <section class="shelf-section replay-section">
        <div class="section-header">
          <h2>{{ $t('ui.replay.albums') }}</h2>
          <span>{{ $t('ui.replay.albumCount', { count: replaySummary.albums.length }) }}</span>
        </div>

        <div v-if="replaySummary.albums.length" class="replay-rank-list">
          <div
            v-for="(entry, index) in replaySummary.albums.slice(0, 10)"
            :key="`replay-album-${entry.key}`"
            class="replay-rank-row replay-rank-row--album"
            :style="replayRankStyle(entry, replaySummary.albums)"
          >
            <span>{{ index + 1 }}</span>
            <q-img v-if="entry.item.thumbnail" :src="entry.item.thumbnail" class="replay-rank-cover" />
            <strong>{{ entry.item.title }}</strong>
            <em>{{ $t('ui.replay.plays', { count: entry.plays }) }}</em>
          </div>
        </div>
        <div v-else class="replay-mini-empty">{{ $t('ui.replay.noAlbums') }}</div>
      </section>
    </div>
  </section>
</template>
