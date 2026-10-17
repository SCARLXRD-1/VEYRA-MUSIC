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
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { gsap } from 'gsap';
import CompactSettingsMenu from '../controls/CompactSettingsMenu.vue';
import NeumorphicWaveSlider from './NeumorphicWaveSlider.vue';
import NeumorphicAudioVisualizer from './NeumorphicAudioVisualizer.vue';
import QuickAudioDeck from './QuickAudioDeck.vue';
import { createVolumeWheelHandler } from '../../app/playback/volumeWheel.js';
import { bitrateLabel, playbackQualityLabel } from '../../app/playback/trackQuality.js';

export default {
  name: 'PlayerBar',
  components: {
    CompactSettingsMenu,
    NeumorphicWaveSlider,
    NeumorphicAudioVisualizer,
    QuickAudioDeck
  },
  props: { app: { type: Object, required: true } },
  setup(props) {
    const currentOutputDeviceLabel = computed(() => {
      const activeTarget = props.app.activePlaybackTarget?.value;
      if (activeTarget && activeTarget !== 'local') {
        const connectState = props.app.veyraConnect?.value;
        const phone = connectState?.devices?.find(d => d.id === activeTarget);
        return phone ? (phone.name || 'Phone') : 'Veyra Phone';
      }
      if (props.app.audioOutputLoading?.value) return 'Loading...';
      const activeId = props.app.audioEngineConfig.value?.outputDeviceId || 'default';
      const dev = props.app.audioOutputDevices.value?.find(d => d.deviceId === activeId);
      return dev ? dev.label : 'System default';
    });

    const currentOutputDeviceIcon = computed(() => {
      const activeTarget = props.app.activePlaybackTarget?.value;
      return (activeTarget && activeTarget !== 'local') ? 'phone_android' : 'headphones';
    });

    const connectedConnectDevices = computed(() => {
      const connectState = props.app.veyraConnect?.value;
      return (connectState?.devices || [])
        .filter(d => d.connected && Number(d.protocolVersion) >= 4);
    });

    const onSelectOutputDevice = (device) => {
      props.app.selectPlaybackTarget?.('local');
      if (props.app.audioEngineConfig.value) {
        props.app.audioEngineConfig.value.outputDeviceId = device.deviceId;
      }
    };

    const onSelectConnectDevice = (device) => {
      props.app.selectPlaybackTarget?.(device.id);
    };

    const volumeIcon = computed(() => {
      const vol = props.app.volume.value;
      if (vol === 0) return 'volume_off';
      if (vol < 0.3) return 'volume_mute';
      if (vol < 0.7) return 'volume_down';
      return 'volume_up';
    });

    const onVolumeWheel = createVolumeWheelHandler(props.app.volume);

    let savedVolume = 0.85;
    const toggleMute = () => {
      if (props.app.volume.value > 0) {
        savedVolume = props.app.volume.value;
        props.app.volume.value = 0;
      } else {
        props.app.volume.value = savedVolume || 0.85;
      }
    };

    const handleListeningPartyClick = () => {
      if (props.app.listeningParty.value?.status === 'connected') {
        props.app.rightPanelMode.value = 'party';
      } else {
        props.app.listeningPartyDialogOpen.value = true;
      }
    };

    const playerBarRoot = ref(null);
    const getBarEl = () => playerBarRoot.value?.$el || playerBarRoot.value;

    watch(
      () => props.app.activeTrack?.value?.id,
      (newId, oldId) => {
        if (newId && newId !== oldId) {
          nextTick(() => {
            const cover = getBarEl()?.querySelector?.('.mini-cover');
            if (cover) {
              gsap.fromTo(cover, { scale: 0.92, opacity: 0.7 }, { scale: 1, opacity: 1, duration: 0.35, ease: 'back.out(1.5)', clearProps: 'opacity,transform' });
            }
          });
        }
      }
    );

    watch(
      () => props.app.isPlaying?.value,
      () => {
        nextTick(() => {
          const playBtn = getBarEl()?.querySelector?.('.player-control--play');
          if (playBtn) {
            gsap.fromTo(playBtn, { scale: 0.92 }, { scale: 1, duration: 0.22, ease: 'back.out(2)', clearProps: 'transform' });
          }
        });
      }
    );

    onMounted(() => {
      props.app.loadAudioOutputDevices?.();
    });

    return {
      ...props.app,
      app: props.app,
      playerBarRoot,
      bitrateLabel,
      playbackQualityLabel,
      currentOutputDeviceLabel,
      currentOutputDeviceIcon,
      connectedConnectDevices,
      onSelectOutputDevice,
      onSelectConnectDevice,
      volumeIcon,
      onVolumeWheel,
      toggleMute,
      handleListeningPartyClick,
      sleepTimerActive: props.app.sleepTimerActive,
      sleepTimerStatus: props.app.sleepTimerStatus,
      openSleepTimerSettings: props.app.openSleepTimerSettings,
      openFullscreenPlayer: props.app.openFullscreenPlayer,
      audioOutputLoading: props.app.audioOutputLoading
    };
  }
};
</script>

<template>
  <q-footer
    ref="playerBarRoot"
    class="player-bar"
    :class="{ 'player-bar--empty': !activeTrack }"
    :style="playerBarStyle"
    :aria-label="activeTrack ? `${$t('ui.player.nowPlaying')}: ${activeTrack.title}` : $t('ui.player.nothingPlaying')"
  >
    <!-- Left Section: Track / Art Area -->
    <div class="player-section player-section--left">
      <button
        type="button"
        class="mini-cover-button"
        :title="activeTrack ? `${$t('ui.player.open') || 'Open'} ${activeTrack.album || activeTrack.title}` : $t('ui.player.albumArtwork')"
        :aria-label="activeTrack ? `${$t('ui.player.open') || 'Open'} ${activeTrack.album || activeTrack.title}` : $t('ui.player.albumArtwork')"
        :disabled="!activeTrack"
        @click="openTrackAlbum"
        @keydown="onSongActionKeydown($event, activeTrack, browseDetail)"
        @contextmenu="shareActiveTrackSongLink"
      >
        <q-img v-if="activeTrack?.thumbnail" :src="activeTrack.thumbnail" class="mini-cover" />
        <span v-else class="mini-cover mini-cover--empty">
          <q-icon name="music_note" />
        </span>
      </button>

      <div class="player-track__info">
        <div class="player-track__title-row">
          <button
            type="button"
            class="mini-title mini-link"
            :disabled="!activeTrack"
            :aria-label="activeTrack ? `${$t('ui.player.open') || 'Open'} ${activeTrack.album || activeTrack.title}` : $t('ui.player.nothingPlaying')"
            @click="openTrackAlbum"
            @keydown="onSongActionKeydown($event, activeTrack, browseDetail)"
            @contextmenu="shareActiveTrackSongLink"
          >
            {{ activeTrack?.title || 'Nothing playing' }}
          </button>
          <ExplicitBadge :explicit="activeTrack?.explicit" />
          <DownloadIndicator
            v-if="activeTrack"
            :downloaded="isTrackDownloaded(activeTrack)"
            :downloading="isTrackDownloading(activeTrack)"
          />

          <!-- YouTube Liked Songs Button -->
          <q-btn
            v-if="activeTrack"
            flat
            round
            dense
            size="sm"
            class="track-action-btn"
            :icon="isActiveTrackLiked ? 'star' : 'star_border'"
            :loading="activeTrackLikePending"
            :disable="!canToggleActiveTrackLike"
            :title="$t(isActiveTrackLiked ? 'ui.player.removeLiked' : 'ui.player.addLiked')"
            :aria-label="$t(isActiveTrackLiked ? 'ui.player.removeLiked' : 'ui.player.addLiked')"
            @click="toggleActiveTrackLike"
          />
          <!-- More / Context Menu Button -->
          <q-btn
            v-if="activeTrack"
            flat
            round
            dense
            size="sm"
            class="track-action-btn"
            icon="more_horiz"
            :title="$t('ui.player.songActions')"
            :aria-label="$t('ui.player.songActions')"
            @click="openSongActionMenu(activeTrack, $event)"
          />
        </div>

        <button
          v-if="activeTrack"
          type="button"
          class="mini-subtitle mini-link artist-link"
          :disabled="!canOpenActiveTrackArtist()"
          :aria-label="activeArtist ? `${$t('ui.player.open')} ${activeArtist}` : $t('ui.player.artist')"
          @click="openTrackArtist"
          @keydown="onSongActionKeydown($event, activeTrack, browseDetail)"
          @contextmenu="shareActiveTrackSongLink"
        >
          {{ activeArtist || 'Artist' }}
        </button>
        <div v-else class="mini-subtitle">{{ $t('ui.player.chooseSong') }}</div>

        <!-- Album Row -->
        <div v-if="activeTrack" class="mini-album-row">
          <q-icon name="album" size="14px" />
          <span class="mini-album-name">{{ activeTrack.album || 'Unknown Album' }}</span>
        </div>

        <!-- Quality Pills Row -->
        <div v-if="activeTrack" class="quality-pills-row">
          <NeumorphicAudioVisualizer :app="app" :is-playing="isPlaying" size="sm" />
          <StatusPillBadge :status="isPlaying ? 'streaming' : 'info'" size="sm" :pulse="isPlaying">
            {{ isPlaying ? 'STREAM' : 'READY' }}
          </StatusPillBadge>
          <span v-if="playbackQualityLabel(activeTrack)" class="quality-pill format-pill">
            {{ playbackQualityLabel(activeTrack) }}
          </span>
          <span v-if="bitrateLabel(activeTrack)" class="quality-pill format-pill">
            {{ bitrateLabel(activeTrack) }} kbps
          </span>
          <button
            type="button"
            class="quality-pill dsp-pill"
            :class="{ 'dsp-pill--active': app.audioEngineConfig?.value?.eqEnabled || app.crossfadeMode?.value === 'smart' }"
            :title="app.crossfadeMode?.value === 'smart' ? 'Smart Crossfade (WSOLA) Activo — Clic para Quick Audio Deck' : 'Audio DSP — Clic para Quick Audio Deck'"
            aria-label="DSP Quick Audio Deck"
          >
            DSP
            <q-menu anchor="top start" self="bottom start" class="player-popup-menu">
              <QuickAudioDeck :app="app" />
            </q-menu>
          </button>
          <span class="quality-pill duration-pill">{{ durationLabel }}</span>
        </div>
      </div>
    </div>

    <!-- Vertical Separator -->
    <div class="player-separator" />

    <!-- Center Section: Transport / Progress Area -->
    <div class="player-section player-section--center">
      <div class="transport-buttons">
        <!-- Shuffle -->
        <q-btn
          flat
          round
          dense
          class="player-control player-control--secondary"
          icon="shuffle"
          :color="shuffleEnabled ? 'primary' : undefined"
          :disable="!queue.length"
          :title="$t(shuffleEnabled ? 'ui.player.shuffleOn' : 'ui.player.shuffleOff')"
          :aria-label="$t('ui.player.shuffle')"
          @click="toggleShuffle"
        />

        <!-- Skip Previous -->
        <q-btn
          flat
          round
          dense
          class="player-control"
          icon="skip_previous"
          :disable="!activeTrack || buffering"
          :aria-label="$t('ui.player.previousSong')"
          @click="playPrevious"
        />

        <!-- Play / Pause (Large center button) -->
        <q-btn
          round
          color="primary"
          class="player-control--play"
          :loading="buffering"
          :disable="!activeTrack"
          :icon="isPlaying ? 'pause' : 'play_arrow'"
          :aria-label="$t(isPlaying ? 'ui.player.pausePlayback' : 'ui.player.startPlayback')"
          @click="togglePlayback"
        />

        <!-- Skip Next -->
        <q-btn
          flat
          round
          dense
          class="player-control"
          icon="skip_next"
          :disable="(!queue.length && (!activeTrack || repeatMode === 'off')) || buffering"
          :aria-label="$t('ui.player.nextSong')"
          @click="playNext({ skipRepeatOne: true })"
        />

        <!-- Repeat -->
        <q-btn
          flat
          round
          dense
          class="player-control player-control--secondary"
          :icon="repeatMode === 'one' ? 'repeat_one' : 'repeat'"
          :color="repeatMode !== 'off' ? 'primary' : undefined"
          :title="repeatModeTitle()"
          :aria-label="$t('ui.player.repeatMode')"
          @click="cycleRepeatMode"
        />

      </div>

      <!-- Progress Slider -->
      <div class="progress-wrap">
        <span class="progress-time">{{ formatTime(displayedTime) }}</span>
        <div class="progress-slider" :style="crossfadeProgressStyle">
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
        <span class="progress-time">{{ durationLabel }}</span>
      </div>
    </div>

    <!-- Vertical Separator -->
    <div class="player-separator" />

    <!-- Right Section: Output / Party / Volume / Settings Area -->
    <div class="player-section player-section--right">
      <div class="right-top-row">
        <!-- Output Device Selection Pill -->
        <q-btn flat dense class="output-device-pill" :aria-label="$t('ui.player.selectOutput')">
          <q-icon :name="currentOutputDeviceIcon" size="16px" class="q-mr-xs" />
          <span class="output-device-label">{{ currentOutputDeviceLabel }}</span>
          <q-icon name="expand_more" size="16px" class="q-ml-xs" />
          <q-menu dark anchor="bottom right" self="top right" class="player-popup-menu">
            <q-list dark style="min-width: 220px">
              <q-item-label header class="text-weight-bold text-caption text-grey-5">{{ $t('ui.player.computer') }}</q-item-label>
              <q-item v-if="audioOutputLoading" disable>
                <q-item-section class="text-grey text-caption">{{ $t('ui.player.loadingDevices') }}</q-item-section>
              </q-item>
              <q-item v-else-if="!audioOutputDevices || audioOutputDevices.length === 0" disable>
                <q-item-section class="text-grey text-caption">{{ $t('ui.player.noOutputDevices') }}</q-item-section>
              </q-item>
              <template v-else>
                <q-item
                  v-for="device in audioOutputDevices"
                  :key="device.deviceId"
                  clickable
                  v-close-popup
                  :active="activePlaybackTarget === 'local' && audioEngineConfig.outputDeviceId === device.deviceId"
                  @click="onSelectOutputDevice(device)"
                >
                  <q-item-section avatar style="min-width: 28px">
                    <q-icon name="headphones" size="16px" />
                  </q-item-section>
                  <q-item-section>{{ device.label }}</q-item-section>
                </q-item>
              </template>

              <q-separator dark class="q-my-xs" />
              <q-item-label header class="text-weight-bold text-caption text-grey-5">{{ $t('ui.player.connect') }}</q-item-label>
              <q-item v-if="!connectedConnectDevices || connectedConnectDevices.length === 0" disable>
                <q-item-section class="text-grey text-caption">{{ $t('ui.player.noPhones') }}</q-item-section>
              </q-item>
              <template v-else>
                <q-item
                  v-for="device in connectedConnectDevices"
                  :key="device.id"
                  clickable
                  v-close-popup
                  :active="activePlaybackTarget === device.id"
                  @click="onSelectConnectDevice(device)"
                >
                  <q-item-section avatar style="min-width: 28px">
                    <q-icon name="phone_android" size="16px" :color="activePlaybackTarget === device.id ? 'primary' : undefined" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ device.name || 'Phone' }}</q-item-label>
                    <q-item-label v-if="activePlaybackTarget === device.id" caption class="text-primary">{{ $t('ui.player.activeTarget') }}</q-item-label>
                  </q-item-section>
                </q-item>
              </template>
            </q-list>
          </q-menu>
        </q-btn>

        <!-- Listening Party Button -->
        <q-btn
          flat
          round
          dense
          class="player-control player-control--secondary"
          :icon="listeningParty.status === 'connected' ? 'groups' : 'group_add'"
          :color="listeningParty.status === 'connected' ? 'primary' : (listeningParty.status === 'connecting' ? 'warning' : undefined)"
          :title="$t('ui.player.listeningParty')"
          :aria-label="$t('ui.player.listeningParty')"
          @click="handleListeningPartyClick"
        >
          <q-badge v-if="listeningParty.status === 'connected' && listeningParty.peers?.length > 0" color="orange" floating transparent>
            {{ listeningParty.peers.length }}
          </q-badge>
        </q-btn>

      </div>

      <div class="right-bottom-row">
        <!-- Volume controls -->
        <div
          class="volume-wrap"
          :title="$t('ui.player.scrollVolume')"
          @wheel.prevent="onVolumeWheel"
        >
          <q-btn
            flat
            round
            dense
            class="volume-btn"
            :icon="volumeIcon"
            :title="$t('ui.player.muteVolume')"
            :aria-label="$t('ui.player.muteVolume')"
            @click="toggleMute"
          />
          <q-slider
            v-model="volume"
            :min="0"
            :max="1"
            :step="0.01"
            color="primary"
            class="volume-slider"
            :aria-label="$t('ui.player.volumeLevel')"
          />
          <span class="volume-percentage">{{ Math.round(volume * 100) }}%</span>
        </div>

        <!-- Fullscreen Player -->
        <q-btn
          flat
          round
          dense
          class="player-control player-control--secondary player-fullscreen-button"
          icon="open_in_full"
          :title="$t('ui.player.openFullscreen')"
          :aria-label="$t('ui.player.openFullscreen')"
          :disable="!activeTrack"
          @click="openFullscreenPlayer"
        />

        <!-- Sleep Timer -->
        <q-btn
          flat
          round
          dense
          class="player-control player-control--secondary player-sleep-timer-button"
          :class="{ 'player-sleep-timer-button--active': sleepTimerActive }"
          icon="bedtime"
          :title="`${$t('ui.player.sleepTimer')}${sleepTimerActive ? `: ${sleepTimerStatus}` : ''}`"
          :aria-label="sleepTimerActive ? `${$t('ui.player.sleepTimer')}: ${sleepTimerStatus}. ${$t('ui.player.openSleepTimer')}` : $t('ui.player.openSleepTimer')"
          @click="openSleepTimerSettings"
        />

        <q-btn
          flat
          round
          dense
          class="player-control player-control--secondary player-right-panel-button"
          icon="queue_music"
          :title="$t('ui.player.queue')"
          :aria-label="$t('ui.player.openQueue')"
          @click="openRightPanel('queue')"
        />

        <q-btn
          flat
          round
          dense
          class="player-control player-control--secondary player-right-panel-button"
          icon="lyrics"
          :title="$t('ui.playback.lyrics')"
          :aria-label="$t('ui.player.openLyrics')"
          :disable="!activeTrack"
          @click="openRightPanel('lyrics')"
        />

        <!-- Settings Button (opens the NEW compact settings menu) -->
        <q-btn
          flat
          round
          dense
          class="player-control player-control--secondary settings-menu-btn"
          icon="menu"
          :title="$t('ui.player.quickSettings')"
          :aria-label="$t('ui.player.quickSettings')"
        >
          <q-menu anchor="top right" self="bottom right" class="compact-settings-menu-popup player-popup-menu">
            <CompactSettingsMenu :app="app" />
          </q-menu>
        </q-btn>
      </div>
    </div>
  </q-footer>
</template>
