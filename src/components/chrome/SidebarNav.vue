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
import { computed, ref, watch } from 'vue';
import { useI18n } from '../../app/i18n/index.js';

function isEmailLike(value = '') {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(value || '').trim());
}

export default {
  name: 'SidebarNav',
  props: { app: { type: Object, required: true } },
  setup(props) {
    const { t } = useI18n();
    const accountAvatarFailed = ref(false);
    const accountAvatarRetry = ref(0);
    const accountAvatarUrl = computed(() => {
      const thumbnail = String(props.app.authState.value.user?.thumbnail || '').trim();
      if (!thumbnail || accountAvatarRetry.value === 0) return thumbnail;
      const separator = thumbnail.includes('?') ? '&' : '?';
      return `${thumbnail}${separator}veyra_retry=${accountAvatarRetry.value}`;
    });
    const accountDisplayName = computed(() => {
      let name = String(props.app.authState.value.user?.name || '').trim();
      let byline = String(props.app.authState.value.user?.byline || '').trim();
      if (/subscribers?/i.test(name) && byline && !/subscribers?/i.test(byline)) {
        name = byline;
      }
      return name && !isEmailLike(name) && !/^(signed in|youtube music)$/i.test(name) ? name : t('common.signedIn');
    });
    const accountDisplayByline = computed(() => {
      let name = String(props.app.authState.value.user?.name || '').trim();
      let byline = String(props.app.authState.value.user?.byline || '').trim();
      if (/subscribers?/i.test(name) && byline && !/subscribers?/i.test(byline)) {
        byline = name;
      }
      if (/subscribers?/i.test(byline)) {
        byline = '';
      }
      return byline && !isEmailLike(byline) && !/^(signed in)$/i.test(byline) ? byline : 'YouTube Music';
    });
    const accountInitial = computed(() => {
      const identity = [accountDisplayName.value, accountDisplayByline.value]
        .map((value) => String(value || '').trim())
        .find((value) => value && !/^(signed in|youtube music)$/i.test(value));
      return identity?.replace(/^@/, '').match(/[\p{L}\p{N}]/u)?.[0]?.toUpperCase() || '';
    });

    watch(() => props.app.authState.value.user?.thumbnail, () => {
      accountAvatarFailed.value = false;
      accountAvatarRetry.value = 0;
    });

    function retryAccountAvatar() {
      if (accountAvatarRetry.value < 2) {
        accountAvatarRetry.value += 1;
        return;
      }
      accountAvatarFailed.value = true;
    }

    return {
      ...props.app,
      accountAvatarFailed,
      accountAvatarUrl,
      accountDisplayByline,
      accountDisplayName,
      accountInitial,
      retryAccountAvatar,
      t
    };
  }
};
</script>

<template>
    <q-drawer
      show-if-above
      behavior="desktop"
      :width="sidebarWidth"
      :mini="sidebarMini"
      :mini-width="sidebarMiniWidth || 68"
      class="sidebar"
    >
      <template #mini>
        <nav class="sidebar-mini" :aria-label="$t('ui.sidebar.compactNavigation')">
          <button type="button" class="sidebar-mini__button" :title="$t('nav.search')" :aria-label="$t('nav.search')" :disabled="!authState.signedIn" @click="openSpotlightSearch('')">
            <q-icon name="search" />
          </button>
          <button
            v-for="item in visibleMiniSidebarItems"
            :key="`sidebar-mini-${item.id}`"
            type="button"
            class="sidebar-mini__button"
            :class="{ 'sidebar-mini__button--active': sidebarItemActive(item) }"
            :title="item.labelKey ? $t(item.labelKey) : item.label"
            :aria-label="item.labelKey ? $t(item.labelKey) : item.label"
            :disabled="item.requiresAuth && !authState.signedIn"
            @click="openSidebarItem(item)"
          >
            <q-icon :name="item.icon" />
          </button>
          <button
            type="button"
            class="sidebar-mini__button"
            :class="{ 'sidebar-mini__button--active': rightPanelOpen && rightPanelMode === 'queue' }"
            :title="$t('nav.queue')"
            :aria-label="$t('nav.queue')"
            @click="openRightPanel('queue')"
          >
            <q-icon name="queue_music" />
          </button>
          <button
            type="button"
            class="sidebar-mini__button"
            :class="{ 'sidebar-mini__button--active': rightPanelOpen && rightPanelMode === 'lyrics' }"
            :title="$t('nav.lyrics')"
            :aria-label="$t('nav.lyrics')"
            :disabled="!activeTrack"
            @click="openRightPanel('lyrics')"
          >
            <q-icon name="lyrics" />
          </button>
          <span class="sidebar-mini__spacer" />
          <button type="button" class="sidebar-mini__button" :class="{ 'sidebar-mini__button--active': activeView === 'support' }" :title="$t('nav.support')" :aria-label="$t('nav.support')" @click="selectView('support')">
            <q-icon name="support_agent" />
            <span v-if="supportUnreadCount" class="sidebar-mini__count" aria-hidden="true">{{ supportUnreadCount }}</span>
          </button>
          <button type="button" class="sidebar-mini__button" :class="{ 'sidebar-mini__button--active': activeView === 'settings' }" :title="$t('nav.settings')" :aria-label="$t('nav.settings')" @click="selectView('settings')">
            <q-icon name="settings" />
          </button>
        </nav>
      </template>
      <div class="sidebar-inner">
        <q-form class="sidebar-search-form" @submit.prevent="runSearch">
          <q-input
            v-model="query"
            dense
            borderless
            clearable
            clear-icon="close"
            :loading="loading"
            :placeholder="$t('nav.searchPlaceholder')"
            :aria-label="$t('nav.search')"
            :aria-busy="loading"
            autocomplete="off"
            spellcheck="false"
            class="sidebar-search-input"
            :disable="!authState.signedIn"
          >
            <template #prepend>
              <q-icon name="search" />
            </template>
          </q-input>
        </q-form>

        <nav
          v-for="group in visibleSidebarNavGroups"
          :key="`sidebar-group-${group.id}`"
          class="sidebar-group sidebar-group--nav"
          :aria-label="group.labelKey ? $t(group.labelKey) : group.label"
        >
          <div class="sidebar-label">{{ group.labelKey ? $t(group.labelKey) : group.label }}</div>
          <button
            v-for="item in group.items"
            :key="`sidebar-item-${item.id}`"
            type="button"
            class="nav-link"
            :class="{ 'nav-link--active': sidebarItemActive(item) }"
            :disabled="item.requiresAuth && !authState.signedIn"
            @click="openSidebarItem(item)"
          >
            <q-icon :name="item.icon" />
            <span>{{ item.labelKey ? $t(item.labelKey) : item.label }}</span>
          </button>
        </nav>


        <div v-if="sidebarLibraryItems.length" class="sidebar-group sidebar-group--playlists">
          <div class="sidebar-section-heading">
            <span>{{ $t('ui.sidebar.playlists') }}</span>
            <span>{{ sidebarLibraryItems.length }}</span>
          </div>
          <div class="sidebar-playlist-scroll">
            <button
              v-for="item in sidebarLibraryItems"
              :key="`sidebar-${itemBrowseId(item)}`"
              type="button"
              class="library-link"
              :class="{ 'library-link--active': browseDetail?.browseId === itemBrowseId(item) }"
              @click="openMedia(item, homeShelfSections.flatMap((section) => section.items))"
            >
              <q-img v-if="item.thumbnail" :src="item.thumbnail" class="library-link__cover" />
              <div v-else class="library-link__cover library-link__cover--empty">
                <q-icon name="album" />
              </div>
              <div class="library-link__copy">
                <span>{{ item.title }}</span>
                <small>{{ itemMeta(item) }}</small>
              </div>
            </button>
          </div>
        </div>

        <div class="sidebar-group sidebar-group--status">
          <button
            type="button"
            class="nav-link sidebar-support-link"
            :class="{ 'nav-link--active': activeView === 'support' }"
            @click="selectView('support')"
          >
            <q-icon name="support_agent" />
            <span>{{ $t('nav.support') }}</span>
            <span v-if="supportUnreadCount" class="nav-link__count" :aria-label="$t('ui.sidebar.unreadReplies', { count: supportUnreadCount })">{{ supportUnreadCount }}</span>
          </button>
          <button
            type="button"
            class="nav-link sidebar-settings-link"
            :class="{ 'nav-link--active': activeView === 'settings' }"
            @click="selectView('settings')"
          >
            <q-icon name="settings" />
            <span>{{ $t('nav.settings') }}</span>
          </button>

          <div class="sidebar-presence">
            <span :title="$t('ui.sidebar.connection', { status: socketState })">
              <q-icon :name="socketState === 'connected' ? 'link' : 'link_off'" />
              {{ socketState === 'connected' ? $t('common.online') : socketState }}
            </span>
            <span :title="authLabel">{{ authLabel }}</span>
          </div>

          <div class="account-actions">
            <q-btn
              v-if="!authState.signedIn"
              unelevated
              color="primary"
              icon="login"
              :label="$t('ui.sidebar.signIn')"
              :loading="authState.status === 'starting'"
              @click="startLogin"
            />
            <div v-else class="account-menu-wrap">
              <button
                type="button"
                class="account-profile"
                :aria-expanded="accountMenuOpen"
                aria-haspopup="menu"
                :title="$t('ui.sidebar.accountOptions')"
                @click="accountMenuOpen = !accountMenuOpen"
              >
                <img
                  v-if="authState.user?.thumbnail && !accountAvatarFailed"
                  :src="accountAvatarUrl"
                  class="account-profile__avatar"
                  alt=""
                  @error="retryAccountAvatar"
                />
                <span v-else class="account-profile__avatar">
                  <template v-if="accountInitial">{{ accountInitial }}</template>
                  <q-icon v-else name="account_circle" />
                </span>
                <span class="account-profile__copy">
                  <strong>{{ accountDisplayName }}</strong>
                  <span>{{ accountDisplayByline }}</span>
                </span>
                <q-icon name="unfold_more" />
              </button>

              <q-menu v-model="accountMenuOpen" no-parent-event anchor="top right" self="bottom right" class="account-menu">
                <div class="account-menu__identity">
                  <strong>{{ accountDisplayName }}</strong>
                  <span>{{ accountDisplayByline }}</span>
                </div>
                <q-separator dark />
                <button type="button" class="account-menu__action" :disabled="accountSwitching" @click="switchAccount">
                  <q-icon name="switch_account" />
                  <span>{{ $t('ui.sidebar.switchAccount') }}</span>
                </button>
                <button type="button" class="account-menu__action" @click="signOut">
                  <q-icon name="logout" />
                  <span>{{ $t('common.signOut') }}</span>
                </button>
              </q-menu>
            </div>
          </div>

          <div v-if="authState.pending" class="device-card">
            <div class="device-card__code">{{ authState.pending.userCode }}</div>
            <div class="device-card__url">{{ authState.pending.verificationUrl }}</div>
            <div class="device-card__actions">
              <button type="button" @click="openVerification">{{ $t('ui.sidebar.openLink') }}</button>
              <button type="button" @click="copyLoginText(authState.pending.userCode)">{{ $t('ui.sidebar.copyCode') }}</button>
              <button type="button" @click="copyLoginText(authState.pending.verificationUrl)">{{ $t('ui.sidebar.copyLink') }}</button>
            </div>
          </div>
        </div>
      </div>
    </q-drawer>

</template>
