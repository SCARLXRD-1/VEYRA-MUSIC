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

export default {
  name: 'DataSavingSection',
  props: { app: { type: Object, required: true } },
  setup(props) {
    const app = props.app;
    const streamQualityDescription = computed(() => {
      const active = app.streamQualityOptions
        .find((option) => option.value === app.streamQuality.value);
      return active?.description || '';
    });

    return { ...app, app, streamQualityDescription };
  }
};
</script>

<template>
  <section id="settings-data-saving" class="settings-section" aria-labelledby="settings-data-saving-title">
    <div class="settings-section__heading">
      <h2 id="settings-data-saving-title">{{ $t('settings.sections.dataSaving') }}</h2>
      <p>{{ $t('settings.ui.dataSavingDescription') }}</p>
    </div>

    <div class="settings-row settings-row--options">
      <div class="settings-row__copy">
        <label id="settings-stream-quality-label">{{ $t('settings.ui.streamingQuality') }}</label>
        <p>{{ $t(`settings.ui.streamQualityDescription.${streamQuality}`) }}</p>
      </div>
      <div class="settings-option-group" role="group" aria-labelledby="settings-stream-quality-label">
        <button
          v-for="option in streamQualityOptions"
          :key="option.value"
          type="button"
          class="settings-option"
          :class="{ 'settings-option--active': streamQuality === option.value }"
          :aria-pressed="streamQuality === option.value"
          @click="streamQuality = option.value"
        >
          {{ $t(`settings.ui.optionLabels.streamQuality.${option.value}`) }}
        </button>
      </div>
    </div>

    <div class="settings-row">
      <div class="settings-row__copy">
        <label for="settings-video-playback">{{ $t('settings.ui.playVideos') }}</label>
        <p>{{ $t('settings.ui.playVideosDescription') }}</p>
      </div>
      <q-toggle id="settings-video-playback" v-model="videoPlaybackEnabled" color="primary" :aria-label="$t('settings.ui.playVideos')" />
    </div>

    <div class="settings-row">
      <div class="settings-row__copy">
        <label for="settings-animated-artwork">{{ $t('settings.ui.animatedArtwork') }}</label>
        <p>{{ $t('settings.ui.animatedArtworkDescription') }}</p>
      </div>
      <q-toggle id="settings-animated-artwork" v-model="animatedArtworkEnabled" color="primary" :aria-label="$t('settings.ui.animatedArtwork')" />
    </div>
  </section>
</template>
