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
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';

export default {
  name: 'NeumorphicWaveSlider',
  props: {
    modelValue: { type: Number, default: 0 },
    min: { type: Number, default: 0 },
    max: { type: Number, default: 1 },
    step: { type: Number, default: 1 },
    disable: { type: Boolean, default: false },
    isPlaying: { type: Boolean, default: false },
    ariaLabel: { type: String, default: 'Progreso de reproducción' }
  },
  emits: ['update:modelValue', 'change', 'pan'],
  setup(props, { emit }) {
    const sliderRef = ref(null);
    const trackWidth = ref(300);
    const trackHeight = 16;
    const trackCenterY = trackHeight / 2;

    const isDragging = ref(false);
    const dragValue = ref(0);

    const currentDisplayValue = computed(() => {
      return isDragging.value ? dragValue.value : props.modelValue;
    });

    const progressRatio = computed(() => {
      const range = Math.max(1, props.max - props.min);
      const val = Math.max(props.min, Math.min(props.max, currentDisplayValue.value));
      return (val - props.min) / range;
    });

    const progressPercent = computed(() => {
      return Math.max(0, Math.min(100, progressRatio.value * 100));
    });

    const thumbX = computed(() => {
      return progressRatio.value * trackWidth.value;
    });

    // Parámetros de animación de onda fluida.
    let animationFrameId = null;
    const wavePhase = ref(0);
    const waveAmplitude = ref(0);
    const prefersReducedMotion = ref(
      typeof window !== 'undefined' && Boolean(window.matchMedia?.('(prefers-reduced-motion: reduce)')?.matches)
    );
    const targetAmplitude = computed(() => (
      props.isPlaying && !props.disable && !prefersReducedMotion.value ? 4.2 : 0
    ));
    const wavelength = 32;

    function tick() {
      // Avanza fase si está reproduciendo y no tiene movimiento reducido.
      if (props.isPlaying && !props.disable && !prefersReducedMotion.value) {
        wavePhase.value = (wavePhase.value + 0.075) % (Math.PI * 2);
      }

      // Interpola suavemente amplitud hacia objetivo.
      const diff = targetAmplitude.value - waveAmplitude.value;
      if (Math.abs(diff) > 0.02) {
        waveAmplitude.value += diff * 0.12;
      } else {
        waveAmplitude.value = targetAmplitude.value;
      }

      // Detiene loop si reposo completo en pausa.
      if (!props.isPlaying && Math.abs(waveAmplitude.value) < 0.05) {
        waveAmplitude.value = 0;
        animationFrameId = null;
        return;
      }

      animationFrameId = requestAnimationFrame(tick);
    }

    function startAnimationLoop() {
      if (!animationFrameId && !document.hidden) {
        animationFrameId = requestAnimationFrame(tick);
      }
    }

    function stopAnimationLoop() {
      if (animationFrameId) {
        cancelAnimationFrame(animationFrameId);
        animationFrameId = null;
      }
    }

    watch(
      () => props.isPlaying,
      (playing) => {
        if (playing) startAnimationLoop();
        else startAnimationLoop(); // Permite desacelerar suavemente a 0.
      }
    );

    // Trazado de la onda sinusoidal en SVG.
    const wavePath = computed(() => {
      const endX = thumbX.value;
      const centerY = trackCenterY;
      if (endX <= 2) {
        return `M 0,${centerY} L ${Math.max(0, endX)},${centerY}`;
      }

      const amp = waveAmplitude.value;
      const phase = wavePhase.value;

      // Si no hay amplitud, dibuja línea recta con brillo láser.
      if (amp <= 0.05) {
        return `M 0,${centerY} L ${endX},${centerY}`;
      }

      let d = `M 0,${centerY}`;
      const stepPx = 4;
      const count = Math.floor(endX / stepPx);

      for (let i = 1; i <= count; i++) {
        const x = i * stepPx;
        // Amortiguador en extremos para transición limpia.
        const leadIn = Math.min(1, x / 14);
        const leadOut = Math.min(1, (endX - x) / 14);
        const envelope = leadIn * leadOut;
        const y = centerY + Math.sin((x / wavelength) * (Math.PI * 2) - phase) * (amp * envelope);
        d += ` L ${x.toFixed(1)},${y.toFixed(1)}`;
      }

      d += ` L ${endX.toFixed(1)},${centerY}`;
      return d;
    });

    // Cálculo de valor según posición del puntero.
    function getValueFromEvent(event) {
      if (!sliderRef.value) return props.modelValue;
      const rect = sliderRef.value.getBoundingClientRect();
      const x = Math.max(0, Math.min(rect.width, event.clientX - rect.left));
      const ratio = rect.width > 0 ? x / rect.width : 0;
      const rawValue = props.min + ratio * (props.max - props.min);
      const stepped = Math.round(rawValue / props.step) * props.step;
      return Math.max(props.min, Math.min(props.max, stepped));
    }

    function onPointerDown(event) {
      if (props.disable) return;
      isDragging.value = true;
      dragValue.value = getValueFromEvent(event);
      emit('pan', 'start');
      emit('update:modelValue', dragValue.value);

      try {
        sliderRef.value?.setPointerCapture?.(event.pointerId);
      } catch (_) {}
    }

    function onPointerMove(event) {
      if (!isDragging.value || props.disable) return;
      dragValue.value = getValueFromEvent(event);
      emit('update:modelValue', dragValue.value);
    }

    function onPointerUp(event) {
      if (!isDragging.value) return;
      try {
        sliderRef.value?.releasePointerCapture?.(event.pointerId);
      } catch (_) {}

      const finalValue = getValueFromEvent(event);
      isDragging.value = false;
      emit('change', finalValue);
      emit('pan', 'end');
    }

    function onKeydown(event) {
      if (props.disable) return;
      let delta = 0;
      if (event.key === 'ArrowLeft' || event.key === 'ArrowDown') delta = -5;
      else if (event.key === 'ArrowRight' || event.key === 'ArrowUp') delta = 5;
      else if (event.key === 'PageDown') delta = -30;
      else if (event.key === 'PageUp') delta = 30;
      else if (event.key === 'Home') delta = -props.modelValue;
      else if (event.key === 'End') delta = props.max - props.modelValue;
      else return;

      event.preventDefault();
      const nextVal = Math.max(props.min, Math.min(props.max, props.modelValue + delta));
      emit('update:modelValue', nextVal);
      emit('change', nextVal);
    }

    // Observador de dimensiones para ajuste automático.
    let resizeObserver = null;
    let handleVisibility = null;
    onMounted(() => {
      if (sliderRef.value) {
        trackWidth.value = sliderRef.value.getBoundingClientRect().width || 300;
        resizeObserver = new ResizeObserver((entries) => {
          for (const entry of entries) {
            if (entry.contentRect.width > 0) {
              trackWidth.value = entry.contentRect.width;
            }
          }
        });
        resizeObserver.observe(sliderRef.value);
      }

      startAnimationLoop();

      handleVisibility = () => {
        if (document.hidden) stopAnimationLoop();
        else if (props.isPlaying) startAnimationLoop();
      };
      document.addEventListener('visibilitychange', handleVisibility);
    });

    onBeforeUnmount(() => {
      stopAnimationLoop();
      resizeObserver?.disconnect();
      if (handleVisibility) {
        document.removeEventListener('visibilitychange', handleVisibility);
      }
    });

    return {
      sliderRef,
      trackWidth,
      trackHeight,
      trackCenterY,
      progressPercent,
      thumbX,
      wavePath,
      isDragging,
      onPointerDown,
      onPointerMove,
      onPointerUp,
      onKeydown
    };
  }
};
</script>

<template>
  <div
    ref="sliderRef"
    class="neu-wave-slider"
    :class="{
      'neu-wave-slider--disabled': disable,
      'neu-wave-slider--dragging': isDragging,
      'neu-wave-slider--playing': isPlaying
    }"
    role="slider"
    :tabindex="disable ? -1 : 0"
    :aria-label="ariaLabel"
    :aria-valuenow="modelValue"
    :aria-valuemin="min"
    :aria-valuemax="max"
    :aria-disabled="disable"
    @pointerdown="onPointerDown"
    @pointermove="onPointerMove"
    @pointerup="onPointerUp"
    @pointercancel="onPointerUp"
    @keydown="onKeydown"
  >
    <!-- Surco fresado en relieve profundo -->
    <div class="neu-wave-slider__socket">
      <svg
        class="neu-wave-slider__svg"
        :viewBox="`0 0 ${trackWidth} ${trackHeight}`"
        preserveAspectRatio="none"
        aria-hidden="true"
      >
        <defs>
          <linearGradient id="neuWaveGrad" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stop-color="var(--wave-accent-start, #1E6CEB)" />
            <stop offset="60%" stop-color="var(--wave-accent-mid, #257bf8)" />
            <stop offset="100%" stop-color="var(--wave-accent-end, #38BDF8)" />
          </linearGradient>
          <filter id="neuWaveGlowFilter" x="-10%" y="-50%" width="120%" height="200%">
            <feDropShadow dx="0" dy="0" stdDeviation="2.2" flood-color="var(--wave-accent-end, #38BDF8)" flood-opacity="0.65" />
          </filter>
        </defs>

        <!-- Guía de pista restante (unplayed) -->
        <line
          :x1="thumbX"
          :y1="trackCenterY"
          :x2="trackWidth"
          :y2="trackCenterY"
          class="neu-wave-slider__guide-line"
        />

        <!-- Trazo de onda activa (reproduced / wave) -->
        <path
          :d="wavePath"
          class="neu-wave-slider__wave-line"
          filter="url(#neuWaveGlowFilter)"
        />
      </svg>
    </div>

    <!-- Cabezal de dial táctil flotante (Thumb) -->
    <div
      class="neu-wave-slider__thumb"
      :style="{ left: `${progressPercent}%` }"
      aria-hidden="true"
    >
      <div class="neu-wave-slider__thumb-core" />
    </div>
  </div>
</template>

<style scoped>
.neu-wave-slider {
  --wave-accent-start: rgb(var(--player-accent-rgb, 30, 108, 235));
  --wave-accent-mid: rgb(var(--player-accent-rgb, 37, 123, 248));
  --wave-accent-end: rgb(var(--player-accent-soft-rgb, 56, 189, 248));
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  height: 24px;
  cursor: pointer;
  user-select: none;
  touch-action: none;
  outline: none;
}

.neu-wave-slider--disabled {
  opacity: 0.35;
  cursor: not-allowed;
  pointer-events: none;
}

/* Canal fresado en bajorrelieve profundo (Debossed Socket) */
.neu-wave-slider__socket {
  position: relative;
  width: 100%;
  height: 14px;
  border-radius: 999px;
  background: #080D16;
  border: 1px solid rgba(255, 255, 255, 0.035);
  box-shadow: 
    inset 2px 2px 5px rgba(0, 0, 0, 0.95),
    inset -1px -1px 2px rgba(255, 255, 255, 0.035),
    0 1px 0 rgba(255, 255, 255, 0.03);
  overflow: visible;
}

.neu-wave-slider__svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: visible;
  pointer-events: none;
}

/* Línea de pista restante en bajorrelieve sutil */
.neu-wave-slider__guide-line {
  stroke: rgba(255, 255, 255, 0.09);
  stroke-width: 3.5;
  stroke-linecap: round;
  transition: stroke 0.2s ease;
}

.neu-wave-slider:hover .neu-wave-slider__guide-line {
  stroke: rgba(255, 255, 255, 0.14);
}

/* Trazo de onda orgánica esculpida */
.neu-wave-slider__wave-line {
  fill: none;
  stroke: url(#neuWaveGrad);
  stroke-width: 3.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

/* Cabezal de dial táctil flotante con bisel especular */
.neu-wave-slider__thumb {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: linear-gradient(145deg, #FFFFFF, #94A3B8);
  border: 1.5px solid rgba(255, 255, 255, 0.95);
  box-shadow: 
    2px 2px 6px rgba(0, 0, 0, 0.85),
    -1px -1px 3px rgba(255, 255, 255, 0.35),
    0 0 10px rgba(var(--player-accent-soft-rgb, 56, 189, 248), 0.7);
  transition: transform 0.12s cubic-bezier(0.2, 0.8, 0.2, 1), box-shadow 0.15s ease;
  pointer-events: none;
  z-index: 2;
}

.neu-wave-slider__thumb-core {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--wave-accent-start, #1E6CEB);
  box-shadow: 0 0 5px var(--wave-accent-end, #38BDF8);
}

.neu-wave-slider:hover .neu-wave-slider__thumb,
.neu-wave-slider--dragging .neu-wave-slider__thumb {
  transform: translate(-50%, -50%) scale(1.22);
  box-shadow: 
    3px 3px 8px rgba(0, 0, 0, 0.9),
    -1px -1px 4px rgba(255, 255, 255, 0.45),
    0 0 14px rgba(var(--player-accent-soft-rgb, 56, 189, 248), 0.9);
}

.neu-wave-slider:focus-visible {
  border-radius: 999px;
  box-shadow: 0 0 0 2px rgba(56, 189, 248, 0.5);
}
</style>
