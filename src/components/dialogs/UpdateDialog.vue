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
  name: 'UpdateDialog',
  props: { app: { type: Object, required: true } },
  setup(props) {
    return props.app;
  }
};
</script>

<template>
  <q-dialog v-model="updateDialogOpen" :aria-label="$t('ui.dialogs.updates')">
    <q-card class="update-dialog">
      <header class="update-dialog__header">
        <div>
          <div class="update-dialog__title">{{ $t('ui.dialogs.updates') }}</div>
          <div class="update-dialog__subtitle">{{ updateStatusLabel }}</div>
        </div>
        <q-btn v-close-popup flat round dense icon="close" :aria-label="$t('common.close')" />
      </header>

      <div class="update-dialog__body">
        <dl class="update-dialog__versions">
          <div>
            <dt>{{ $t('ui.dialogs.installed') }}</dt>
            <dd>{{ updateState.version || appVersion }}</dd>
          </div>
          <div>
            <dt>{{ $t('ui.dialogs.available') }}</dt>
            <dd>{{ updateState.availableVersion || '—' }}</dd>
          </div>
          <div>
            <dt>{{ $t('ui.dialogs.source') }}</dt>
            <dd>{{ updateState.packageLabel ? `${updateState.packageLabel} · ${updateChannelIsBeta ? 'Beta channel' : 'Release channel'}` : (updateChannelIsBeta ? 'Beta channel (GitHub)' : (updateState.updateUrl ? 'Release channel' : 'Not configured')) }}</dd>
          </div>
        </dl>

        <section class="update-dialog__content">
          <div class="update-dialog__section-heading">
            <strong>{{ $t('ui.dialogs.appUpdate') }}</strong>
            <button type="button" :disabled="!updateCanCheck" @click="checkForUpdates">{{ $t('ui.dialogs.checkApp') }}</button>
          </div>
          <div class="update-dialog__status" :class="{ 'update-dialog__status--error': updateState.status === 'error' }">
            <q-icon :name="updateBannerIcon" />
            <div>
              <strong>{{ updateState.message || updateStatusLabel }}</strong>
              <span v-if="updateState.error">{{ updateState.error }}</span>
              <span v-else-if="updateState.releaseDate">{{ updateState.releaseDate }}</span>
            </div>
          </div>
        </section>

        <section class="update-dialog__content">
          <div class="update-dialog__section-heading">
            <strong>{{ $t('ui.dialogs.artistPages') }}</strong>
            <div class="update-dialog__section-actions">
              <button type="button" @click="importArtistPack">{{ $t('ui.dialogs.importPack') }}</button>
              <button type="button" :disabled="!contentUpdateCanCheck" @click="checkContentUpdates({ force: updateState.dev })">{{ $t('ui.dialogs.checkOfficial') }}</button>
            </div>
          </div>
          <div class="update-dialog__status" :class="{ 'update-dialog__status--error': updateState.content?.status === 'error' }">
            <q-icon :name="updateState.content?.status === 'error' ? 'warning' : 'palette'" />
            <div>
              <strong>{{ updateState.content?.message || contentUpdateStatusLabel }}</strong>
              <span v-if="updateState.content?.error">{{ updateState.content.error }}</span>
              <span v-else-if="updateState.content?.userPackCount">User packs installed: {{ updateState.content.userPackCount }}</span>
              <span v-else-if="updateState.content?.installedVersion">Installed {{ updateState.content.installedVersion }}</span>
            </div>
          </div>
        </section>

        <q-linear-progress
          v-if="['downloading', 'external-downloading'].includes(updateState.status)"
          :value="updateProgressPercent / 100"
          color="primary"
          track-color="grey-10"
          class="update-dialog__progress"
          rounded
        />

      </div>

      <footer class="update-dialog__actions">
        <button
          type="button"
          class="update-dialog__button"
          :disabled="!updateCanCheck"
          @click="checkForUpdates"
        >
          <q-icon name="refresh" />
          <span>{{ updateState.status === 'error' ? 'Retry' : 'Check' }}</span>
        </button>
        <button
          v-if="updateState.external && !externalUpdateCanReveal"
          type="button"
          class="update-dialog__button update-dialog__button--primary"
          :disabled="!externalUpdateCanDownload"
          @click="downloadExternalUpdate"
        >
          <q-icon name="download" />
          <span>{{ updateState.status === 'external-downloading' ? 'Downloading…' : 'Download package' }}</span>
        </button>
        <button
          v-else-if="updateState.external"
          type="button"
          class="update-dialog__button update-dialog__button--primary"
          :disabled="!externalUpdateCanReveal"
          @click="revealExternalUpdate"
        >
          <q-icon name="folder_open" />
          <span>{{ $t('ui.dialogs.showInDownloads') }}</span>
        </button>
        <button
          v-else
          type="button"
          class="update-dialog__button update-dialog__button--primary"
          :disabled="!updateCanInstall"
          @click="installUpdate"
        >
          <q-icon :name="updateState.status === 'available' ? 'download' : 'restart_alt'" />
          <span>{{ updateState.status === 'available' ? 'Download update' : 'Restart and install' }}</span>
        </button>
      </footer>
    </q-card>
  </q-dialog>
</template>
