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
import { ref } from 'vue';

export default {
  name: 'BackupRestoreSection',
  props: { app: { type: Object, required: true } },
  setup(props) {
    const fileInput = ref(null);

    async function importBackup(event) {
      const file = event.target.files?.[0];
      if (!file) return;

      try {
        await (props.app.importVeyraBackup || props.app.importOrchardBackup)(file);
      } catch (error) {
        props.app.backupMessage.value = error.message || 'Could not restore that backup.';
      } finally {
        event.target.value = '';
      }
    }

    return { ...props.app, fileInput, importBackup };
  }
};
</script>

<template>
  <section id="settings-backup" class="settings-section backup-section" aria-labelledby="settings-backup-title">
    <div class="settings-section__heading">
      <h2 id="settings-backup-title">{{ $t('settings.ui.backup') }}</h2>
      <p>{{ $t('settings.ui.backupDescription') }}</p>
    </div>

    <div class="settings-action-row backup-section__row">
      <div class="settings-row__copy">
        <span>{{ $t('settings.ui.exportData') }}</span>
        <p>{{ $t('settings.ui.exportDescription') }}</p>
      </div>
      <button type="button" class="settings-button" @click="exportVeyraBackup || exportOrchardBackup">
        <q-icon name="archive" />
        {{ $t('settings.ui.exportData') }}
      </button>
    </div>

    <div class="settings-action-row backup-section__row">
      <div class="settings-row__copy">
        <span>{{ $t('settings.ui.restoreData') }}</span>
        <p>{{ $t('settings.ui.restoreDescription') }}</p>
      </div>
      <button type="button" class="settings-button" @click="fileInput?.click()">
        <q-icon name="upload_file" />
        {{ $t('settings.ui.importData') }}
      </button>
      <input ref="fileInput" class="backup-section__input" type="file" accept="application/json,.json" @change="importBackup" />
    </div>

    <p v-if="backupMessage" class="backup-section__message">{{ backupMessage }}</p>
  </section>
</template>
