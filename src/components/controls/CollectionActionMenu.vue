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
import { computed, nextTick, ref, watch } from 'vue';

export default {
  name: 'CollectionActionMenu',
  props: { app: { type: Object, required: true } },
  setup(props) {
    const menuRef = ref(null);
    const menuStyle = computed(() => {
      const menu = props.app.collectionActionMenu.value;
      const width = 248;
      const height = 280;
      const gutter = 12;
      return {
        left: `${Math.max(gutter, Math.min(menu.x, window.innerWidth - width - gutter))}px`,
        top: `${Math.max(40, Math.min(menu.y, window.innerHeight - height - gutter))}px`
      };
    });

    watch(() => props.app.collectionActionMenu.value.open, async (open) => {
      if (!open) return;
      await nextTick();
      menuRef.value?.querySelector('button')?.focus();
    });

    function onKeydown(event) {
      if (event.key === 'Escape') {
        props.app.closeCollectionActionMenu();
        return;
      }
      if (!['ArrowDown', 'ArrowUp', 'Home', 'End'].includes(event.key)) return;
      const buttons = [...menuRef.value.querySelectorAll('button:not(:disabled)')];
      if (!buttons.length) return;
      event.preventDefault();
      const current = buttons.indexOf(document.activeElement);
      let next = event.key === 'Home' ? 0 : event.key === 'End' ? buttons.length - 1 : current;
      if (event.key === 'ArrowDown') next = (current + 1) % buttons.length;
      if (event.key === 'ArrowUp') next = (current - 1 + buttons.length) % buttons.length;
      buttons[next]?.focus();
    }

    return { ...props.app, menuRef, menuStyle, onKeydown };
  }
};
</script>

<template>
  <Teleport to="body">
    <div
      v-if="collectionActionMenu.open"
      class="collection-action-layer"
      @mousedown.self="closeCollectionActionMenu"
      @contextmenu.prevent.self="closeCollectionActionMenu"
    >
      <section
        ref="menuRef"
        class="collection-action-menu"
        :style="menuStyle"
        role="menu"
        :aria-label="$t('ui.actionWords.actionsFor', { item: collectionActionMenu.item?.title || 'collection' })"
        @keydown="onKeydown"
        @contextmenu.prevent
      >
        <div class="collection-action-menu__title">
          <strong>{{ collectionActionMenu.item?.title }}</strong>
          <span>{{ itemTypeLabel(collectionActionMenu.item) }}</span>
        </div>
        <div class="collection-action-menu__items">
          <button type="button" role="menuitem" @click="runCollectionAction('open')">
            <q-icon name="open_in_new" /><span>{{ $t('ui.actionWords.open') }}</span>
          </button>
          <button type="button" role="menuitem" @click="runCollectionAction('play')">
            <q-icon name="play_arrow" /><span>{{ $t('ui.actionWords.play') }}</span>
          </button>
          <button type="button" role="menuitem" @click="runCollectionAction('shuffle')">
            <q-icon name="shuffle" /><span>{{ $t('ui.actionWords.shuffle') }}</span>
          </button>
          <button type="button" role="menuitem" @click="runCollectionAction('download')">
            <q-icon name="download" /><span>{{ $t('ui.actionWords.download') }}</span>
          </button>
          <button type="button" role="menuitem" @click="runCollectionAction('share')">
            <q-icon name="ios_share" /><span>{{ $t('ui.actionWords.share') }}</span>
          </button>
        </div>
      </section>
    </div>
  </Teleport>
</template>
