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
import { computed, onMounted, onUnmounted, reactive, ref, watch, defineAsyncComponent } from 'vue';
import { gsap } from 'gsap';
import {
  AudioLines,
  Ban,
  CheckCircle2,
  CircleCheck,
  Globe2,
  Image,
  LayoutDashboard,
  LogIn,
  Minus,
  Disc3,
  PanelLeft,
  PanelsTopLeft,
  Play,
  QrCode,
  Radio,
  Smartphone,
  Sparkles,
  Volume2,
  VolumeX,
  Waves,
  X
} from '@lucide/vue';
import welcomeMusicUrl from '../../assets/welcome-lofi.mp3';
import { useI18n } from '../../app/i18n/index.js';

const SupportView = defineAsyncComponent(() => import('../views/SupportView.vue'));

export default {
  name: 'WelcomeWindow',
  components: {
    AudioLines,
    Ban,
    CheckCircle2,
    CircleCheck,
    Globe2,
    Image,
    LayoutDashboard,
    LogIn,
    Minus,
    Disc3,
    PanelLeft,
    PanelsTopLeft,
    Play,
    QrCode,
    Radio,
    Smartphone,
    Sparkles,
    SupportView,
    Volume2,
    VolumeX,
    Waves,
    X
  },
  props: { app: { type: Object, required: true } },
  setup(props) {
    const { t, locale, setLocale, availableLocales } = useI18n();
    const audioRef = ref(null);
    const rootElement = ref(null);
    const musicMuted = ref(false);
    const musicBlocked = ref(false);
    const stepIndex = ref(0);
    const supportOpen = ref(false);
    const completionError = ref('');
    const completionPending = ref(false);
    const stepTweens = new Set();
    let welcomeMotion;
    const FULL_STEPS = [
      { key: 'account', icon: Disc3, titleKey: 'welcome.steps.account' },
      { key: 'layout', icon: PanelsTopLeft, titleKey: 'welcome.steps.layout' },
      { key: 'immersive', icon: Image, titleKey: 'welcome.steps.immersive' },
      { key: 'sound', icon: AudioLines, titleKey: 'welcome.steps.sound' },
      { key: 'connected', icon: Radio, titleKey: 'welcome.steps.connected' },
      { key: 'finish', icon: CircleCheck, titleKey: 'welcome.steps.finish' }
    ];
    // Upgrading from 3.x is not a fresh setup: the listener is already signed
    // in and already configured, so the only thing worth their attention is the
    // layout that did not exist when they last chose one.
    const canopyUpgrade = computed(() => props.app.welcomeMode.value === 'canopy-upgrade');
    const steps = computed(() => (canopyUpgrade.value
      ? [{ key: 'layout', icon: PanelsTopLeft, title: t('welcome.steps.canopyUpgrade') }]
      : FULL_STEPS.map((s) => ({ ...s, title: t(s.titleKey) }))));
    const currentStep = computed(() => steps.value[stepIndex.value] || steps.value[0]);
    const accountReady = computed(() => props.app.authState.value.signedIn);
    const canGoNext = computed(() => canopyUpgrade.value || stepIndex.value > 0 || accountReady.value);
    const primaryLabel = computed(() => {
      if (stepIndex.value === steps.value.length - 1) return t('welcome.actions.openApp');
      if (stepIndex.value === 0) return accountReady.value ? t('common.continue') : t('common.signIn');
      return t('common.next');
    });

    async function playWelcomeMusic() {
      const audio = audioRef.value;
      if (!audio) return;

      audio.volume = 0.28;
      audio.muted = musicMuted.value;
      try {
        await audio.play();
        musicBlocked.value = false;
      } catch {
        musicBlocked.value = true;
      }
    }

    function toggleMusicMuted() {
      musicMuted.value = !musicMuted.value;
      if (audioRef.value) audioRef.value.muted = musicMuted.value;
      if (!musicMuted.value) void playWelcomeMusic();
    }

    async function primaryAction() {
      if (stepIndex.value === steps.value.length - 1) {
        completionError.value = '';
        completionPending.value = true;
        const completed = await props.app.completeWelcomeSetup();
        completionPending.value = false;
        if (!completed) completionError.value = props.app.errorMessage.value || 'Could not open Veyra Music.';
        return;
      }

      if (stepIndex.value === 0 && !accountReady.value) {
        await props.app.startLogin();
        return;
      }

      stepIndex.value = Math.min(stepIndex.value + 1, steps.value.length - 1);
    }

    function previousStep() {
      stepIndex.value = Math.max(0, stepIndex.value - 1);
    }

    function openSupport() {
      supportOpen.value = true;
      void props.app.loadSupportReports();
    }

    function animateStepEnter(element, done) {
      if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        done();
        return;
      }

      const tween = gsap.fromTo(element, { autoAlpha: 0, y: 14 }, {
        autoAlpha: 1,
        y: 0,
        duration: 0.34,
        ease: 'power2.out',
        onComplete: () => {
          stepTweens.delete(tween);
          done();
        }
      });
      stepTweens.add(tween);
    }

    function animateStepLeave(element, done) {
      if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        done();
        return;
      }

      const tween = gsap.to(element, {
        autoAlpha: 0,
        y: -8,
        duration: 0.16,
        ease: 'power1.in',
        onComplete: () => {
          stepTweens.delete(tween);
          done();
        }
      });
      stepTweens.add(tween);
    }

    function finishIfAlreadyReady() {
      // The upgrade prompt is shown to listeners who have already completed
      // setup, so this shortcut would close the window before they see it.
      if (canopyUpgrade.value) return;
      if (props.app.authState.value.signedIn && props.app.setupState.value.welcomeCompleted) {
        (window.veyraApp || window.orchardApp)?.finishWelcome?.();
      }
    }

    onMounted(() => {
      welcomeMotion = gsap.matchMedia();
      welcomeMotion.add('(prefers-reduced-motion: no-preference)', () => {
        gsap.timeline({ defaults: { ease: 'power2.out' } })
          .from('.welcome-window__brand', { autoAlpha: 0, x: -16, duration: 0.35, clearProps: 'all' })
          .from('.welcome-window__sound', { autoAlpha: 0, scale: 0.86, duration: 0.28, clearProps: 'all' }, '-=0.16')
          .from('.welcome-window__content', { autoAlpha: 0, y: 14, duration: 0.36, clearProps: 'all' }, '-=0.12')
          .from('.welcome-window__actions > *', { autoAlpha: 0, y: 8, duration: 0.28, stagger: 0.06, clearProps: 'all' }, '-=0.12');
      }, rootElement.value);
      finishIfAlreadyReady();
      void playWelcomeMusic();
    });

    onUnmounted(() => {
      stepTweens.forEach((tween) => tween.kill());
      stepTweens.clear();
      welcomeMotion?.revert();
    });

    watch(() => props.app.authState.value.signedIn, (signedIn) => {
      if (signedIn && !canopyUpgrade.value && stepIndex.value === 0) stepIndex.value = 1;
    });

    watch(() => [
      props.app.authState.value.signedIn,
      props.app.setupState.value.welcomeCompleted
    ], finishIfAlreadyReady);

    return {
      accountReady,
      app: reactive(props.app),
      audioRef,
      rootElement,
      canGoNext,
      canopyUpgrade,
      completionError,
      completionPending,
      CheckCircle2,
      CircleCheck,
      currentStep,
      LogIn,
      musicBlocked,
      musicMuted,
      PanelsTopLeft,
      PanelLeft,
      Play,
      Sparkles,
      Volume2,
      VolumeX,
      Waves,
      locale,
      availableLocales,
      setLocale,
      openSupport,
      playWelcomeMusic,
      previousStep,
      primaryAction,
      primaryLabel,
      stepIndex,
      steps,
      supportOpen,
      toggleMusicMuted,
      animateStepEnter,
      animateStepLeave,
      welcomeMusicUrl,
      $t: t,
      t
    };
  }
};
</script>

<template>
  <main ref="rootElement" class="welcome-window" :class="`welcome-window--${currentStep.key}`">
    <audio ref="audioRef" :src="welcomeMusicUrl" loop autoplay preload="auto"></audio>

    <div class="welcome-window__chrome">
      <div class="welcome-window__language" role="group" :aria-label="$t('settings.language.label')">
        <Globe2 class="welcome-window__language-icon" :size="16" :stroke-width="2" aria-hidden="true" />
        <button
          v-for="option in availableLocales"
          :key="option.code"
          type="button"
          :class="{ 'welcome-window__language-option--active': locale === option.code }"
          :aria-pressed="locale === option.code"
          :aria-label="option.label"
          :title="option.label"
          @click="setLocale(option.code)"
        >
          {{ option.code === 'en' ? 'EN' : 'ES' }}
        </button>
      </div>
      <button
        type="button"
        class="welcome-window__sound"
        :title="musicBlocked ? $t('welcome.actions.soundStart') : musicMuted ? $t('welcome.actions.soundUnmute') : $t('welcome.actions.soundMute')"
        :aria-label="musicBlocked ? $t('welcome.actions.soundStart') : musicMuted ? $t('welcome.actions.soundUnmute') : $t('welcome.actions.soundMute')"
        @click="musicBlocked ? playWelcomeMusic() : toggleMusicMuted()"
      >
        <component :is="musicBlocked ? Play : musicMuted ? VolumeX : Volume2" :size="18" :stroke-width="2" aria-hidden="true" />
      </button>
      <div class="welcome-window__window-controls">
        <button type="button" class="welcome-window__window-control" :title="$t('common.minimize')" @click="app.minimizeWindow">
          <Minus :size="18" :stroke-width="2" aria-hidden="true" />
        </button>
        <button type="button" class="welcome-window__window-control" :title="$t('common.close')" @click="app.closeWindow">
          <X :size="18" :stroke-width="2" aria-hidden="true" />
        </button>
      </div>
    </div>

    <section class="welcome-window__hero">
      <div class="welcome-window__hero-bg"></div>
      <div class="welcome-window__hero-content">
        <div class="welcome-window__brand">
          <img :src="app.veyraLogoUrl" alt="" />
          <h2>Veyra Music</h2>
        </div>
        
        <div class="welcome-window__tracker" :aria-label="$t('welcome.steps.account')">
          <button
            v-for="(step, index) in steps"
            :key="step.key"
            type="button"
            :aria-label="step.title"
            class="welcome-window__tracker-step"
            :class="{
              'welcome-window__tracker-step--active': index === stepIndex,
              'welcome-window__tracker-step--done': index < stepIndex
            }"
            @click="stepIndex = accountReady || index === 0 ? index : stepIndex"
          >
            <span class="welcome-window__tracker-icon-pod">
              <component :is="index < stepIndex ? CheckCircle2 : step.icon" class="welcome-window__tracker-icon" :size="18" :stroke-width="2" aria-hidden="true" />
            </span>
            <span>{{ step.title }}</span>
          </button>
        </div>
      </div>
    </section>

    <section class="welcome-window__panel" aria-labelledby="welcome-title">
      <div class="welcome-window__content-wrapper">
        <Transition :css="false" mode="out-in" @enter="animateStepEnter" @leave="animateStepLeave">
          <div :key="currentStep.key" class="welcome-window__content">
            <div class="welcome-window__step-icon-badge">
              <component :is="currentStep.icon" class="welcome-window__step-icon" :size="34" :stroke-width="2" aria-hidden="true" />
            </div>
            <h1 id="welcome-title">{{ currentStep.title }}</h1>

            <template v-if="currentStep.key === 'account'">
              <p>{{ $t('welcome.account.description') }}</p>
              <div class="welcome-window__signin">
                <button
                  type="button"
                  class="welcome-window__primary"
                  :disabled="app.authState.status === 'starting'"
                  @click="app.startLogin"
                >
                  <component :is="accountReady ? CircleCheck : LogIn" :size="18" :stroke-width="2" aria-hidden="true" />
                  {{ accountReady ? (app.authState.user?.name || $t('common.signedIn')) : app.authState.status === 'starting' ? $t('welcome.account.openingSignIn') : $t('welcome.account.signInButton') }}
                </button>
              </div>
              <div v-if="app.authState.pending" class="welcome-window__device">
                <strong>{{ app.authState.pending.userCode }}</strong>
                <span>{{ app.authState.pending.verificationUrl }}</span>
                <div>
                  <button type="button" @click="app.openVerification">{{ $t('common.openLink') }}</button>
                  <button type="button" @click="app.copyLoginText(app.authState.pending.userCode)">{{ $t('common.copyCode') }}</button>
                </div>
              </div>
            </template>

            <template v-else-if="currentStep.key === 'layout'">
              <p v-if="canopyUpgrade">
                {{ $t('welcome.layout.canopyDescription') }}
              </p>
              <p v-else>{{ $t('welcome.layout.description') }}</p>
              <div class="welcome-window__connect-grid">
                <button
                  v-for="option in app.layoutPresetOptions"
                  :key="option.value"
                  type="button"
                  class="welcome-window__sound-card"
                  :class="{ 'welcome-window__choice--active': app.layoutPreset === option.value }"
                  @click="app.layoutPreset = option.value"
                >
                  <span class="welcome-window__card-icon-pod">
                    <component :is="option.value === 'canopy' ? PanelsTopLeft : PanelLeft" :size="24" :stroke-width="2" aria-hidden="true" />
                  </span>
                  <strong>{{ option.value === 'canopy' ? $t('welcome.layout.canopyLabel') : $t('welcome.layout.groveLabel') }}</strong>
                  <span>{{ option.value === 'canopy' ? $t('welcome.layout.canopyDesc') : $t('welcome.layout.groveDesc') }}</span>
                </button>
              </div>
            </template>

            <template v-else-if="currentStep.key === 'immersive'">
              <p>{{ $t('welcome.immersive.description') }}</p>
              <div class="welcome-window__setting-row">
                <span>{{ $t('welcome.immersive.toggleLabel') }}</span>
                <q-toggle v-model="app.immersiveBackgroundsEnabled" color="primary" :aria-label="$t('welcome.immersive.toggleLabel')" />
              </div>
              <div class="welcome-window__options">
                <button
                  v-for="option in app.immersiveBackgroundMotionOptions"
                  :key="option.value"
                  type="button"
                  class="welcome-window__option"
                  :class="{ 'welcome-window__choice--active': app.immersiveBackgroundMotion === option.value }"
                  :disabled="!app.immersiveBackgroundsEnabled"
                  @click="app.immersiveBackgroundMotion = option.value"
                >
                  {{ option.value === 'static' ? $t('welcome.immersive.motion.static') : option.value === 'pan' ? $t('welcome.immersive.motion.pan') : $t('welcome.immersive.motion.dynamic') }}
                </button>
              </div>
            </template>

            <template v-else-if="currentStep.key === 'sound'">
              <p>{{ $t('welcome.sound.description') }}</p>
              <div class="welcome-window__sound-grid">
                <button
                  type="button"
                  class="welcome-window__sound-card"
                  :class="{ 'welcome-window__choice--active': !app.crossfadeEnabled }"
                  @click="app.crossfadeEnabled = false"
                >
                  <span class="welcome-window__card-icon-pod">
                    <Ban :size="24" :stroke-width="2" aria-hidden="true" />
                  </span>
                  <strong>{{ $t('welcome.sound.offTitle') }}</strong>
                  <span>{{ $t('welcome.sound.offDesc') }}</span>
                </button>
                <button
                  v-for="option in app.crossfadeModeOptions"
                  :key="option.value"
                  type="button"
                  class="welcome-window__sound-card"
                  :class="{ 'welcome-window__choice--active': app.crossfadeEnabled && app.crossfadeMode === option.value }"
                  @click="app.crossfadeEnabled = true; app.setCrossfadeMode(option.value)"
                >
                  <span class="welcome-window__card-icon-pod">
                    <component :is="option.value === 'smart' ? Sparkles : Waves" :size="24" :stroke-width="2" aria-hidden="true" />
                  </span>
                  <strong>{{ option.value === 'smart' ? $t('welcome.sound.smartTitle') : $t('welcome.sound.standardTitle') }}</strong>
                  <span>{{ option.value === 'smart' ? $t('welcome.sound.smartDesc') : $t('welcome.sound.standardDesc') }}</span>
                </button>
              </div>
              <div class="welcome-window__setting-row">
                <span>{{ $t('welcome.sound.autoplayLabel') }}</span>
                <q-toggle v-model="app.autoplayEnabled" color="primary" :aria-label="$t('welcome.sound.autoplayLabel')" />
              </div>
              <div class="welcome-window__setting-row">
                <span>{{ $t('welcome.sound.autoEqLabel') }}</span>
                <q-toggle
                  :model-value="app.audioEngineConfig.autoEqEnabled"
                  color="primary"
                  :aria-label="$t('welcome.sound.autoEqLabel')"
                  @update:model-value="app.setAutoEqEnabled"
                />
              </div>
              <div class="welcome-window__slider">
                <span>{{ $t('welcome.sound.durationLabel') }}</span>
                <q-slider v-model="app.crossfadeSeconds" :min="1" :max="12" :step="1" :disable="!app.crossfadeEnabled" color="primary" />
                <output>{{ app.crossfadeSeconds }}s</output>
              </div>
            </template>

            <template v-else-if="currentStep.key === 'connected'">
              <p>{{ $t('welcome.connected.description') }}</p>
              <div class="welcome-window__connect-grid">
                <div class="welcome-window__setting-row">
                  <span>{{ $t('welcome.connected.discordLabel') }}</span>
                  <q-toggle v-model="app.discordRpcEnabled" color="primary" :aria-label="$t('welcome.connected.discordLabel')" />
                </div>
                <div class="welcome-window__connect-panel">
                  <button type="button" class="welcome-window__secondary" :disabled="app.socketState !== 'connected'" @click="app.loadVeyraConnectInfo({ refresh: true })">
                    <QrCode :size="18" :stroke-width="2" aria-hidden="true" />
                    {{ $t('welcome.connected.pairPhone') }}
                  </button>
                  <div class="welcome-window__qr" v-html="app.veyraConnect.qrSvg"></div>
                  <span class="welcome-window__bridge-status">
                    {{ $t('welcome.connected.bridgeStatus', { status: app.socketState === 'connected' ? $t('common.online') : app.socketState }) }}
                  </span>
                </div>
              </div>
            </template>

            <template v-else>
              <p>{{ $t('welcome.finish.description') }}</p>
              <div class="welcome-window__summary">
                <span>
                  <span class="welcome-window__summary-icon-pod">
                    <CircleCheck :size="16" :stroke-width="2" aria-hidden="true" />
                  </span>
                  {{ app.authState.user?.name || $t('common.signedIn') }}
                </span>
                <span>
                  <span class="welcome-window__summary-icon-pod">
                    <LayoutDashboard :size="16" :stroke-width="2" aria-hidden="true" />
                  </span>
                  {{ app.layoutPreset === 'canopy' ? $t('welcome.finish.canopyActive') : $t('welcome.finish.groveActive') }}
                </span>
                <span>
                  <span class="welcome-window__summary-icon-pod">
                    <Image :size="16" :stroke-width="2" aria-hidden="true" />
                  </span>
                  {{ app.immersiveBackgroundsEnabled ? $t('welcome.finish.backgroundsOn', { motion: app.immersiveBackgroundMotion === 'static' ? $t('welcome.immersive.motion.static') : app.immersiveBackgroundMotion === 'pan' ? $t('welcome.immersive.motion.pan') : $t('welcome.immersive.motion.dynamic') }) : $t('welcome.finish.backgroundsOff') }}
                </span>
                <span>
                  <span class="welcome-window__summary-icon-pod">
                    <AudioLines :size="16" :stroke-width="2" aria-hidden="true" />
                  </span>
                  {{ app.audioEngineConfig.autoEqEnabled ? $t('welcome.finish.autoEqActive') : app.audioEngineConfig.eqEnabled ? $t('welcome.finish.manualEqActive') : $t('welcome.finish.eqOff') }}
                </span>
                <span>
                  <span class="welcome-window__summary-icon-pod">
                    <Smartphone :size="16" :stroke-width="2" aria-hidden="true" />
                  </span>
                  {{ $t('welcome.finish.devicesCount', { count: app.veyraConnect.devices.length }) }}
                </span>
              </div>
              <p v-if="musicBlocked" class="welcome-window__note">{{ $t('welcome.finish.blockedAudioNote') }}</p>
            </template>
          </div>
        </Transition>
      </div>

      <footer class="welcome-window__actions">
        <button type="button" class="welcome-window__secondary" @click="openSupport">{{ $t('common.support') }}</button>
        <div>
          <button type="button" class="welcome-window__secondary" :disabled="stepIndex === 0" @click="previousStep">{{ $t('common.previous') }}</button>
          <button type="button" class="welcome-window__primary" :disabled="!canGoNext || completionPending" @click="primaryAction">
            {{ completionPending ? $t('welcome.actions.openingApp') : primaryLabel }}
          </button>
        </div>
      </footer>
      <p v-if="completionError" class="welcome-window__completion-error" role="alert">{{ completionError }}</p>
    </section>
  </main>

  <q-dialog v-model="supportOpen" maximized>
    <div class="welcome-support-dialog">
      <button type="button" class="welcome-support-dialog__close" :title="$t('common.close')" @click="supportOpen = false"><X :size="18" :stroke-width="2" aria-hidden="true" /></button>
      <SupportView :app="app" />
    </div>
  </q-dialog>
</template>
