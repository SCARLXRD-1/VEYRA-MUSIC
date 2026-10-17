<!--
 * Copyright (C) 2026 AKASHI DEV
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
  name: 'StatusPillBadge',
  props: {
    status: {
      type: String,
      default: 'info',
      validator: (val) => ['active', 'streaming', 'sync', 'primary', 'accent', 'warning', 'info', 'count', 'muted'].includes(val)
    },
    label: {
      type: [String, Number],
      default: ''
    },
    dot: {
      type: Boolean,
      default: true
    },
    pulse: {
      type: Boolean,
      default: false
    },
    size: {
      type: String,
      default: 'md',
      validator: (val) => ['sm', 'md'].includes(val)
    }
  }
};
</script>

<template>
  <span
    class="status-pill-badge"
    :class="[
      `status-pill-badge--${status}`,
      `status-pill-badge--${size}`,
      { 'status-pill-badge--pulse': pulse }
    ]"
  >
    <span v-if="dot" class="status-pill-dot" aria-hidden="true" />
    <slot>{{ label }}</slot>
  </span>
</template>
