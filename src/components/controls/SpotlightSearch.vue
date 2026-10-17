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
import { watch } from 'vue';

export default {
  name: 'SpotlightSearch',
  props: { app: { type: Object, required: true } },
  setup(props) {
    watch(() => props.app.spotlightRows.value.length, (length) => {
      if (!length) {
        props.app.spotlightActiveIndex.value = 0;
        return;
      }

      if (props.app.spotlightActiveIndex.value >= length) {
        props.app.spotlightActiveIndex.value = length - 1;
      }
    });

    function onSpotlightKeydown(event) {
      if (event.key === 'Escape') {
        event.preventDefault();
        props.app.closeSpotlightSearch();
        return;
      }

      if (event.key === 'ArrowDown') {
        event.preventDefault();
        props.app.moveSpotlightSelection(1);
        return;
      }

      if (event.key === 'ArrowUp') {
        event.preventDefault();
        props.app.moveSpotlightSelection(-1);
      }
    }

    return {
      ...props.app,
      onSpotlightKeydown
    };
  }
};
</script>

<template>
  <Teleport to="body">
    <div v-if="spotlightOpen" class="spotlight-search-layer" @mousedown.self="closeSpotlightSearch">
      <section
        class="spotlight-search"
        role="dialog"
        aria-modal="true"
        :aria-label="$t('ui.accessibility.searchApp')"
        :style="playerBarStyle"
        @keydown="onSpotlightKeydown"
      >
        <form class="spotlight-search__form" @submit.prevent="submitSpotlightSearch">
          <q-icon name="search" />
          <input
            ref="spotlightInputRef"
            v-model="spotlightQuery"
            type="search"
            autocomplete="off"
            spellcheck="false"
            :placeholder="$t('ui.accessibility.searchItems')"
            :aria-label="$t('ui.accessibility.searchItems')"
            @input="queueSpotlightSearch"
          />
          <button type="button" :aria-label="$t('ui.accessibility.closeSearch')" @click="closeSpotlightSearch">
            <q-icon name="close" />
          </button>
        </form>

        <div v-if="spotlightLoading" class="spotlight-search__progress" aria-hidden="true">
          <span />
        </div>

        <div v-if="spotlightRows.length" class="spotlight-search__results" role="listbox" :aria-label="$t('ui.accessibility.searchResults')">
          <button
            v-for="(row, index) in spotlightRows"
            :key="row.id"
            type="button"
            class="spotlight-search__row"
            :class="{ 'spotlight-search__row--active': spotlightActiveIndex === index }"
            :aria-selected="spotlightActiveIndex === index"
            role="option"
            @mouseenter="spotlightActiveIndex = index"
            @click="openSpotlightRow(row)"
          >
            <img v-if="row.artwork" :src="row.artwork" alt="" class="spotlight-search__art" />
            <span v-else class="spotlight-search__art spotlight-search__art--empty">
              <q-icon :name="row.icon" />
            </span>
            <span class="spotlight-search__copy">
              <strong>{{ row.title }}</strong>
              <span>{{ row.subtitle }}</span>
            </span>
            <span v-if="row.type" class="spotlight-search__type">{{ row.type }}</span>
          </button>
        </div>

        <div v-else class="spotlight-search__empty" role="status">
          <q-spinner v-if="spotlightLoading" size="18px" />
          <q-icon v-else name="search_off" />
          <span>{{ spotlightLoading ? 'Searching...' : 'No matches' }}</span>
        </div>
      </section>
    </div>
  </Teleport>
</template>
