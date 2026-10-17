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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
-->

<script>
import { useI18n } from '../../app/i18n/index.js';

export default {
  name: 'NavigationLayoutSection',
  props: { app: { type: Object, required: true } },
  setup(props) {
    const { t } = useI18n();
    return { ...props.app, t };
  }
};
</script>

<template>
  <section id="settings-layout" class="settings-section" aria-labelledby="settings-layout-title">
    <div class="settings-section__heading">
      <h2 id="settings-layout-title">{{ $t('settings.sections.layout') }}</h2>
      <p>{{ $t('settings.ui.navLayoutDescription') }}</p>
    </div>

    <div class="layout-editor" aria-labelledby="settings-home-layout-title">
      <div class="layout-editor__heading">
        <div>
          <h3 id="settings-home-layout-title">{{ $t('settings.ui.homeShelves') }}</h3>
          <p>{{ $t('settings.ui.homeShelvesDescription') }}</p>
        </div>
        <button type="button" class="layout-editor__reset" @click="resetHomeLayout">{{ $t('settings.ui.reset') }}</button>
      </div>

      <ol class="layout-editor__list">
        <li
          v-for="(item, index) in homeLayoutItems"
          :key="`home-layout-${item.id}`"
          class="layout-editor__item"
          :class="{ 'layout-editor__item--hidden': !item.visible }"
        >
          <span class="layout-editor__position" aria-hidden="true">{{ String(index + 1).padStart(2, '0') }}</span>
          <span class="layout-editor__copy">
            <strong>{{ item.labelKey ? $t(item.labelKey) : item.label }}</strong>
            <small>{{ item.descriptionKey ? $t(item.descriptionKey) : item.description }}</small>
          </span>
          <span class="layout-editor__moves">
            <button
              type="button"
              :disabled="index === 0"
              :aria-label="$t('settings.ui.moveUp', { item: item.labelKey ? $t(item.labelKey) : item.label })"
              :title="$t('settings.ui.moveUp', { item: item.labelKey ? $t(item.labelKey) : item.label })"
              @click="moveHomeSection(item.id, -1)"
            >
              <q-icon name="arrow_upward" />
            </button>
            <button
              type="button"
              :disabled="index === homeLayoutItems.length - 1"
              :aria-label="$t('settings.ui.moveDown', { item: item.labelKey ? $t(item.labelKey) : item.label })"
              :title="$t('settings.ui.moveDown', { item: item.labelKey ? $t(item.labelKey) : item.label })"
              @click="moveHomeSection(item.id, 1)"
            >
              <q-icon name="arrow_downward" />
            </button>
          </span>
          <q-toggle
            :model-value="item.visible"
            color="primary"
            :aria-label="$t('settings.ui.showOnHome', { item: item.labelKey ? $t(item.labelKey) : item.label })"
            @update:model-value="setHomeSectionVisible(item.id, $event)"
          />
        </li>
      </ol>
    </div>

    <div class="layout-editor" aria-labelledby="settings-sidebar-layout-title">
      <div class="layout-editor__heading">
        <div>
          <h3 id="settings-sidebar-layout-title">{{ $t('settings.ui.sidebar') }}</h3>
          <p>{{ $t('settings.ui.sidebarDescription') }}</p>
        </div>
        <button type="button" class="layout-editor__reset" @click="resetSidebarLayout">{{ $t('settings.ui.reset') }}</button>
      </div>

      <div v-for="group in sidebarLayoutGroups" :key="`layout-group-${group.id}`" class="layout-editor__group">
        <h4>{{ group.labelKey ? $t(group.labelKey) : group.label }}</h4>
        <ol class="layout-editor__list">
          <li
            v-for="(item, index) in group.items"
            :key="`sidebar-layout-${item.id}`"
            class="layout-editor__item layout-editor__item--compact"
            :class="{ 'layout-editor__item--hidden': !item.visible }"
          >
            <span class="layout-editor__position" aria-hidden="true">{{ String(index + 1).padStart(2, '0') }}</span>
            <q-icon :name="item.icon" class="layout-editor__icon" />
            <span class="layout-editor__copy"><strong>{{ item.labelKey ? $t(item.labelKey) : item.label }}</strong></span>
            <span class="layout-editor__moves">
              <button
                type="button"
                :disabled="index === 0"
                :aria-label="$t('settings.ui.moveUp', { item: item.labelKey ? $t(item.labelKey) : item.label })"
                :title="$t('settings.ui.moveUp', { item: item.labelKey ? $t(item.labelKey) : item.label })"
                @click="moveSidebarItem(group.id, item.id, -1)"
              >
                <q-icon name="arrow_upward" />
              </button>
              <button
                type="button"
                :disabled="index === group.items.length - 1"
                :aria-label="$t('settings.ui.moveDown', { item: item.labelKey ? $t(item.labelKey) : item.label })"
                :title="$t('settings.ui.moveDown', { item: item.labelKey ? $t(item.labelKey) : item.label })"
                @click="moveSidebarItem(group.id, item.id, 1)"
              >
                <q-icon name="arrow_downward" />
              </button>
            </span>
            <q-toggle
              :model-value="item.visible"
              color="primary"
              :aria-label="$t('settings.ui.showInSidebar', { item: item.labelKey ? $t(item.labelKey) : item.label })"
              @update:model-value="setSidebarItemVisible(item.id, $event)"
            />
          </li>
        </ol>
      </div>
    </div>
  </section>
</template>
