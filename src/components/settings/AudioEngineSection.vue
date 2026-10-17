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
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import NeumorphicKnob from '../controls/NeumorphicKnob.vue';

export default {
  name: 'AudioEngineSection',
  components: {
    NeumorphicKnob
  },
  props: { app: { type: Object, required: true } },
  setup(props) {
    const spectrum = ref(Array.from({ length: 32 }, () => 0));
    const profileInput = ref(null);
    let animationFrame = 0;

    const autoEqStatus = computed(() => {
      const profile = props.app.audioEngineAutoProfile.value || props.app.audioEngineAutoProfile;
      const parts = [
        profile?.learned ? 'Using learned profile' : 'Learning this track',
        `${profile?.profileCount || 0} saved profile${profile?.profileCount === 1 ? '' : 's'}`
      ];
      if (profile?.tempo) parts.push(`${Math.round(profile.tempo)} BPM`);
      return parts.join(' · ');
    });

    const responsePath = computed(() => {
      const config = props.app.audioEngineConfig.value || props.app.audioEngineConfig;
      const autoGains = props.app.audioEngineAutoGains.value || props.app.audioEngineAutoGains;
      const gains = config?.autoEqEnabled ? autoGains : (config?.gains || []);
      const points = gains.map((gain, index) => {
        const x = 8 + (index * 284) / Math.max(1, gains.length - 1);
        const y = Math.max(8, Math.min(112, 60 - Number(gain || 0) * 4));
        return `${x},${y}`;
      });
      return points.length ? `M ${points.join(' L ')}` : 'M 8,60 L 292,60';
    });

    function updateSpectrum() {
      const next = props.app.audioSpectrum?.(32) || [];
      spectrum.value = spectrum.value.map((value, index) => Math.max(next[index] || 0, value * 0.78));
      animationFrame = window.requestAnimationFrame(updateSpectrum);
    }

    async function importProfile(event) {
      try {
        await props.app.importAudioEngineProfile(event.target.files?.[0]);
      } catch (error) {
        props.app.audioEngineMessage.value = error.message || 'Could not import that audio profile.';
      } finally {
        event.target.value = '';
      }
    }

    onMounted(() => {
      updateSpectrum();
      props.app.loadAudioOutputDevices?.();
    });
    onBeforeUnmount(() => window.cancelAnimationFrame(animationFrame));

    return { ...props.app, spectrum, profileInput, responsePath, autoEqStatus, importProfile };
  }
};
</script>

<template>
  <section id="settings-audio-engine" class="settings-section audio-engine" aria-labelledby="settings-audio-engine-title">
    <div class="settings-section__heading audio-engine__heading">
      <div>
        <h2 id="settings-audio-engine-title">{{ $t('ui.settingsExtras.audioEngine') }}</h2>
        <p>{{ $t('ui.settingsExtras.audioEngineDescription') }}</p>
      </div>
      <q-toggle v-model="audioEngineConfig.enabled" color="primary" :aria-label="$t('ui.settingsExtras.audioEngine')" />
    </div>

    <div class="audio-engine__monitor" :class="{ 'audio-engine__monitor--bypassed': !audioEngineConfig.enabled }">
      <div class="audio-engine__spectrum" aria-hidden="true">
        <i v-for="(level, index) in spectrum" :key="index" :style="{ height: `${Math.max(3, level * 100)}%` }"></i>
      </div>
      <svg class="audio-engine__response" viewBox="0 0 300 120" preserveAspectRatio="none" :aria-label="$t('ui.settingsExtras.equalizerCurve')">
        <line x1="0" y1="60" x2="300" y2="60" />
        <path :d="responsePath" />
      </svg>
      <span>{{ audioEngineConfig.enabled ? (audioEngineConfig.autoEqEnabled ? $t('ui.settingsExtras.autoEqActive') : audioEngineConfig.eqEnabled ? $t('ui.settingsExtras.manualEqActive') : $t('ui.settingsExtras.direct')) : $t('ui.settingsExtras.bypassed') }}</span>
    </div>

    <div class="settings-row">
      <div class="settings-row__copy">
        <label for="settings-engine-auto-eq">{{ $t('ui.settingsExtras.automaticEq') }}</label>
        <p>{{ $t('ui.settingsExtras.autoEqDescription') }}</p>
      </div>
      <q-toggle
        id="settings-engine-auto-eq"
        :model-value="audioEngineConfig.autoEqEnabled"
        :disable="!audioEngineConfig.enabled"
        color="primary"
        @update:model-value="setAutoEqEnabled"
      />
    </div>
    <p v-if="audioEngineConfig.autoEqEnabled" class="audio-engine__auto-status">
      {{ autoEqStatus }}
    </p>

    <div class="settings-row">
      <div class="settings-row__copy">
        <label for="settings-engine-eq">{{ $t('ui.settingsExtras.manualEq') }}</label>
        <p>{{ $t('ui.settingsExtras.manualEqDescription') }}</p>
      </div>
      <q-toggle
        id="settings-engine-eq"
        :model-value="audioEngineConfig.eqEnabled"
        :disable="!audioEngineConfig.enabled"
        color="primary"
        @update:model-value="setManualEqEnabled"
      />
    </div>

    <div class="audio-engine__presets" :class="{ 'audio-engine__disabled': !audioEngineConfig.enabled || !audioEngineConfig.eqEnabled }">
      <button
        v-for="preset in audioEnginePresets"
        :key="preset.value"
        type="button"
        class="settings-option"
        :class="{ 'settings-option--active': audioEngineActivePreset === preset.value }"
        :aria-pressed="audioEngineActivePreset === preset.value"
        :disabled="!audioEngineConfig.enabled || !audioEngineConfig.eqEnabled"
        @click="applyAudioEnginePreset(preset.value)"
      >{{ $t(`settings.ui.optionLabels.eqPresets.${preset.value}`) }}</button>
    </div>

    <div class="audio-engine__equalizer" :class="{ 'audio-engine__disabled': !audioEngineConfig.enabled || !audioEngineConfig.eqEnabled }">
      <label v-for="(band, index) in audioEngineBands" :key="band.frequency" class="audio-engine__band">
        <output>{{ Number(audioEngineConfig.gains[index]).toFixed(1) }}</output>
        <input
          v-model.number="audioEngineConfig.gains[index]"
          type="range"
          min="-12"
          max="12"
          step="0.5"
          :disabled="!audioEngineConfig.enabled || !audioEngineConfig.eqEnabled"
          :aria-label="$t('ui.settingsExtras.hertzGain', { frequency: band.frequency })"
        />
        <span>{{ band.label }}</span>
      </label>
    </div>

    <div class="audio-engine__control-grid" style="display: flex; justify-content: space-around; align-items: flex-start; padding: 16px 8px;">
      <div class="audio-engine__control-item">
        <NeumorphicKnob
          v-model="audioEngineConfig.preampDb"
          :min="-12"
          :max="6"
          :step="0.5"
          :default-value="0"
          :label="$t('ui.settingsExtras.preamp')"
          unit="dB"
          :size="50"
          :disabled="!audioEngineConfig.enabled || !audioEngineConfig.eqEnabled"
        />
      </div>
      <div class="audio-engine__control-item">
        <NeumorphicKnob
          v-model="audioEngineConfig.q"
          :min="0.4"
          :max="2.4"
          :step="0.1"
          :default-value="1.4"
          :label="$t('ui.settingsExtras.bandWidth')"
          unit="Q"
          :size="50"
          :disabled="!audioEngineConfig.enabled"
        />
      </div>
      <div class="audio-engine__control-item">
        <NeumorphicKnob
          v-model="audioEngineConfig.balance"
          :min="-1"
          :max="1"
          :step="0.05"
          :default-value="0"
          :label="$t('ui.settingsExtras.balance')"
          unit="Pan"
          :size="50"
          :disabled="!audioEngineConfig.enabled"
        />
      </div>
    </div>

    <div class="settings-row">
      <div class="settings-row__copy">
        <label for="settings-engine-leveling">{{ $t('ui.settingsExtras.dynamicLeveling') }}</label>
        <p>{{ $t('ui.settingsExtras.dynamicLevelingDescription') }}</p>
      </div>
      <q-toggle id="settings-engine-leveling" v-model="volumeNormalizationEnabled" color="primary" />
    </div>

    <div class="settings-row settings-row--slider">
      <div class="settings-row__copy">
        <label for="settings-output-gain">{{ $t('ui.settingsExtras.globalGain') }}</label>
        <p>{{ $t('ui.settingsExtras.globalGainDescription') }}</p>
      </div>
      <div class="settings-slider">
        <q-slider
          id="settings-output-gain"
          v-model="audioEngineConfig.outputGainDb"
          :min="-24"
          :max="6"
          :step="0.5"
          color="primary"
        />
        <output>{{ Number(audioEngineConfig.outputGainDb).toFixed(1) }} dB</output>
      </div>
    </div>

    <div class="settings-row settings-row--slider" :class="{ 'settings-row--disabled': !activeTrack || !audioEngineConfig.enabled }">
      <div class="settings-row__copy">
        <label for="settings-track-gain">{{ $t('ui.settingsExtras.trackGain') }}</label>
        <p>{{ activeTrack ? $t('ui.settingsExtras.rememberedFor', { title: activeTrack.title }) : $t('ui.settingsExtras.playToSetGain') }}</p>
      </div>
      <div class="settings-slider">
        <q-slider
          id="settings-track-gain"
          :model-value="activeTrackGainDb"
          :min="-12"
          :max="12"
          :step="0.5"
          :disable="!activeTrack || !audioEngineConfig.enabled"
          color="primary"
          @update:model-value="setActiveTrackGain"
        />
        <output>{{ Number(activeTrackGainDb).toFixed(1) }} dB</output>
      </div>
    </div>

    <div class="settings-row audio-engine__output-row">
      <div class="settings-row__copy">
        <label for="settings-audio-output">{{ $t('ui.settingsExtras.outputDevice') }}</label>
        <p>{{ $t('ui.settingsExtras.outputDeviceDescription') }}</p>
      </div>
      <select id="settings-audio-output" v-model="audioEngineConfig.outputDeviceId" class="audio-engine__select" :disabled="audioOutputLoading">
        <option v-for="device in audioOutputDevices" :key="device.deviceId" :value="device.deviceId">{{ device.deviceId === 'default' ? $t('ui.settingsExtras.systemDefault') : device.label }}</option>
      </select>
    </div>

    <div class="audio-engine__actions">
      <button type="button" class="settings-button" @click="exportAudioEngineProfile"><q-icon name="download" />{{ $t('ui.settingsExtras.exportProfile') }}</button>
      <button type="button" class="settings-button" @click="profileInput?.click()"><q-icon name="upload" />{{ $t('ui.settingsExtras.importProfile') }}</button>
      <button type="button" class="settings-link-button settings-link-button--danger" @click="resetAudioEngine"><q-icon name="restart_alt" />{{ $t('ui.settingsExtras.resetEngine') }}</button>
      <input ref="profileInput" class="audio-engine__file" type="file" accept="application/json,.json" @change="importProfile" />
      <p v-if="audioEngineMessage" class="audio-engine__message" aria-live="polite">{{ audioEngineMessage }}</p>
    </div>
  </section>
</template>
