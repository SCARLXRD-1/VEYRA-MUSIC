<!--
 * Copyright (C) 2026 SFG545
 * Copyright (C) 2026 AKASHI DEV
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
import NeumorphicKnob from '../controls/NeumorphicKnob.vue';

export default {
  name: 'QuickAudioDeck',
  components: {
    NeumorphicKnob
  },
  props: {
    app: { type: Object, required: true }
  },
  setup(props) {
    const config = computed(() => props.app.audioEngineConfig?.value || {});
    const crossfade = computed(() => props.app.crossfadeMode?.value || 'standard');
    const presets = computed(() => props.app.audioEnginePresets || []);
    const activePreset = computed(() => props.app.audioEngineActivePreset?.value);

    const isSmartActive = computed(() => crossfade.value === 'smart');
    const isEqActive = computed(() => Boolean(config.value.enabled && config.value.eqEnabled));

    const preampValue = computed({
      get: () => Number(config.value.preampDb || 0),
      set: (val) => {
        if (props.app.audioEngineConfig?.value) {
          props.app.audioEngineConfig.value.preampDb = val;
        }
      }
    });

    function setCrossfade(mode) {
      props.app.setCrossfadeMode?.(mode);
    }

    function toggleEq() {
      const next = !isEqActive.value;
      if (!config.value.enabled && props.app.audioEngineConfig?.value) {
        props.app.audioEngineConfig.value.enabled = true;
      }
      props.app.setManualEqEnabled?.(next);
    }

    function selectPreset(presetVal) {
      if (!isEqActive.value && props.app.audioEngineConfig?.value) {
        props.app.audioEngineConfig.value.enabled = true;
        props.app.setManualEqEnabled?.(true);
      }
      props.app.applyAudioEnginePreset?.(presetVal);
    }

    function openFullSettings() {
      props.app.openSettingsSection?.('audio-engine');
    }

    return {
      config,
      crossfade,
      presets,
      activePreset,
      isSmartActive,
      isEqActive,
      preampValue,
      setCrossfade,
      toggleEq,
      selectPreset,
      openFullSettings
    };
  }
};
</script>

<template>
  <div class="quick-audio-deck">
    <!-- Header -->
    <div class="quick-audio-deck__header">
      <div class="quick-audio-deck__title">
        <q-icon name="tune" size="14px" color="primary" />
        <span>DSP Audio Deck</span>
      </div>
      <span class="quick-audio-deck__chip is-active" style="font-size: 9px; padding: 2px 7px;">
        {{ isSmartActive ? 'WSOLA ON' : isEqActive ? 'EQ ON' : 'DIRECT' }}
      </span>
    </div>

    <!-- Crossfade Mode -->
    <div class="quick-audio-deck__section">
      <span class="quick-audio-deck__label">Modo Crossfade</span>
      <div class="quick-audio-deck__chips">
        <button
          type="button"
          class="quick-audio-deck__chip"
          :class="{ 'is-active': crossfade === 'smart' }"
          @click="setCrossfade('smart')"
        >
          Smart WSOLA
        </button>
        <button
          type="button"
          class="quick-audio-deck__chip"
          :class="{ 'is-active': crossfade === 'standard' }"
          @click="setCrossfade('standard')"
        >
          Estándar
        </button>
        <button
          type="button"
          class="quick-audio-deck__chip"
          :class="{ 'is-active': crossfade === 'off' }"
          @click="setCrossfade('off')"
        >
          Directo
        </button>
      </div>
    </div>

    <!-- Quick EQ Presets -->
    <div class="quick-audio-deck__section">
      <div class="quick-audio-deck__switch-row">
        <span class="quick-audio-deck__label" style="margin-bottom: 0;">Ecualizador 10-Band</span>
        <button
          type="button"
          class="quick-audio-deck__chip"
          :class="{ 'is-active': isEqActive }"
          style="font-size: 9.5px; padding: 2px 8px;"
          @click="toggleEq"
        >
          {{ isEqActive ? 'Activado' : 'Bypass' }}
        </button>
      </div>

      <div class="quick-audio-deck__chips" style="margin-top: 6px;">
        <button
          v-for="preset in presets.slice(0, 5)"
          :key="preset.value"
          type="button"
          class="quick-audio-deck__chip"
          :class="{ 'is-active': isEqActive && activePreset === preset.value }"
          @click="selectPreset(preset.value)"
        >
          {{ preset.label || preset.value }}
        </button>
      </div>
    </div>

    <!-- Analog Rotary Knob (Preamp Gain) -->
    <div class="quick-audio-deck__section">
      <span class="quick-audio-deck__label">Ganancia Preamp Analógica</span>
      <div class="quick-audio-deck__knob-row">
        <NeumorphicKnob
          v-model="preampValue"
          :min="-12"
          :max="6"
          :step="0.5"
          :default-value="0"
          label=""
          unit="dB"
          :size="46"
        />
        <div style="font-size: 10px; color: var(--muted, #94A3B8); line-height: 1.3; max-width: 140px;">
          Ajuste fino de headroom para evitar distorsión o elevar pistas de bajo volumen.
        </div>
      </div>
    </div>

    <!-- Footer button -->
    <div style="margin-top: 8px; text-align: center;">
      <button
        type="button"
        class="quick-audio-deck__chip"
        style="width: 100%; padding: 4px; border-radius: 8px; font-size: 10.5px;"
        @click="openFullSettings"
      >
        Abrir Estudio Completo de Audio
      </button>
    </div>
  </div>
</template>
