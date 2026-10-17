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
  name: 'AboutDialog',
  props: { app: { type: Object, required: true } },
  methods: {
    async viewLicense() {
      try {
        await (window.veyraApp || window.orchardApp)?.viewLicense?.();
      } catch (error) {
        console.warn('Could not open the Veyra Music license.', error);
      }
    }
  },
  setup(props) {
    return props.app;
  }
};
</script>

<template>
  <q-dialog v-model="aboutDialogOpen" :aria-label="$t('ui.accessibility.about')">
    <q-card class="about-dialog-card">
      <q-btn
        v-close-popup
        flat
        round
        dense
        icon="close"
        class="about-dialog__close"
        :aria-label="$t('ui.accessibility.closeAbout')"
      />

      <div class="about-dialog__content">
        <img class="about-dialog__logo" :src="veyraLogoUrl" alt="Veyra Music" />
        <div class="about-dialog__name">Veyra Music</div>
        <div class="about-dialog__license">
          <span>{{ $t('ui.dialogs.license') }}</span>
          <strong>AGPL-3.0-or-later</strong>
        </div>
        <q-btn
          flat
          dense
          icon="description"
          label="View license"
          class="about-dialog__license-button"
          @click="viewLicense"
        />
        <a
          class="about-dialog__website"
          href="https://github.com/SCARLXRD-1/VEYRA-MUSIC"
          target="_blank"
          rel="noopener noreferrer"
        >github.com/SCARLXRD-1/VEYRA-MUSIC</a>
        <div class="about-dialog__upstream">
          <span>Fork de</span>
          <a
            href="https://github.com/SFG5453/Orchard"
            target="_blank"
            rel="noopener noreferrer"
          >Orchard (SFG5453)</a>
        </div>
      </div>
    </q-card>
  </q-dialog>
</template>
