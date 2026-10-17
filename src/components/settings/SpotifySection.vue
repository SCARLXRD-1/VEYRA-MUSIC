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
import { ref, onMounted } from 'vue';
import { t } from '../../app/i18n/index.js';

export default {
  name: 'SpotifySection',
  props: { app: { type: Object, required: true } },
  setup() {
    const spotifyState = ref({ status: 'disconnected', hasSpdc: false, secureStorage: false });
    const spotifyMessage = ref('');
    const showManualInput = ref(false);
    const manualSpdc = ref('');

    const spotifyBridge = () => (window.veyraSpotify || window.orchardSpotify);

    const loadStatus = async () => {
      const bridge = spotifyBridge();
      if (!bridge) return;
      try {
        spotifyState.value = await bridge.status();
      } catch (err) {
        console.warn('Failed to load Spotify status', err);
      }
    };

    const connectSpotify = async () => {
      const bridge = spotifyBridge();
      if (!bridge) return;
      spotifyMessage.value = '';
      try {
        spotifyState.value = await bridge.connect();
        spotifyMessage.value = t(spotifyState.value.hasSpdc ? 'ui.integrations.spotifyConnectedMessage' : 'ui.integrations.spotifyLoginClosed');
      } catch (err) {
        spotifyMessage.value = err?.message || t('ui.integrations.spotifyLoginFailed');
      }
    };

    const disconnectSpotify = async () => {
      const bridge = spotifyBridge();
      if (!bridge) return;
      spotifyMessage.value = '';
      try {
        spotifyState.value = await bridge.disconnect();
        spotifyMessage.value = t('ui.integrations.spotifyDisconnected');
      } catch (err) {
        spotifyMessage.value = err?.message || t('ui.integrations.spotifyDisconnectFailed');
      }
    };

    const saveManualSpdc = async () => {
      const bridge = spotifyBridge();
      if (!bridge || !manualSpdc.value.trim()) return;
      spotifyMessage.value = '';
      try {
        spotifyState.value = await bridge.saveSpdc(manualSpdc.value.trim());
        spotifyMessage.value = t(spotifyState.value.hasSpdc ? 'ui.integrations.spotifyCookieSaved' : 'ui.integrations.spotifyInvalidCookie');
        manualSpdc.value = '';
        showManualInput.value = false;
      } catch (err) {
        spotifyMessage.value = err?.message || t('ui.integrations.spotifyCookieFailure');
      }
    };

    onMounted(() => {
      loadStatus();
    });

    return {
      spotifyState,
      spotifyMessage,
      showManualInput,
      manualSpdc,
      connectSpotify,
      disconnectSpotify,
      saveManualSpdc
    };
  }
};
</script>

<template>
  <div class="settings-row settings-row--options">
    <div class="settings-row__copy">
      <label>{{ $t('ui.integrations.spotifyTitle') }}</label>
      <p v-if="spotifyState.status === 'connected'">
        {{ $t('ui.integrations.spotifyConnected') }}
      </p>
      <p v-else>
        {{ $t('ui.integrations.spotifyHelp') }}
      </p>
      <small v-if="spotifyMessage" class="settings-status-message">{{ spotifyMessage }}</small>
    </div>
    <div class="settings-actions spotify-actions">
      <template v-if="spotifyState.status === 'connected'">
        <button
          type="button"
          class="settings-link-button settings-link-button--danger"
          @click="disconnectSpotify"
        >
          {{ $t('ui.integrations.disconnect') }}
        </button>
      </template>
      <template v-else>
        <button
          type="button"
          class="settings-button"
          @click="connectSpotify"
        >
          {{ $t('ui.integrations.spotifyLogin') }}
        </button>
        <button
          type="button"
          class="settings-link-button"
          @click="showManualInput = !showManualInput"
        >
          {{ $t(showManualInput ? 'ui.integrations.cancel' : 'ui.integrations.enterCookie') }}
        </button>
      </template>
    </div>
  </div>

  <div v-if="showManualInput && spotifyState.status !== 'connected'" class="settings-manual-cookie-row">
    <input
      v-model="manualSpdc"
      type="password"
      class="settings-input"
      :placeholder="$t('ui.integrations.cookiePlaceholder')"
      @keyup.enter="saveManualSpdc"
    />
    <button
      type="button"
      class="settings-button"
      :disabled="!manualSpdc.trim()"
      @click="saveManualSpdc"
    >
      {{ $t('ui.integrations.save') }}
    </button>
  </div>
</template>

<style scoped>
.settings-status-message {
  display: block;
  margin-top: 5px;
  color: #929a94;
  font-size: 10px;
  line-height: 1.4;
}

.spotify-actions {
  justify-content: flex-end;
  gap: 8px;
  padding-top: 0;
}

.settings-manual-cookie-row {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 6px;
}

.settings-input {
  flex: 1;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  color: #fff;
  padding: 6px 10px;
  font-size: 12px;
}
</style>
