<!--
 * Copyright (C) 2026 SFG545
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
import { computed, ref } from 'vue';

export default {
  name: 'NeumorphicKnob',
  props: {
    modelValue: { type: Number, default: 0 },
    min: { type: Number, default: -12 },
    max: { type: Number, default: 12 },
    step: { type: Number, default: 0.5 },
    defaultValue: { type: Number, default: 0 },
    label: { type: String, default: '' },
    unit: { type: String, default: 'dB' },
    size: { type: Number, default: 44 },
    disabled: { type: Boolean, default: false }
  },
  emits: ['update:modelValue', 'change'],
  setup(props, { emit }) {
    const isDragging = ref(false);
    let startY = 0;
    let startValue = 0;

    const normalizedValue = computed(() => {
      const val = Math.max(props.min, Math.min(props.max, props.modelValue));
      const range = props.max - props.min;
      return range === 0 ? 0 : (val - props.min) / range;
    });

    // Mapeo de ángulo: de -135 grados a +135 grados (270 grados de recorrido)
    const rotationAngle = computed(() => {
      return -135 + normalizedValue.value * 270;
    });

    function updateValue(newVal) {
      const clamped = Math.max(props.min, Math.min(props.max, newVal));
      const stepped = Math.round(clamped / props.step) * props.step;
      const precision = props.step.toString().includes('.') ? props.step.toString().split('.')[1].length : 0;
      const rounded = Number(stepped.toFixed(precision));
      emit('update:modelValue', rounded);
      emit('change', rounded);
    }

    function onMouseDown(e) {
      if (props.disabled) return;
      isDragging.value = true;
      startY = e.clientY;
      startValue = props.modelValue;

      const onMouseMove = (moveEvent) => {
        const deltaY = startY - moveEvent.clientY;
        const sensitivity = (props.max - props.min) / 160;
        updateValue(startValue + deltaY * sensitivity);
      };

      const onMouseUp = () => {
        isDragging.value = false;
        window.removeEventListener('mousemove', onMouseMove);
        window.removeEventListener('mouseup', onMouseUp);
      };

      window.addEventListener('mousemove', onMouseMove);
      window.addEventListener('mouseup', onMouseUp);
    }

    function onWheel(e) {
      if (props.disabled) return;
      const direction = e.deltaY < 0 ? 1 : -1;
      updateValue(props.modelValue + direction * props.step);
    }

    function onDoubleClick() {
      if (props.disabled) return;
      updateValue(props.defaultValue);
    }

    return {
      isDragging,
      rotationAngle,
      onMouseDown,
      onWheel,
      onDoubleClick
    };
  }
};
</script>

<template>
  <div
    class="neu-knob-wrapper"
    :class="{ 'neu-knob--disabled': disabled, 'neu-knob--dragging': isDragging }"
    @wheel.prevent="onWheel"
  >
    <div
      class="neu-knob"
      :style="{ width: `${size}px`, height: `${size}px` }"
      role="slider"
      :aria-valuemin="min"
      :aria-valuemax="max"
      :aria-valuenow="modelValue"
      :aria-label="label"
      :tabindex="disabled ? -1 : 0"
      @mousedown="onMouseDown"
      @dblclick="onDoubleClick"
    >
      <div class="neu-knob__socket">
        <div
          class="neu-knob__dial"
          :style="{ transform: `rotate(${rotationAngle}deg)` }"
        >
          <div class="neu-knob__notch" />
        </div>
      </div>
    </div>

    <div v-if="label" class="neu-knob__label">{{ label }}</div>
    <div class="neu-knob__readout">{{ modelValue > 0 ? `+${modelValue}` : modelValue }} {{ unit }}</div>
  </div>
</template>

<style scoped>
.neu-knob-wrapper {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  user-select: none;
}

.neu-knob {
  cursor: grab;
  position: relative;
  display: grid;
  place-items: center;
  outline: none;
}

.neu-knob--dragging .neu-knob {
  cursor: grabbing;
}

.neu-knob__socket {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #090E18;
  padding: 3px;
  box-shadow: 
    inset 2px 2px 5px rgba(0, 0, 0, 0.95),
    inset -1px -1px 3px rgba(255, 255, 255, 0.04),
    0 1px 0 rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.03);
}

.neu-knob__dial {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(145deg, #182338 0%, #0F1626 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 
    3px 3px 8px rgba(0, 0, 0, 0.65),
    -1.5px -1.5px 4px rgba(255, 255, 255, 0.04),
    inset 1px 1px 1px rgba(255, 255, 255, 0.12);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 3px;
  transition: transform 0.05s linear;
}

.neu-knob:hover .neu-knob__dial {
  background: linear-gradient(145deg, #1C2942 0%, #111A2E 100%);
  border-color: rgba(56, 189, 248, 0.3);
  box-shadow: 
    4px 4px 10px rgba(0, 0, 0, 0.75),
    0 0 10px rgba(var(--player-accent-soft-rgb, 56, 189, 248), 0.2);
}

.neu-knob__notch {
  width: 3px;
  height: 7px;
  border-radius: 999px;
  background: linear-gradient(to bottom, #FFFFFF, var(--wave-accent-end, #38BDF8));
  box-shadow: 0 0 5px rgba(var(--player-accent-soft-rgb, 56, 189, 248), 0.75);
}

.neu-knob__label {
  font-size: 10.5px;
  font-weight: 600;
  color: var(--muted, #94A3B8);
  text-align: center;
}

.neu-knob__readout {
  font-size: 10px;
  font-weight: 650;
  color: #F8FAFC;
  font-variant-numeric: tabular-nums;
  background: #090E18;
  padding: 1px 6px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.04);
  box-shadow: inset 1px 1px 2px rgba(0, 0, 0, 0.7);
}

.neu-knob--disabled {
  opacity: 0.4;
  pointer-events: none;
}
</style>
