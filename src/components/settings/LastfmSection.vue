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
export default {
  name: 'LastfmSection',
  props: { app: { type: Object, required: true } },
  setup(props) {
    return { ...props.app };
  }
};
</script>

<template>
  <div class="settings-row settings-row--options">
    <div class="settings-row__copy">
      <label>{{ $t('ui.integrations.lastfmTitle') }}</label>
      <p v-if="lastfmState.status === 'connected'">
        {{ $t('ui.integrations.lastfmSending', { user: lastfmState.user }) }}
      </p>
      <p v-else-if="lastfmState.status === 'pending'">
        {{ $t('ui.integrations.lastfmApprove') }}
      </p>
      <p v-else>
        {{ $t('ui.integrations.lastfmHelp') }}
      </p>
      <small v-if="lastfmMessage" class="settings-status-message">{{ lastfmMessage }}</small>
      <small v-if="lastfmState.status === 'connected' && !lastfmState.secureStorage" class="settings-status-message">
        {{ $t('ui.integrations.secureStorageUnavailable') }}
      </small>
    </div>
    <div class="settings-actions lastfm-actions">
      <button
        v-if="lastfmState.status === 'pending'"
        type="button"
        class="settings-button"
        @click="completeLastfmConnection"
      >
        {{ $t('ui.integrations.finishConnection') }}
      </button>
      <button
        v-else-if="lastfmState.status === 'connected'"
        type="button"
        class="settings-link-button settings-link-button--danger"
        @click="disconnectLastfm"
      >
        Disconnect
      </button>
      <button
        v-else
        type="button"
        class="settings-button"
        :disabled="lastfmState.status === 'loading' || lastfmState.status === 'unavailable'"
        @click="connectLastfm"
      >
        Connect Last.fm
      </button>
    </div>
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

.lastfm-actions {
  justify-content: flex-end;
  padding-top: 0;
}
</style>
