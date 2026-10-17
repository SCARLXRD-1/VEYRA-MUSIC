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
  name: 'CompactSettingsMenu',
  props: {
    app: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const eqMode = computed({
      get() {
        if (!props.app.audioEngineConfig.value.enabled) return 'off';
        if (props.app.audioEngineConfig.value.autoEqEnabled) return 'auto';
        if (props.app.audioEngineConfig.value.eqEnabled) return 'manual';
        return 'off';
      },
      set(val) {
        if (val === 'off') {
          props.app.setAutoEqEnabled(false);
          props.app.setManualEqEnabled(false);
        } else if (val === 'auto') {
          props.app.setAutoEqEnabled(true);
        } else if (val === 'manual') {
          props.app.setManualEqEnabled(true);
        }
      }
    });

    return {
      eqMode,
      crossfadeEnabled: props.app.crossfadeEnabled,
      crossfadeMode: props.app.crossfadeMode,
      crossfadeModeOptions: props.app.crossfadeModeOptions,
      discordRpcEnabled: props.app.discordRpcEnabled
    };
  }
};
</script>

<template>
  <div class="compact-settings-menu" :aria-label="$t('ui.player.quickSettings')">
    <div class="compact-settings-header">
      <q-icon name="settings" size="16px" />
      <span>{{ $t('ui.player.quickSettings') }}</span>
    </div>

    <q-separator dark />

    <div class="compact-settings-sections">
      <!-- Equalizer Section -->
      <div class="compact-settings-section">
        <div class="compact-settings-row">
          <label class="compact-settings-label">{{ $t('ui.settingsExtras.equalizerMode') }}</label>
          <div class="settings-option-group" role="group" :aria-label="$t('ui.settingsExtras.equalizerMode')">
            <button
              v-for="option in [{ value: 'off' }, { value: 'auto' }, { value: 'manual' }]"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': eqMode === option.value }"
              :aria-label="`${$t('ui.settingsExtras.equalizerMode')}: ${$t(`settings.ui.optionLabels.equalizer.${option.value}`)}`"
              :aria-pressed="eqMode === option.value"
              @click="eqMode = option.value"
            >
              {{ $t(`settings.ui.optionLabels.equalizer.${option.value}`) }}
            </button>
          </div>
        </div>
      </div>

      <q-separator dark />

      <!-- Crossfade Section -->
      <div class="compact-settings-section">
        <div class="compact-settings-row">
          <label for="compact-crossfade-toggle" class="compact-settings-label">{{ $t('settings.ui.crossfade') }}</label>
          <q-toggle
            id="compact-crossfade-toggle"
            v-model="crossfadeEnabled"
            color="primary"
            dense
            :aria-label="$t('settings.ui.crossfade')"
          />
        </div>
        <div class="compact-settings-row" :class="{ 'compact-settings-row--disabled': !crossfadeEnabled }">
          <span class="compact-settings-label">{{ $t('settings.ui.crossfadeMode') }}</span>
          <div class="settings-option-group" role="group" :aria-label="$t('settings.ui.crossfadeMode')">
            <button
              v-for="option in crossfadeModeOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': crossfadeMode === option.value }"
              :disabled="!crossfadeEnabled"
              :aria-label="`${$t('settings.ui.crossfadeMode')}: ${$t(`settings.ui.optionLabels.crossfade.${option.value}`)}`"
              :aria-pressed="crossfadeMode === option.value"
              @click="app.setCrossfadeMode(option.value)"
            >
              {{ $t(`settings.ui.optionLabels.crossfade.${option.value}`) }}
            </button>
          </div>
        </div>
      </div>

      <q-separator dark />

      <!-- Discord RPC Section -->
      <div class="compact-settings-section">
        <div class="compact-settings-row">
          <label for="compact-discord-toggle" class="compact-settings-label">{{ $t('settings.ui.discord') }}</label>
          <q-toggle
            id="compact-discord-toggle"
            v-model="discordRpcEnabled"
            color="primary"
            dense
            :aria-label="$t('settings.ui.discord')"
          />
        </div>
      </div>
    </div>
  </div>
</template>
