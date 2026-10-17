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
import AudioEngineSection from './AudioEngineSection.vue';
import DataSavingSection from './DataSavingSection.vue';
import BackupRestoreSection from './BackupRestoreSection.vue';
import DiagnosticsSection from './DiagnosticsSection.vue';
import LastfmSection from './LastfmSection.vue';
import QobuzSection from './QobuzSection.vue';
import SpotifySection from './SpotifySection.vue';
import SongCacheSection from './SongCacheSection.vue';
import ArtistPacksSection from './ArtistPacksSection.vue';
import VeyraAccountSection from './VeyraAccountSection.vue';
import NavigationLayoutSection from './NavigationLayoutSection.vue';
import { computed, ref, watch } from 'vue';
import { useI18n } from '../../app/i18n/index.js';

export default {
  name: 'SettingsView',
  components: { AudioEngineSection, DataSavingSection, BackupRestoreSection, DiagnosticsSection, LastfmSection, QobuzSection, SpotifySection, SongCacheSection, ArtistPacksSection, VeyraAccountSection, NavigationLayoutSection },
  props: { app: { type: Object, required: true } },
  setup(props) {
    const { locale, setLocale, availableLocales, t } = useI18n();
    const settingOptionLabel = (group, option) => t(`settings.ui.optionLabels.${group}.${option.value}`);
    const layoutPresetDescription = computed(() => t(`settings.ui.layoutDescriptions.${props.app.layoutPreset.value}`));
    const uiScaleDraft = ref(props.app.uiScale.value);
    watch(props.app.uiScale, (scale) => {
      uiScaleDraft.value = scale;
    });
    const applyUiScale = (scale) => {
      props.app.uiScale.value = scale;
    };

    const palettePresets = ['#3AE0FD', '#39FAE5', '#28F2FE', '#FD214C', '#FD3D3C'];

    return { ...props.app, app: props.app, layoutPresetDescription, uiScaleDraft, applyUiScale, locale, setLocale, availableLocales, palettePresets, settingOptionLabel };
  }
};
</script>

<template>
  <main class="settings-view">
    <nav class="settings-index" :aria-label="$t('settings.ui.settingsSections')">
      <a href="#settings-playback">
        <q-icon name="play_circle_outline" />
        <span>{{ $t('settings.ui.playbackTitle') }}</span>
      </a>
      <a href="#settings-audio-engine">
        <q-icon name="equalizer" />
        <span>{{ $t('settings.sections.audio') }}</span>
      </a>
      <a href="#settings-song-cache">
        <q-icon name="offline_pin" />
        <span>{{ $t('settings.sections.cache') }}</span>
      </a>
      <a href="#settings-data-saving">
        <q-icon name="data_saver_on" />
        <span>{{ $t('settings.sections.dataSaving') }}</span>
      </a>
      <a href="#settings-layout">
        <q-icon name="space_dashboard" />
        <span>{{ $t('settings.sections.layout') }}</span>
      </a>
      <a href="#settings-appearance">
        <q-icon name="palette" />
        <span>{{ $t('settings.ui.appearanceTitle') }}</span>
      </a>
      <a href="#settings-integrations">
        <q-icon name="extension" />
        <span>{{ $t('settings.ui.integrations') }}</span>
      </a>
      <a href="#settings-connect">
        <q-icon name="phonelink" />
        <span>{{ $t('settings.ui.connect') }}</span>
      </a>
      <a href="#settings-application">
        <q-icon name="info_outline" />
        <span>{{ $t('settings.ui.application') }}</span>
      </a>
      <a v-if="proxyModeAvailable" href="#settings-network">
        <q-icon name="lan" />
        <span>{{ $t('settings.ui.network') }}</span>
      </a>
      <a href="#settings-diagnostics">
        <q-icon name="fact_check" />
        <span>{{ $t('settings.ui.diagnostics') }}</span>
      </a>
      <a href="#settings-backup">
        <q-icon name="archive" />
        <span>{{ $t('settings.ui.backup') }}</span>
      </a>
    </nav>

    <div class="settings-content">
      <section id="settings-playback" class="settings-section" aria-labelledby="settings-playback-title">
        <div class="settings-section__heading">
          <h2 id="settings-playback-title">{{ $t('settings.ui.playbackTitle') }}</h2>
          <p>{{ $t('settings.ui.playbackDescription') }}</p>
        </div>

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-autoplay">{{ $t('settings.ui.autoplay') }}</label>
            <p>{{ $t('settings.ui.autoplayDescription') }}</p>
          </div>
          <q-toggle id="settings-autoplay" v-model="autoplayEnabled" color="primary" :aria-label="$t('settings.ui.autoplay')" />
        </div>

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-save-playback-state">{{ $t('settings.ui.saveQueue') }}</label>
            <p>{{ $t('settings.ui.saveQueueDescription') }}</p>
          </div>
          <q-toggle id="settings-save-playback-state" v-model="playbackStatePersistenceEnabled" color="primary" :aria-label="$t('settings.ui.saveQueue')" />
        </div>

        <div class="settings-row settings-row--options">
          <div class="settings-row__copy">
            <label id="settings-sponsorblock-label">{{ $t('settings.ui.skipNonMusic') }}</label>
            <p>{{ $t('settings.ui.skipNonMusicDescription') }}</p>
          </div>
          <div class="settings-option-group" role="group" aria-labelledby="settings-sponsorblock-label">
            <button
              v-for="option in sponsorBlockModeOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': sponsorBlockMode === option.value }"
              :aria-pressed="sponsorBlockMode === option.value"
              @click="sponsorBlockMode = option.value"
            >
              {{ settingOptionLabel('sponsor', option) }}
            </button>
          </div>
        </div>

        <div class="settings-row settings-row--options">
          <div class="settings-row__copy">
            <label id="settings-queue-layout-label">{{ $t('settings.ui.queueStyle') }}</label>
            <p>{{ $t('settings.ui.queueStyleDescription') }}</p>
          </div>
          <div class="settings-option-group" role="group" aria-labelledby="settings-queue-layout-label">
            <button
              v-for="option in queueLayoutOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': queueLayout === option.value }"
              :aria-pressed="queueLayout === option.value"
              @click="queueLayout = option.value"
            >
              {{ settingOptionLabel('queue', option) }}
            </button>
          </div>
        </div>

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-crossfade">{{ $t('settings.ui.crossfade') }}</label>
            <p>{{ $t('settings.ui.crossfadeDescription') }}</p>
          </div>
          <q-toggle id="settings-crossfade" v-model="crossfadeEnabled" color="primary" :aria-label="$t('settings.ui.crossfade')" />
        </div>

        <div class="settings-row settings-row--options" :class="{ 'settings-row--disabled': !crossfadeEnabled }">
          <div class="settings-row__copy">
            <label id="settings-crossfade-mode-label">{{ $t('settings.ui.crossfadeMode') }}</label>
            <p>{{ $t('settings.ui.crossfadeModeDescription') }}</p>
          </div>
          <div class="settings-option-group" role="group" aria-labelledby="settings-crossfade-mode-label">
            <button
              v-for="option in crossfadeModeOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': crossfadeMode === option.value }"
              :aria-pressed="crossfadeMode === option.value"
              :disabled="!crossfadeEnabled"
              @click="setCrossfadeMode(option.value)"
            >
              {{ settingOptionLabel('crossfade', option) }}
            </button>
          </div>
        </div>

        <div class="settings-row settings-row--slider" :class="{ 'settings-row--disabled': !crossfadeEnabled }">
          <div class="settings-row__copy">
            <label for="settings-crossfade-length">{{ $t('settings.ui.crossfadeLength') }}</label>
            <p>{{ $t('settings.ui.crossfadeLengthDescription') }}</p>
          </div>
          <div class="settings-slider">
            <q-slider
              id="settings-crossfade-length"
              v-model="crossfadeSeconds"
              :min="1"
              :max="12"
              :step="1"
              :disable="!crossfadeEnabled"
              color="primary"
              :aria-label="$t('settings.ui.crossfadeLength')"
            />
            <output for="settings-crossfade-length">{{ crossfadeSeconds }} {{ $t('settings.ui.seconds') }}</output>
          </div>
        </div>

        <div id="settings-sleep-timer" class="settings-row settings-row--sleep-timer">
          <div class="settings-row__copy">
            <label id="settings-sleep-timer-label">{{ $t('settings.ui.sleepTimer') }}</label>
            <p aria-live="polite">{{ sleepTimerSummary }}</p>
          </div>
          <div class="sleep-timer-options" role="group" aria-labelledby="settings-sleep-timer-label">
            <button
              v-for="option in sleepTimerOptions"
              :key="option.value"
              type="button"
              class="sleep-timer-option"
              :class="{ 'sleep-timer-option--active': sleepTimerMode === option.value }"
              :aria-pressed="sleepTimerMode === option.value"
              @click="startSleepTimer(option.value)"
            >
              {{ $t('settings.ui.timerMinutes', { minutes: option.minutes }) }}
            </button>
            <button
              type="button"
              class="sleep-timer-option sleep-timer-option--track"
              :class="{ 'sleep-timer-option--active': sleepTimerMode === 'end-track' }"
              :aria-pressed="sleepTimerMode === 'end-track'"
              :disabled="!activeTrack"
              @click="startSleepTimer('end-track')"
            >
              {{ $t('settings.ui.endSong') }}
            </button>
            <button
              v-if="sleepTimerActive"
              type="button"
              class="sleep-timer-option sleep-timer-option--cancel"
              @click="cancelSleepTimer"
            >
              {{ $t('common.cancel') }}
            </button>
          </div>
        </div>
      </section>

      <SongCacheSection :app="app" />
      <DataSavingSection :app="app" />
      <ArtistPacksSection :app="app" />

      <AudioEngineSection :app="app" />

      <NavigationLayoutSection :app="app" />

      <section id="settings-appearance" class="settings-section" aria-labelledby="settings-appearance-title">
        <div class="settings-section__heading">
          <h2 id="settings-appearance-title">{{ $t('settings.ui.appearanceTitle') }}</h2>
          <p>{{ $t('settings.ui.appearanceDescription') }}</p>
        </div>

        <div class="settings-row settings-row--slider">
          <div class="settings-row__copy">
            <label for="settings-ui-scale">{{ $t('settings.ui.textSize') }}</label>
            <p>{{ $t('settings.ui.textSizeDescription') }}</p>
          </div>
          <div class="settings-slider">
            <q-slider id="settings-ui-scale" v-model="uiScaleDraft" :min="0.85" :max="1.5" :step="0.05" color="primary" :aria-label="$t('settings.ui.textSize')" @change="applyUiScale" />
            <output for="settings-ui-scale">{{ Math.round(uiScaleDraft * 100) }}%</output>
          </div>
        </div>

        <div class="settings-row settings-row--options">
          <div class="settings-row__copy">
            <label id="settings-layout-preset-label">{{ $t('settings.ui.interfaceDesign') }}</label>
            <p>{{ layoutPresetDescription }}</p>
          </div>
          <div class="settings-option-group" role="group" aria-labelledby="settings-layout-preset-label">
            <button
              v-for="option in layoutPresetOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': layoutPreset === option.value }"
              :aria-pressed="layoutPreset === option.value"
              @click="layoutPreset = option.value"
            >
              {{ settingOptionLabel('layout', option) }}
            </button>
          </div>
        </div>

        <div class="settings-row settings-row--options">
          <div class="settings-row__copy">
            <label id="settings-graphics-mode-label">{{ $t('settings.ui.graphicsMode') }}</label>
            <p id="settings-graphics-mode-description">
              {{ $t('settings.ui.graphicsModeDescription') }}
            </p>
            <p v-if="graphicsModePlatform === 'linux' && graphicsModeIntegratedSupported">
              {{ $t('settings.ui.linuxGpu') }}
            </p>
            <p v-else-if="graphicsModeStatusReady && !graphicsModeIntegratedSupported">
              {{ $t('settings.ui.gpuUnavailable') }}
            </p>
            <p v-if="graphicsModeMessage" aria-live="polite">{{ graphicsModeMessage }}</p>
          </div>
          <div
            class="settings-option-group"
            role="group"
            aria-labelledby="settings-graphics-mode-label"
            aria-describedby="settings-graphics-mode-description"
          >
            <button
              v-for="option in graphicsModeOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': graphicsMode === option.value }"
              :aria-pressed="graphicsMode === option.value"
              :disabled="option.value === 'integrated' && graphicsModeStatusReady && !graphicsModeIntegratedSupported"
              @click="graphicsMode = option.value"
            >
              {{ settingOptionLabel('graphics', option) }}
            </button>
          </div>
        </div>

        <div v-if="graphicsModeRestartRequired" class="settings-action-row">
          <div class="settings-row__copy">
            <span>{{ $t('settings.ui.restartRequired') }}</span>
            <p>{{ $t('settings.ui.restartDescription', { mode: settingOptionLabel('graphics', { value: graphicsMode }) }) }}</p>
          </div>
          <button type="button" class="settings-button" @click="restartVeyra">
            <q-icon name="restart_alt" />
            {{ $t('settings.ui.restartApp') }}
          </button>
        </div>

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-immersive">{{ $t('settings.ui.immersive') }}</label>
            <p>{{ $t('settings.ui.immersiveDescription') }}</p>
          </div>
          <q-toggle id="settings-immersive" v-model="immersiveBackgroundsEnabled" color="primary" :aria-label="$t('settings.ui.immersive')" />
        </div>

        <div class="settings-row settings-row--options" :class="{ 'settings-row--disabled': !immersiveBackgroundsEnabled }">
          <div class="settings-row__copy">
            <label id="settings-background-intensity-label">{{ $t('settings.ui.backgroundIntensity') }}</label>
            <p>{{ $t('settings.ui.backgroundIntensityDescription') }}</p>
          </div>
          <div class="settings-option-group" role="group" aria-labelledby="settings-background-intensity-label">
            <button
              v-for="option in immersiveBackgroundIntensityOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': immersiveBackgroundIntensity === option.value }"
              :aria-pressed="immersiveBackgroundIntensity === option.value"
              :disabled="!immersiveBackgroundsEnabled"
              @click="immersiveBackgroundIntensity = option.value"
            >
              {{ settingOptionLabel('intensity', option) }}
            </button>
          </div>
        </div>

        <div class="settings-row settings-row--options" :class="{ 'settings-row--disabled': !immersiveBackgroundsEnabled }">
          <div class="settings-row__copy">
            <label id="settings-background-motion-label">{{ $t('settings.ui.backgroundMotion') }}</label>
            <p>{{ $t('settings.ui.backgroundMotionDescription') }}</p>
          </div>
          <div class="settings-option-group" role="group" aria-labelledby="settings-background-motion-label">
            <button
              v-for="option in immersiveBackgroundMotionOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': immersiveBackgroundMotion === option.value }"
              :aria-pressed="immersiveBackgroundMotion === option.value"
              :disabled="!immersiveBackgroundsEnabled"
              @click="immersiveBackgroundMotion = option.value"
            >
              {{ settingOptionLabel('motion', option) }}
            </button>
          </div>
        </div>

        <div class="settings-row settings-row--options">
          <div class="settings-row__copy">
            <label id="settings-accent-source-label">{{ $t('settings.ui.accentColor') }}</label>
            <p>{{ $t('settings.ui.accentDescription') }}</p>
          </div>
          <div class="settings-option-group" role="group" aria-labelledby="settings-accent-source-label">
            <button
              v-for="option in accentColorSourceOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': accentColorSource === option.value }"
              :aria-pressed="accentColorSource === option.value"
              @click="accentColorSource = option.value"
            >
              {{ settingOptionLabel('accent', option) }}
            </button>
            <label v-if="accentColorSource === 'custom'" class="settings-color-control" for="settings-custom-accent">
              <span>{{ $t('settings.ui.chooseColor') }}</span>
              <input id="settings-custom-accent" v-model="customAccentColor" type="color" :aria-label="$t('settings.ui.customAccent')" />
            </label>
            <div v-if="accentColorSource === 'custom'" class="settings-palette-presets" role="group" :aria-label="$t('settings.ui.palettePresets')">
              <button
                v-for="(hex, idx) in palettePresets"
                :key="hex"
                type="button"
                class="settings-palette-swatch"
                :class="{ 'settings-palette-swatch--active': (customAccentColor || '').toLowerCase() === hex.toLowerCase() }"
                :style="{ backgroundColor: hex }"
                :title="$t('settings.ui.paletteColor', { number: idx + 1, hex })"
                :aria-label="$t('settings.ui.paletteColor', { number: idx + 1, hex })"
                @click="customAccentColor = hex"
              />
            </div>
          </div>
        </div>

        <div class="settings-row settings-row--options">
          <div class="settings-row__copy">
            <label id="settings-theme-label">{{ $t('settings.ui.theme') }}</label>
            <p>{{ $t('settings.ui.themeDescription') }}</p>
          </div>
          <div class="settings-option-group" role="group" aria-labelledby="settings-theme-label">
            <button
              v-for="option in themePreferenceOptions"
              :key="option.value"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': themePreference === option.value }"
              :aria-pressed="themePreference === option.value"
              @click="themePreference = option.value"
            >
              {{ settingOptionLabel('theme', option) }}
            </button>
          </div>
        </div>

        <div class="settings-row settings-row--options">
          <div class="settings-row__copy">
            <label id="settings-language-label">{{ $t('settings.language.label') }}</label>
            <p>{{ $t('settings.language.description') }}</p>
          </div>
          <div class="settings-option-group" role="group" aria-labelledby="settings-language-label">
            <button
              v-for="opt in availableLocales"
              :key="opt.code"
              type="button"
              class="settings-option"
              :class="{ 'settings-option--active': locale === opt.code }"
              :aria-pressed="locale === opt.code"
              @click="setLocale(opt.code)"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>
      </section>

      <section id="settings-integrations" class="settings-section" aria-labelledby="settings-integrations-title">
        <div class="settings-section__heading">
          <h2 id="settings-integrations-title">{{ $t('settings.ui.integrations') }}</h2>
          <p>{{ $t('settings.ui.integrationsDescription') }}</p>
        </div>

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-discord">{{ $t('settings.ui.discord') }}</label>
            <p>{{ $t('settings.ui.discordDescription') }}</p>
          </div>
          <q-toggle id="settings-discord" v-model="discordRpcEnabled" color="primary" :aria-label="$t('settings.ui.discord')" />
        </div>

        <LastfmSection :app="app" />
        <SpotifySection :app="app" />
        <QobuzSection />
        <VeyraAccountSection :app="app" />

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-youtube-history">{{ $t('settings.ui.youtubeHistory') }}</label>
            <p>{{ $t('settings.ui.youtubeHistoryDescription') }}</p>
          </div>
          <q-toggle id="settings-youtube-history" v-model="youtubeHistoryEnabled" color="primary" :aria-label="$t('settings.ui.youtubeHistory')" />
        </div>
      </section>

      <section id="settings-connect" class="settings-section" aria-labelledby="settings-connect-title">
        <div class="settings-section__heading">
          <h2 id="settings-connect-title">{{ $t('settings.ui.connect') }}</h2>
          <p>{{ $t('settings.ui.connectDescription') }}</p>
        </div>

        <div class="settings-connect">
          <details class="settings-connect__qr-panel">
            <summary>
              <q-icon name="qr_code_2" />
              <span>{{ $t('settings.ui.showCameraQr') }}</span>
            </summary>
            <div class="settings-connect__qr" v-html="veyraConnect.qrSvg"></div>
          </details>
          <div class="settings-connect__copy">
            <span>{{ $t('settings.ui.pairingLink') }}</span>
            <p>{{ veyraConnect.serverUrl || $t('settings.ui.waitingBridge') }}</p>
            <div class="settings-connect__actions">
              <button type="button" class="settings-button" :disabled="!socket?.connected" @click="loadVeyraConnectInfo({ refresh: true })">
                <q-icon name="qr_code_2" />
                {{ $t('settings.ui.newQr') }}
              </button>
              <button type="button" class="settings-button" :disabled="!veyraConnect.pairUrl" @click="copyVeyraConnectLink">
                <q-icon name="content_copy" />
                {{ $t('settings.ui.copyAppLink') }}
              </button>
              <button type="button" class="settings-button" :disabled="!veyraConnect.webPairUrl" @click="copyVeyraConnectWebLink">
                <q-icon name="content_copy" />
                {{ $t('settings.ui.copyCameraLink') }}
              </button>
            </div>
            <p v-if="veyraConnectPairingMessage" class="settings-connect__message">{{ veyraConnectPairingMessage }}</p>
            <details v-if="veyraConnect.altWebPairUrls?.length" class="settings-connect__alternates">
              <summary>{{ $t('settings.ui.phoneAddressHelp') }}</summary>
              <p>
                {{ $t('settings.ui.networkAlternates') }}
              </p>
              <button
                v-for="url in veyraConnect.altWebPairUrls"
                :key="url"
                type="button"
                class="settings-button settings-connect__alternate"
                @click="copyVeyraConnectAltLink(url)"
              >
                <q-icon name="content_copy" />
                {{ url }}
              </button>
            </details>
          </div>
        </div>

        <div v-if="veyraConnect.pending.length" class="settings-connect-list">
          <div
            v-for="request in veyraConnect.pending"
            :key="request.id"
            class="settings-action-row settings-action-row--connect"
          >
            <div class="settings-row__copy">
              <span>{{ request.name }}</span>
              <p>{{ $t('settings.ui.approvePhone') }}</p>
            </div>
            <div class="settings-connect__actions">
              <button type="button" class="settings-button" @click="approveVeyraConnectPairing(request.id)">{{ $t('settings.ui.approve') }}</button>
              <button type="button" class="settings-link-button settings-link-button--danger" @click="rejectVeyraConnectPairing(request.id)">{{ $t('settings.ui.reject') }}</button>
            </div>
          </div>
        </div>

        <div class="settings-connect-list">
          <div
            v-for="device in veyraConnect.devices"
            :key="device.id"
            class="settings-action-row settings-action-row--connect"
          >
            <div class="settings-row__copy">
              <span>{{ device.name }}</span>
              <p>{{ device.connected ? $t('settings.ui.connectedNow') : $t('settings.ui.notConnected') }}</p>
            </div>
            <button type="button" class="settings-link-button settings-link-button--danger" @click="revokeVeyraConnectDevice(device.id)">
              {{ $t('settings.ui.revoke') }}
            </button>
          </div>
          <div v-if="!veyraConnect.devices.length && !veyraConnect.pending.length" class="settings-connect-empty">
            {{ $t('settings.ui.noPhones') }}
          </div>
        </div>
      </section>

      <section id="settings-application" class="settings-section" aria-labelledby="settings-application-title">
        <div class="settings-section__heading">
          <h2 id="settings-application-title">{{ $t('settings.ui.application') }}</h2>
        </div>

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-close-to-tray">{{ $t('settings.ui.closeToTray') }}</label>
            <p>{{ $t('settings.ui.closeToTrayDescription') }}</p>
          </div>
          <q-toggle id="settings-close-to-tray" v-model="closeToTrayEnabled" color="primary" :aria-label="$t('settings.ui.closeToTray')" />
        </div>

        <div class="settings-action-row">
          <div class="settings-row__copy">
            <span>{{ $t('settings.ui.updates') }}</span>
            <p>{{ updateState.content?.message || updateState.message || $t('settings.ui.updateHint') }}</p>
          </div>
          <button type="button" class="settings-button" :disabled="updateState.status === 'checking'" @click="openUpdateDialog({ check: true, checkContent: true })">
            <q-icon name="system_update_alt" />
            {{ $t('settings.ui.updates') }}
          </button>
        </div>

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-beta-channel">{{ $t('settings.ui.betaChannel') }}</label>
            <p>{{ $t('settings.ui.betaDescription') }}</p>
          </div>
          <q-toggle id="settings-beta-channel" v-model="updateChannelBetaToggle" color="primary" :aria-label="$t('settings.ui.betaChannel')" />
        </div>

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-keep-old-versions">{{ $t('settings.ui.keepOldVersions') }}</label>
            <p>{{ $t('settings.ui.keepOldVersionsDescription') }}</p>
          </div>
          <q-toggle id="settings-keep-old-versions" v-model="keepOldVersions" color="primary" :aria-label="$t('settings.ui.keepOldVersions')" />
        </div>

        <div class="settings-actions">
          <button type="button" class="settings-link-button" @click="reopenWelcomeWizard">
            <q-icon name="auto_awesome" />
            {{ $t('settings.ui.reopenWelcome') }}
          </button>
          <button type="button" class="settings-link-button" @click="aboutDialogOpen = true">
            <q-icon name="info_outline" />
            {{ $t('settings.ui.about') }}
          </button>
          <button type="button" class="settings-link-button settings-link-button--danger" @click="resetUserPreferences">
            <q-icon name="restart_alt" />
            {{ $t('settings.ui.restoreDefaults') }}
          </button>
        </div>
      </section>

      <section
        v-if="proxyModeAvailable"
        id="settings-network"
        class="settings-section"
        aria-labelledby="settings-network-title"
      >
        <div class="settings-section__heading">
          <h2 id="settings-network-title">{{ $t('settings.ui.network') }}</h2>
          <p>{{ $t('settings.ui.networkDescription') }}</p>
        </div>

        <div class="settings-row">
          <div class="settings-row__copy">
            <label for="settings-proxy-bypass">{{ $t('settings.ui.proxy') }}</label>
            <p>{{ $t('settings.ui.proxyDescription') }}</p>
          </div>
          <q-toggle id="settings-proxy-bypass" v-model="proxyBypassToggle" color="primary" :aria-label="$t('settings.ui.proxy')" />
        </div>

        <p v-if="proxyModeMessage" class="settings-connect__message">{{ proxyModeMessage }}</p>
      </section>

      <DiagnosticsSection :app="app" />
      <BackupRestoreSection :app="app" />
    </div>
  </main>
</template>
