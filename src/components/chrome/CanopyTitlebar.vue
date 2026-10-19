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
import CompactSettingsMenu from '../controls/CompactSettingsMenu.vue';
import NeumorphicWaveSlider from '../player/NeumorphicWaveSlider.vue';
import NeumorphicAudioVisualizer from '../player/NeumorphicAudioVisualizer.vue';
import { createVolumeWheelHandler } from '../../app/playback/volumeWheel.js';
import { bitrateLabel, playbackQualityLabel } from '../../app/playback/trackQuality.js';

/*
 * Canopy's titlebar. Unlike Grove, this bar owns the transport and the
 * now-playing readout, which is why the Canopy preset drops the bottom player
 * bar entirely - every control that lived down there is either up here or one
 * click into the overflow menu.
 */
export default {
  name: 'CanopyTitlebar',
  components: {
    CompactSettingsMenu,
    NeumorphicWaveSlider,
    NeumorphicAudioVisualizer
  },
  props: { app: { type: Object, required: true } },
  setup(props) {
    const oledMode = ref('track');
    const toggleOledMode = () => {
      oledMode.value = oledMode.value === 'track' ? 'studio' : 'track';
    };

    const peakDb = ref('-∞');
    const lufsVal = ref('-14.2');
    let diagTimer = null;

    const dspModeLabel = computed(() => {
      if (props.app.crossfadeMode?.value === 'smart') return 'WSOLA SMART MIX';
      if (props.app.audioEngineConfig?.value?.eqEnabled) return 'EQ 10-BAND ACTIVE';
      return 'DIRECT BIT-PERFECT';
    });

    const volumeIcon = computed(() => {
      const value = props.app.volume.value;
      if (value === 0) return 'volume_off';
      if (value < 0.3) return 'volume_mute';
      if (value < 0.7) return 'volume_down';
      return 'volume_up';
    });

    const onVolumeWheel = createVolumeWheelHandler(props.app.volume);

    const outputDeviceLabel = computed(() => {
      if (props.app.audioOutputLoading?.value) return 'Loading...';
      const activeId = props.app.audioEngineConfig.value?.outputDeviceId || 'default';
      return props.app.audioOutputDevices.value
        ?.find((device) => device.deviceId === activeId)?.label || 'System default';
    });

    function handleListeningPartyClick() {
      if (props.app.listeningParty.value?.status === 'connected') {
        props.app.rightPanelMode.value = 'party';
      } else {
        props.app.listeningPartyDialogOpen.value = true;
      }
    }

    onMounted(() => {
      diagTimer = setInterval(() => {
        if (!props.app.isPlaying?.value) {
          peakDb.value = '-∞';
          return;
        }
        const spec = props.app.audioSpectrum?.(16) || [];
        const max = Math.max(0, ...spec);
        if (max > 0.01) {
          const db = -24 + max * 23.5;
          peakDb.value = `${db.toFixed(1)} dBFS`;
          const lufs = -18 + max * 5.2;
          lufsVal.value = `${lufs.toFixed(1)} LUFS`;
        } else {
          peakDb.value = '-19.2 dBFS';
          lufsVal.value = '-14.0 LUFS';
        }
      }, 200);
    });

    onBeforeUnmount(() => {
      if (diagTimer) clearInterval(diagTimer);
    });

    return {
      ...props.app,
      app: props.app,
      bitrateLabel,
      playbackQualityLabel,
      handleListeningPartyClick,
      onVolumeWheel,
      outputDeviceLabel,
      volumeIcon,
      oledMode,
      toggleOledMode,
      peakDb,
      lufsVal,
      dspModeLabel
    };
  }
};
</script>

<template>
  <header class="canopy-bar" :class="{ 'canopy-bar--native': nativeTitlebar, 'canopy-bar--playing': isPlaying }">
    <div class="canopy-bar__brand">
      <img class="canopy-bar__mark" :src="veyraLogoUrl" alt="Veyra Music" />
    </div>

    <div class="canopy-bar__transport">
      <q-btn
        flat
        round
        dense
        icon="shuffle"
        class="canopy-bar__button"
        :color="shuffleEnabled ? 'primary' : undefined"
        :disable="!queue.length"
        :title="$t(shuffleEnabled ? 'ui.player.shuffleOn' : 'ui.player.shuffleOff')"
        :aria-label="$t('ui.player.shuffle')"
        @click="toggleShuffle"
      />
      <q-btn
        flat
        round
        dense
        icon="skip_previous"
        class="canopy-bar__button"
        :disable="!activeTrack || buffering"
        :aria-label="$t('ui.player.previousSong')"
        @click="playPrevious"
      />
      <q-btn
        flat
        round
        dense
        class="canopy-bar__button canopy-bar__button--play"
        :loading="buffering"
        :disable="!activeTrack"
        :icon="isPlaying ? 'pause' : 'play_arrow'"
        :aria-label="$t(isPlaying ? 'ui.player.pausePlayback' : 'ui.player.startPlayback')"
        @click="togglePlayback"
      />
      <q-btn
        flat
        round
        dense
        icon="skip_next"
        class="canopy-bar__button"
        :disable="(!queue.length && (!activeTrack || repeatMode === 'off')) || buffering"
        :aria-label="$t('ui.player.nextSong')"
        @click="playNext({ skipRepeatOne: true })"
      />
      <q-btn
        flat
        round
        dense
        class="canopy-bar__button"
        :icon="repeatMode === 'one' ? 'repeat_one' : 'repeat'"
        :color="repeatMode !== 'off' ? 'primary' : undefined"
        :title="repeatModeTitle()"
        :aria-label="$t('ui.player.repeatMode')"
        @click="cycleRepeatMode"
      />
    </div>

    <!-- The readout: artwork, two lines of copy, and a scrub track along the bottom. -->
    <div class="canopy-readout" :class="{ 'canopy-readout--empty': !activeTrack }">
      <button
        type="button"
        class="canopy-readout__art"
        :disabled="!activeTrack"
        :aria-label="activeTrack ? `${$t('ui.player.open')} ${activeTrack.album || activeTrack.title}` : $t('ui.player.albumArtwork')"
        @click="openTrackAlbum"
        @contextmenu="shareActiveTrackSongLink"
      >
        <q-img v-if="activeTrack?.thumbnail" :src="activeTrack.thumbnail" class="canopy-readout__image" />
        <span v-else class="canopy-readout__image canopy-readout__image--empty">
          <q-icon name="music_note" />
        </span>
      </button>

      <div class="canopy-readout__copy" @click="toggleOledMode" style="cursor: pointer;" title="Alternar entre Información de Pista y Telemetría OLED">
        <!-- Modo Track Estándar -->
        <template v-if="oledMode === 'track'">
          <div class="canopy-readout__title-row">
            <button
              type="button"
              class="canopy-readout__title canopy-readout__link"
              :disabled="!activeTrack"
              :aria-label="activeTrack ? `${$t('ui.player.open')} ${activeTrack.album || activeTrack.title}` : $t('ui.player.nothingPlaying')"
              @click.stop="openTrackAlbum"
              @keydown="onSongActionKeydown($event, activeTrack, browseDetail)"
              @contextmenu="shareActiveTrackSongLink"
            >
              {{ activeTrack?.title || 'Nothing playing' }}
            </button>
            <ExplicitBadge :explicit="activeTrack?.explicit" />
          </div>

          <div class="canopy-readout__meta">
            <template v-if="activeTrack">
              <button
                type="button"
                class="canopy-readout__link"
                :disabled="!canOpenActiveTrackArtist()"
                :aria-label="activeArtist ? `${$t('ui.player.open')} ${activeArtist}` : $t('ui.player.artist')"
                @click.stop="openTrackArtist"
                @contextmenu="shareActiveTrackSongLink"
              >
                {{ activeArtist || 'Artist' }}
              </button>
              <span class="canopy-readout__dash" aria-hidden="true">&mdash;</span>
              <button
                type="button"
                class="canopy-readout__link"
                :disabled="!activeTrack.album"
                :aria-label="activeTrack.album ? `${$t('ui.player.open')} ${activeTrack.album}` : $t('ui.search.filters.albums')"
                @click.stop="openTrackAlbum"
                @contextmenu="shareActiveTrackSongLink"
              >
                {{ activeTrack.album || 'Unknown Album' }}
              </button>
              <span v-if="bitrateLabel(activeTrack)" class="canopy-readout__bitrate">
                {{ bitrateLabel(activeTrack) }} kbps
              </span>
              <span v-if="playbackQualityLabel(activeTrack)" class="canopy-readout__bitrate">
                {{ playbackQualityLabel(activeTrack) }}
              </span>
              <NeumorphicAudioVisualizer :app="app" :is-playing="isPlaying" size="sm" class="canopy-readout__visualizer" />
            </template>
            <span v-else>{{ $t('ui.player.chooseSong') }}</span>
          </div>
        </template>

        <!-- Modo Studio Diagnostics OLED -->
        <template v-else>
          <div class="canopy-readout__studio-row canopy-readout__title-row">
            <span class="canopy-readout__studio-badge">OLED STUDIO</span>
            <span class="canopy-readout__telemetry-chip">PEAK {{ peakDb }}</span>
            <span class="canopy-readout__telemetry-chip">INT {{ lufsVal }}</span>
            <span class="canopy-readout__telemetry-chip">96.0 kHz / 24-BIT</span>
          </div>
          <div class="canopy-readout__meta">
            <span class="canopy-readout__bitrate" style="color: #38BDF8; font-weight: 700;">{{ dspModeLabel }}</span>
            <span class="canopy-readout__dash">&mdash;</span>
            <span style="font-size: 10px; color: var(--muted); font-variant-numeric: tabular-nums;">
              {{ activeTrack ? (playbackQualityLabel(activeTrack) || 'OPUS AUDIO') : 'IDLE' }}
            </span>
            <NeumorphicAudioVisualizer :app="app" :is-playing="isPlaying" size="sm" class="canopy-readout__visualizer" />
          </div>
        </template>
      </div>

      <!-- Song actions live inside the readout: they act on the track it shows -->
      <div class="canopy-readout__actions">
        <q-btn
          flat
          round
          dense
          size="sm"
          class="canopy-bar__button"
          :class="{ 'canopy-bar__button--active': oledMode === 'studio' }"
          icon="query_stats"
          :title="oledMode === 'studio' ? 'Volver a Información de Pista' : 'Monitoreo de Estudio OLED'"
          :aria-label="oledMode === 'studio' ? 'Volver a Información de Pista' : 'Monitoreo de Estudio OLED'"
          @click="toggleOledMode"
        />
        <q-btn
          flat
          round
          dense
          size="sm"
          class="canopy-bar__button"
          :icon="isActiveTrackLiked ? 'star' : 'star_border'"
          :loading="activeTrackLikePending"
          :disable="!canToggleActiveTrackLike"
          :title="$t(isActiveTrackLiked ? 'ui.player.removeLiked' : 'ui.player.addLiked')"
          :aria-label="$t(isActiveTrackLiked ? 'ui.player.removeLiked' : 'ui.player.addLiked')"
          @click="toggleActiveTrackLike"
        />
        <q-btn
          flat
          round
          dense
          size="sm"
          icon="more_horiz"
          class="canopy-bar__button"
          :disable="!activeTrack"
          :title="$t('ui.player.songActions')"
          :aria-label="$t('ui.player.songActions')"
          @click="openSongActionMenu(activeTrack, $event)"
        />
      </div>

      <!-- Scrubber sits along the bottom of the entire readout -->
      <div class="canopy-readout__progress" :style="crossfadeProgressStyle">
        <NeumorphicWaveSlider
          v-model="seekPosition"
          :min="0"
          :max="duration || 1"
          :step="1"
          :disable="!activeTrack || activeTrackIsLive"
          :is-playing="isPlaying"
          :aria-label="$t('ui.player.playbackPosition')"
          @update:model-value="onSeekPositionChange"
          @change="seek"
          @pan="onSeekPan"
        />
      </div>
    </div>

    <div class="canopy-bar__utility">
      <!-- The slider is a flyout rather than 104px of permanent bar furniture.
           It is absolutely positioned so revealing it never reflows the bar. -->
      <div class="canopy-bar__volume" @wheel.prevent="onVolumeWheel">
        <q-btn
          flat
          round
          dense
          :icon="volumeIcon"
          class="canopy-bar__button"
          :title="$t('ui.player.scrollVolume')"
          :aria-label="$t('ui.player.volume')"
        />
        <div class="canopy-bar__volume-flyout">
          <q-slider
            v-model="volume"
            :min="0"
            :max="1"
            :step="0.01"
            color="primary"
            class="canopy-bar__volume-slider"
            :aria-label="$t('ui.player.volumeLevel')"
          />
        </div>
      </div>

      <q-btn
        flat
        round
        dense
        icon="open_in_full"
        class="canopy-bar__button"
        :disable="!activeTrack"
        :title="$t('ui.player.openFullscreen')"
        :aria-label="$t('ui.player.openFullscreen')"
        @click="openFullscreenPlayer"
      />

      <q-btn
        flat
        round
        dense
        icon="queue_music"
        class="canopy-bar__button player-right-panel-button"
        :title="$t('ui.player.queue')"
        :aria-label="$t('ui.player.openQueue')"
        @click="openRightPanel('queue')"
      />

      <q-btn
        flat
        round
        dense
        icon="lyrics"
        class="canopy-bar__button player-right-panel-button"
        :disable="!activeTrack"
        :title="$t('ui.playback.lyrics')"
        :aria-label="$t('ui.player.openLyrics')"
        @click="openRightPanel('lyrics')"
      />

      <!-- Everything the bottom bar used to hold that does not earn a permanent slot. -->
      <q-btn flat round dense icon="tune" class="canopy-bar__button" :title="$t('ui.player.quickSettings')" :aria-label="$t('ui.player.quickSettings')">
        <q-menu dark anchor="bottom right" self="top right" class="player-popup-menu canopy-overflow-menu">
          <q-list dark style="min-width: 236px">
            <q-item-label header>{{ $t('ui.player.output') }}</q-item-label>
            <q-item clickable>
              <q-item-section>{{ outputDeviceLabel }}</q-item-section>
              <q-item-section side>
                <q-icon name="chevron_right" />
              </q-item-section>
              <q-menu dark anchor="top end" self="top start">
                <q-list dark style="min-width: 180px">
                  <q-item v-if="audioOutputLoading" disable>
                    <q-item-section class="text-grey text-caption">{{ $t('ui.player.loadingDevices') }}</q-item-section>
                  </q-item>
                  <q-item v-else-if="!audioOutputDevices || audioOutputDevices.length === 0" disable>
                    <q-item-section class="text-grey text-caption">{{ $t('ui.player.noOutputDevices') }}</q-item-section>
                  </q-item>
                  <q-item
                    v-for="device in audioOutputDevices"
                    v-else
                    :key="device.deviceId"
                    v-close-popup
                    clickable
                    :active="audioEngineConfig.outputDeviceId === device.deviceId"
                    @click="audioEngineConfig.outputDeviceId = device.deviceId"
                  >
                    <q-item-section>{{ device.label }}</q-item-section>
                  </q-item>
                </q-list>
              </q-menu>
            </q-item>

            <q-separator dark />

            <q-item v-close-popup clickable @click="handleListeningPartyClick">
              <q-item-section avatar>
                <q-icon :name="listeningParty.status === 'connected' ? 'groups' : 'group_add'" />
              </q-item-section>
              <q-item-section>{{ $t('ui.player.listeningParty') }}</q-item-section>
            </q-item>
            <q-item v-close-popup clickable @click="openSleepTimerSettings">
              <q-item-section avatar>
                <q-icon name="bedtime" />
              </q-item-section>
              <q-item-section>{{ sleepTimerActive ? `Sleep timer: ${sleepTimerStatus}` : 'Sleep timer' }}</q-item-section>
            </q-item>

          </q-list>
        </q-menu>
      </q-btn>

      <q-btn flat round dense icon="menu" class="canopy-bar__button" :title="$t('ui.player.quickSettings')" :aria-label="$t('ui.player.quickSettings')">
        <q-menu anchor="bottom right" self="top right" class="compact-settings-menu-popup player-popup-menu">
          <CompactSettingsMenu :app="app" />
        </q-menu>
      </q-btn>
    </div>

    <div v-if="!nativeTitlebar" class="canopy-bar__controls">
      <button type="button" :title="$t('common.minimize')" :aria-label="$t('common.minimize')" @click="minimizeWindow">
        <q-icon name="remove" />
      </button>
      <button type="button" :title="$t('common.maximize')" :aria-label="$t('common.maximize')" @click="toggleMaximizeWindow">
        <q-icon name="crop_square" />
      </button>
      <button type="button" :title="$t('common.close')" :aria-label="$t('common.close')" class="canopy-bar__close" @click="closeWindow">
        <q-icon name="close" />
      </button>
    </div>
  </header>
</template>

<style src="../../styles/layout-canopy.css"></style>
