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
  name: 'PinsView',
  props: { app: { type: Object, required: true } },
  setup(props) {
    return props.app;
  }
};
</script>

<template>
  <section class="shelf-section pins-view">
    <div class="section-header">
      <h2>{{ $t('ui.pins.title') }}</h2>
      <span>{{ $t('ui.pins.itemCount', { count: pinnedTracks.length }) }}</span>
    </div>

    <div v-if="pinnedTracks.length" class="table-card">
      <div
        v-for="(track, index) in pinnedTracks"
        :key="`pin-${track.id}`"
        class="table-row pins-view__row"
        role="button"
        tabindex="0"
        @click="playTrack(track, { queueSource: pinnedTracks })"
        @keydown.enter.prevent="playTrack(track, { queueSource: pinnedTracks })"
        @keydown.space.prevent="playTrack(track, { queueSource: pinnedTracks })"
        @keydown="onSongActionKeydown($event, track)"
        @contextmenu="openSongActionMenu(track, $event)"
      >
        <span class="table-index">{{ index + 1 }}</span>
        <span class="table-track">
          <q-img v-if="track.thumbnail" :src="track.thumbnail" class="table-cover" />
          <span v-else class="table-cover table-cover--empty"><q-icon name="music_note" /></span>
          <span class="table-copy">
            <strong class="explicit-title">
              <span class="explicit-title__text">{{ track.title }}</span>
              <ExplicitBadge :explicit="track.explicit" />
              <DownloadIndicator
                :downloaded="isTrackDownloaded(track)"
                :downloading="isTrackDownloading(track)"
              />
            </strong>
            <small>{{ itemMeta(track) }}</small>
          </span>
        </span>
        <span class="table-album">{{ track.album || '—' }}</span>
        <span class="table-time">{{ track.duration || '—' }}</span>
        <button
          type="button"
          class="table-icon-button"
          :aria-label="$t('ui.pins.unpin', { title: track.title })"
          :title="$t('ui.pins.unpinAction')"
          @click.stop="togglePinnedTrack(track)"
          @keydown.stop
        >
          <q-icon name="push_pin" />
        </button>
      </div>
    </div>

    <div v-else class="empty-state pins-view__empty">
      {{ $t('ui.pins.empty') }}
    </div>
  </section>
</template>
