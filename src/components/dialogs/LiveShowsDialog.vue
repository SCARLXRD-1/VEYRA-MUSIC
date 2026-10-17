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
  name: 'LiveShowsDialog',
  props: { app: { type: Object, required: true } },
  setup(props) {
    return props.app;
  }
};
</script>

<template>
  <q-dialog v-model="liveShowsDialogOpen" :aria-label="$t('ui.dialogs.findShows')">
    <q-card class="live-shows-dialog">
      <header class="live-shows-dialog__header">
        <div>
          <strong>{{ $t('ui.dialogs.findShows') }}</strong>
          <span>{{ $t('ui.dialogs.showsDescription') }}</span>
        </div>
        <q-btn flat round dense icon="close" :aria-label="$t('common.close')" @click="closeLiveShows" />
      </header>

      <form class="live-shows-dialog__form" @submit.prevent="findLiveShowsByLocation">
        <label for="live-shows-location">{{ $t('ui.dialogs.location') }}</label>
        <div class="live-shows-dialog__location-row">
          <input
            id="live-shows-location"
            v-model="liveShowsLocation"
            type="text"
            maxlength="100"
            autocomplete="postal-code"
            :placeholder="$t('ui.dialogs.location')"
            :disabled="liveShowsLoading"
          />
          <button type="submit" :disabled="liveShowsLoading || liveShowsLocation.trim().length < 2">
            <q-spinner v-if="liveShowsLoading && !liveShowsUsingCurrentLocation" size="16px" />
            <span v-else>{{ $t('ui.dialogs.search') }}</span>
          </button>
        </div>
      </form>

      <div class="live-shows-dialog__divider"><span>{{ $t('ui.dialogs.or') }}</span></div>

      <button
        type="button"
        class="live-shows-dialog__current"
        :disabled="liveShowsLoading"
        @click="findLiveShowsNearMe"
      >
        <q-spinner v-if="liveShowsLoading && liveShowsUsingCurrentLocation" size="17px" />
        <q-icon v-else name="my_location" />
        <span>{{ $t('ui.dialogs.useLocation') }}</span>
      </button>

      <div v-if="liveShowsError" class="live-shows-dialog__error" role="alert">
        <q-icon name="warning" />
        <span>{{ liveShowsError }}</span>
      </div>

      <p class="live-shows-dialog__privacy">
        Your location is sent only to Veyra Music's concert service to perform this search.
      </p>
    </q-card>
  </q-dialog>
</template>
