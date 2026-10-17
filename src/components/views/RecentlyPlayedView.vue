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
  name: 'RecentlyPlayedView',
  props: { app: { type: Object, required: true } },
  setup(props) {
    return props.app;
  }
};
</script>

<template>
  <section class="shelf-section recently-played-view">
    <div class="section-header">
      <h2>{{ $t('ui.recent.history') }}</h2>
      <span>{{ $t('ui.recent.trackCount', { count: history.length }) }}</span>
    </div>

    <div v-if="history.length" class="table-card">
      <button
        v-for="(item, index) in history"
        :key="`recently-played-${item.id}-${index}`"
        type="button"
        class="table-row recently-played-view__row"
        @click="playHistoryTrack(item)"
        @keydown="onSongActionKeydown($event, item)"
        @contextmenu="openSongActionMenu(item, $event)"
      >
        <span class="table-index">{{ index + 1 }}</span>
        <span class="table-track">
          <q-img v-if="item.thumbnail" :src="item.thumbnail" class="table-cover" />
          <span v-else class="table-cover table-cover--empty">
            <q-icon name="music_note" />
          </span>
          <span class="table-copy">
            <strong class="explicit-title">
              <span class="explicit-title__text">{{ item.title }}</span>
              <ExplicitBadge :explicit="item.explicit" />
              <DownloadIndicator
                :downloaded="isTrackDownloaded(item)"
                :downloading="isTrackDownloading(item)"
              />
            </strong>
            <small>{{ itemMeta(item) }}</small>
          </span>
        </span>
        <span class="table-album">{{ item.album || '—' }}</span>
        <span class="table-time">{{ item.duration || '—' }}</span>
      </button>
    </div>

    <div v-else class="empty-state">{{ $t('ui.recent.empty') }}</div>
  </section>
</template>
