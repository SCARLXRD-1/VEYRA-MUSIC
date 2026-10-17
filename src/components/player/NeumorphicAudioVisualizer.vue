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
import { onBeforeUnmount, onMounted, ref, watch } from 'vue';

export default {
  name: 'NeumorphicAudioVisualizer',
  props: {
    app: { type: Object, default: null },
    barsCount: { type: Number, default: 5 },
    isPlaying: { type: Boolean, default: false },
    size: { type: String, default: 'md' } // 'sm' | 'md'
  },
  setup(props) {
    const bars = ref(Array.from({ length: props.barsCount }, () => 0.12));
    let animationFrameId = null;
    let lastFrameTime = 0;
    const targetFps = 35; // Throttled para rendimiento ultra-eficiente
    const frameInterval = 1000 / targetFps;

    // Decaimiento y picos suavizados
    const currentLevels = Array.from({ length: props.barsCount }, () => 0.12);

    function tick(currentTime) {
      if (!props.isPlaying) {
        // En reposo, descender suavemente a nivel mínimo
        let stillActive = false;
        for (let i = 0; i < props.barsCount; i++) {
          if (currentLevels[i] > 0.1) {
            currentLevels[i] = Math.max(0.08, currentLevels[i] * 0.85);
            stillActive = true;
          }
        }
        bars.value = [...currentLevels];
        if (stillActive) {
          animationFrameId = requestAnimationFrame(tick);
        } else {
          animationFrameId = null;
        }
        return;
      }

      const elapsed = currentTime - lastFrameTime;
      if (elapsed >= frameInterval) {
        lastFrameTime = currentTime - (elapsed % frameInterval);

        // Intenta obtener datos de espectro real del motor de audio
        let rawSpectrum = null;
        if (props.app?.audioSpectrum) {
          try {
            rawSpectrum = props.app.audioSpectrum(props.barsCount);
          } catch {
            rawSpectrum = null;
          }
        }

        const isRawActive = rawSpectrum && rawSpectrum.some(v => v > 0.05);

        for (let i = 0; i < props.barsCount; i++) {
          let targetLevel = 0.1;
          if (isRawActive) {
            targetLevel = Math.max(0.1, Math.min(1.0, rawSpectrum[i] * 1.25));
          } else {
            // Pulso rítmico orgánico si no hay elemento web audio conectado directamente
            const timeSec = currentTime * 0.003;
            const sineWave = Math.sin(timeSec * 2.8 + i * 1.1);
            const cosWave = Math.cos(timeSec * 1.9 + i * 0.7);
            targetLevel = Math.max(0.15, Math.min(0.85, 0.45 + (sineWave * 0.28) + (cosWave * 0.18)));
          }

          // Ataque rápido, caída suave
          if (targetLevel > currentLevels[i]) {
            currentLevels[i] = targetLevel;
          } else {
            currentLevels[i] = Math.max(0.08, currentLevels[i] * 0.88);
          }
        }

        bars.value = [...currentLevels];
      }

      animationFrameId = requestAnimationFrame(tick);
    }

    function startLoop() {
      if (!animationFrameId && !document.hidden) {
        lastFrameTime = performance.now();
        animationFrameId = requestAnimationFrame(tick);
      }
    }

    function stopLoop() {
      if (animationFrameId) {
        cancelAnimationFrame(animationFrameId);
        animationFrameId = null;
      }
    }

    function onVisibilityChange() {
      if (document.hidden) {
        stopLoop();
      } else if (props.isPlaying) {
        startLoop();
      }
    }

    watch(() => props.isPlaying, (playing) => {
      if (playing) startLoop();
    });

    onMounted(() => {
      document.addEventListener('visibilitychange', onVisibilityChange);
      if (props.isPlaying) startLoop();
    });

    onBeforeUnmount(() => {
      document.removeEventListener('visibilitychange', onVisibilityChange);
      stopLoop();
    });

    return {
      bars
    };
  }
};
</script>

<template>
  <div
    class="neu-visualizer"
    :class="[`neu-visualizer--${size}`, { 'neu-visualizer--active': isPlaying }]"
    aria-hidden="true"
    :title="isPlaying ? 'Ecualizador en vivo' : 'En reposo'"
  >
    <div
      v-for="(level, idx) in bars"
      :key="idx"
      class="neu-visualizer__socket"
    >
      <div
        class="neu-visualizer__bar"
        :style="{ height: `${Math.round(level * 100)}%` }"
      />
    </div>
  </div>
</template>

<style scoped>
.neu-visualizer {
  display: inline-flex;
  align-items: flex-end;
  gap: 3px;
  padding: 3px 5px;
  background: #090E18;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.04);
  box-shadow: 
    inset 1.5px 1.5px 3px rgba(0, 0, 0, 0.9),
    inset -1px -1px 2px rgba(255, 255, 255, 0.025),
    0 1px 0 rgba(255, 255, 255, 0.02);
  flex-shrink: 0;
  vertical-align: middle;
}

.neu-visualizer--sm {
  height: 16px;
  gap: 2.5px;
  padding: 2px 4px;
}

.neu-visualizer--md {
  height: 20px;
  gap: 3px;
  padding: 3px 5px;
}

.neu-visualizer__socket {
  position: relative;
  display: flex;
  align-items: flex-end;
  height: 100%;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.03);
  overflow: hidden;
}

.neu-visualizer--sm .neu-visualizer__socket {
  width: 3.5px;
}

.neu-visualizer--md .neu-visualizer__socket {
  width: 4px;
}

.neu-visualizer__bar {
  width: 100%;
  min-height: 2px;
  border-radius: 999px;
  background: linear-gradient(
    to top,
    var(--wave-accent-start, #1E6CEB) 0%,
    var(--wave-accent-end, #38BDF8) 100%
  );
  box-shadow: 0 0 6px rgba(var(--player-accent-soft-rgb, 56, 189, 248), 0.45);
  transition: height 0.08s ease-out;
  transform: translateZ(0);
}

.neu-visualizer--active .neu-visualizer__bar {
  opacity: 0.95;
}

.neu-visualizer:not(.neu-visualizer--active) .neu-visualizer__bar {
  opacity: 0.35;
  filter: grayscale(0.5);
}
</style>
