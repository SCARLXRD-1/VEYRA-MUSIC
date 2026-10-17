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
import { onMounted, ref } from 'vue';
import { t } from '../../app/i18n/index.js';

export default {
  name: 'QobuzSection',
  setup() {
    const state = ref({
      status: 'disconnected',
      enabled: false,
      quality: 'auto',
      secureStorage: false,
      persistent: false,
      lastError: ''
    });
    const message = ref('');
    const busy = ref(false);
    const qualities = [
      { value: 'auto' },
      { value: 'lossless' },
      { value: 'hires' }
    ];

    const qobuzBridge = () => (window.veyraQobuz || window.orchardQobuz);

    async function loadStatus() {
      const bridge = qobuzBridge();
      if (!bridge) return;
      try {
        state.value = await bridge.status();
      } catch (error) {
        message.value = error?.message || t('ui.integrations.qobuzStatusUnavailable');
      }
    }

    async function connect() {
      const bridge = qobuzBridge();
      if (!bridge || busy.value) return;
      busy.value = true;
      message.value = '';
      try {
        state.value = await bridge.connect();
        if (state.value.status === 'connected') {
          message.value = state.value.persistent
            ? t('ui.integrations.qobuzConnectedPersistent')
            : t('ui.integrations.qobuzConnectedTemporary');
        } else {
          message.value = t('ui.integrations.qobuzLoginClosed');
        }
      } catch (error) {
        message.value = error?.message || t('ui.integrations.qobuzLoginFailed');
        await loadStatus();
      } finally {
        busy.value = false;
      }
    }

    async function disconnect() {
      const bridge = qobuzBridge();
      if (!bridge || busy.value) return;
      busy.value = true;
      message.value = '';
      try {
        state.value = await bridge.disconnect();
        message.value = t('ui.integrations.qobuzDisconnected');
      } catch (error) {
        message.value = error?.message || t('ui.integrations.qobuzDisconnectFailed');
      } finally {
        busy.value = false;
      }
    }

    async function setEnabled(value) {
      const bridge = qobuzBridge();
      if (!bridge) return;
      state.value = await bridge.update({ enabled: value });
    }

    async function setQuality(value) {
      const bridge = qobuzBridge();
      if (!bridge) return;
      state.value = await bridge.update({ quality: value });
    }

    onMounted(loadStatus);
    return { busy, connect, disconnect, message, qualities, setEnabled, setQuality, state };
  }
};
</script>

<template>
  <div class="settings-row settings-row--options qobuz-row">
    <div class="settings-row__copy">
      <label>{{ $t('ui.integrations.qobuzTitle') }}</label>
      <p v-if="state.status === 'connected'">
        {{ $t('ui.integrations.qobuzConnected') }}
      </p>
      <p v-else>
        {{ $t('ui.integrations.qobuzHelp') }}
      </p>
      <small v-if="message || state.lastError" class="qobuz-message">
        {{ message || state.lastError }}
      </small>
    </div>
    <div class="settings-actions qobuz-actions">
      <template v-if="state.status === 'connected'">
        <q-toggle
          :model-value="state.enabled"
          color="primary"
          :label="$t('ui.integrations.enabled')"
          :aria-label="$t('ui.integrations.useQobuz')"
          @update:model-value="setEnabled"
        />
        <button type="button" class="settings-link-button settings-link-button--danger" :disabled="busy" @click="disconnect">
          {{ $t('ui.integrations.disconnect') }}
        </button>
      </template>
      <button v-else type="button" class="settings-button" :disabled="busy" @click="connect">
        {{ $t(busy ? 'ui.integrations.connecting' : 'ui.integrations.connectQobuz') }}
      </button>
    </div>
  </div>

  <div v-if="state.status === 'connected'" class="settings-row settings-row--options qobuz-quality-row">
    <div class="settings-row__copy">
      <label id="settings-qobuz-quality">{{ $t('ui.integrations.qobuzQuality') }}</label>
      <p>{{ $t('ui.integrations.qobuzQualityHelp') }}</p>
    </div>
    <div class="settings-option-group" role="group" aria-labelledby="settings-qobuz-quality">
      <button
        v-for="option in qualities"
        :key="option.value"
        type="button"
        class="settings-option"
        :class="{ 'settings-option--active': state.quality === option.value }"
        :aria-pressed="state.quality === option.value"
        @click="setQuality(option.value)"
      >
        {{ $t(option.value === 'auto' ? 'ui.integrations.qobuzBestAvailable' : option.value === 'lossless' ? 'ui.integrations.qobuzLossless' : 'ui.integrations.qobuzHiRes') }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.qobuz-actions {
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 0;
}

.qobuz-message {
  display: block;
  margin-top: 5px;
  color: #aab2ac;
  font-size: 10px;
  line-height: 1.4;
}

.qobuz-quality-row {
  margin-top: -4px;
}
</style>
