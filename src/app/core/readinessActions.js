/*
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
 */

import { computed, ref } from 'vue';
import { t } from '../i18n/index.js';
import { readPinnedTracks } from '../browse/pinsPersistence.js';
import { copyTextToClipboard } from '../platform/clipboardText.js';
import { readPlaybackState } from '../playback/queuePersistence.js';
import { welcomeSettingsSnapshot } from './settingsStorageSync.js';

const SETUP_STORAGE_KEY = 'veyra:setup-state';
const LAST_RUN_VERSION_STORAGE_KEY = 'veyra:last-run-version';
const WELCOME_MODE_STORAGE_KEY = 'veyra:welcome-mode';
export const WELCOME_MODE_CANOPY = 'canopy-upgrade';
const CANOPY_MAJOR = 4;
const BACKUP_SCHEMA_VERSION = 1;
const STORAGE_KEYS = [
  'veyra:user-preferences',
  'veyra:playback-state',
  'veyra:pinned-tracks',
  'veyra:replay-events',
  'veyra:session-history',
  'veyra:audio-engine',
  'veyra:support-identity',
  SETUP_STORAGE_KEY,
  'orchard:user-preferences',
  'orchard:playback-state',
  'orchard:pinned-tracks',
  'orchard:replay-events',
  'orchard:session-history',
  'orchard:audio-engine',
  'orchard:support-identity',
  'orchard:setup-state'
];

function canStore() {
  return typeof window !== 'undefined' && Boolean(window.localStorage);
}

function readStoredJson(key, fallback) {
  if (!canStore()) return fallback;

  try {
    const legacyKey = key.replace(/^veyra:/, 'orchard:');
    const val = window.localStorage.getItem(key) ?? (legacyKey !== key ? window.localStorage.getItem(legacyKey) : null);
    return JSON.parse(val || JSON.stringify(fallback));
  } catch {
    return fallback;
  }
}

function writeStoredJson(key, value) {
  if (!canStore()) return;

  try {
    const serialized = JSON.stringify(value);
    window.localStorage.setItem(key, serialized);
    if (key.startsWith('veyra:')) {
      window.localStorage.setItem(key.replace(/^veyra:/, 'orchard:'), serialized);
    }
  } catch {
  }
}

function majorVersion(version) {
  const major = Number.parseInt(String(version || '').trim().replace(/^v/, ''), 10);
  return Number.isFinite(major) && major > 0 ? major : 0;
}

// Canopy landed in 4.0.0 and is opt-in, so every 3.x listener reaching any 4.x
// build -- not only 4.0.0 -- deserves one look at the choice. Nothing about the
// upgrade invalidates their session, so their sign-in is left alone.
export function canopyUpgradeAvailable(previousVersion, currentVersion) {
  const previous = majorVersion(previousVersion);
  const current = majorVersion(currentVersion);
  if (!previous || !current) return false;
  return previous < CANOPY_MAJOR && current >= CANOPY_MAJOR;
}

function readArrayStorage(key) {
  const value = readStoredJson(key, []);
  return Array.isArray(value) ? value : [];
}

function storageSnapshot() {
  if (!canStore()) return {};

  return Object.fromEntries(
    STORAGE_KEYS
      .map((key) => [key, window.localStorage.getItem(key)])
      .filter(([, value]) => value !== null)
  );
}

function storageByteCount(snapshot = {}) {
  return Object.values(snapshot).reduce((total, value) => total + String(value || '').length, 0);
}

function formatTime(timestamp) {
  if (!timestamp) return t('ui.readiness.never');
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit'
  }).format(new Date(timestamp));
}

function downloadJson(filename, data) {
  const blob = new Blob([`${JSON.stringify(data, null, 2)}\n`], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  anchor.click();
  URL.revokeObjectURL(url);
}

function readFileAsText(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result || ''));
    reader.onerror = () => reject(reader.error || new Error('Could not read backup file.'));
    reader.readAsText(file);
  });
}

function statusTone(status) {
  if (status === 'ok') return t('ui.readiness.ready');
  if (status === 'error') return t('ui.readiness.needsAttention');
  return t('ui.readiness.review');
}

export function installReadinessActions(ctx) {
  ctx.setupState = ref({
    completed: false,
    welcomeCompleted: false,
    appearanceReviewed: false,
    audioEngineReviewed: false,
    connectReviewed: false,
    discordReviewed: false,
    updateChecked: false,
    backupCreatedAt: 0,
    ...readStoredJson(SETUP_STORAGE_KEY, {})
  });
  ctx.setupPanelOpen = ref(!ctx.setupState.value.completed);
  // Set before the welcome window is opened, so the fresh renderer that backs
  // that window reads it during its own setup.
  ctx.welcomeMode = ref(canStore() ? window.localStorage.getItem(WELCOME_MODE_STORAGE_KEY) || '' : '');
  ctx.diagnostics = ref({ generatedAt: 0, items: [], report: null });
  ctx.diagnosticsMessage = ref('');
  ctx.backupMessage = ref('');

  ctx.persistSetupState = function persistSetupState() {
    writeStoredJson(SETUP_STORAGE_KEY, ctx.setupState.value);
  };

  ctx.updateSetupState = function updateSetupState(patch = {}) {
    ctx.setupState.value = { ...ctx.setupState.value, ...patch };
    ctx.persistSetupState();
  };

  // Runs on every bridge connect. The version is recorded before the offer is
  // made, so a reconnect -- or a listener who closes the window without
  // choosing -- never sees it twice.
  ctx.reviewVersionUpgrade = function reviewVersionUpgrade() {
    if (!canStore()) return false;

    try {
      const previous = window.localStorage.getItem(LAST_RUN_VERSION_STORAGE_KEY) || '';
      window.localStorage.setItem(LAST_RUN_VERSION_STORAGE_KEY, ctx.appVersion);
      if (!canopyUpgradeAvailable(previous, ctx.appVersion)) return false;
      // Someone mid-setup gets the full welcome instead; the layout choice is
      // already one of its steps.
      if (!ctx.setupState.value.welcomeCompleted) return false;

      window.localStorage.setItem(WELCOME_MODE_STORAGE_KEY, WELCOME_MODE_CANOPY);
      window.localStorage.setItem('orchard:welcome-mode', WELCOME_MODE_CANOPY);
      ctx.welcomeMode.value = WELCOME_MODE_CANOPY;
      (window.veyraApp || window.orchardApp)?.showWelcome?.();
      return true;
    } catch {
      return false;
    }
  };

  ctx.dismissWelcomeMode = function dismissWelcomeMode() {
    ctx.welcomeMode.value = '';
    if (!canStore()) return;

    try {
      window.localStorage.removeItem(WELCOME_MODE_STORAGE_KEY);
      window.localStorage.removeItem('orchard:welcome-mode');
    } catch {
      // A stale mode only costs one extra layout prompt.
    }
  };

  ctx.setupProgress = computed(() => {
    const items = ctx.setupItems.value;
    const done = items.filter((item) => item.done).length;
    return { done, total: items.length, label: `${done}/${items.length}` };
  });

  ctx.setupItems = computed(() => [
    {
      key: 'signin',
      icon: 'login',
      title: t('ui.readiness.signinTitle'),
      detail: ctx.authState.value.signedIn ? t('ui.readiness.accountConnected') : t('ui.readiness.accountRequired'),
      done: ctx.authState.value.signedIn,
      action: () => ctx.startLogin()
    },
    {
      key: 'updates',
      icon: 'system_update_alt',
      title: t('ui.readiness.updatesTitle'),
      detail: ctx.updateState.value.message || t('ui.readiness.updatesDetail'),
      done: Boolean(ctx.setupState.value.updateChecked),
      action: async () => {
        await ctx.checkForUpdates();
        ctx.updateSetupState({ updateChecked: true });
      }
    },
    {
      key: 'appearance',
      icon: 'palette',
      title: t('ui.readiness.appearanceTitle'),
      detail: t('ui.readiness.appearanceDetail'),
      done: Boolean(ctx.setupState.value.appearanceReviewed),
      action: () => {
        ctx.updateSetupState({ appearanceReviewed: true });
        document.getElementById('settings-appearance')?.scrollIntoView({ behavior: 'smooth' });
      }
    },
    {
      key: 'audio-engine',
      icon: 'equalizer',
      title: t('ui.readiness.audioTitle'),
      detail: ctx.audioEngineConfig.value.autoEqEnabled
        ? t('ui.readiness.autoEqReady')
        : (ctx.audioEngineConfig.value.eqEnabled
          ? t('ui.readiness.eqActive', { preset: ctx.audioEngineActivePreset.value === 'custom' ? t('ui.readiness.custom') : t(`settings.ui.optionLabels.eqPresets.${ctx.audioEngineActivePreset.value}`) })
          : t('ui.readiness.chooseEq')),
      done: Boolean(ctx.setupState.value.audioEngineReviewed),
      action: () => {
        ctx.updateSetupState({ audioEngineReviewed: true });
        document.getElementById('settings-audio-engine')?.scrollIntoView({ behavior: 'smooth' });
      }
    },
    {
      key: 'connect',
      icon: 'phonelink',
      title: t('ui.readiness.connectTitle'),
      detail: (ctx.veyraConnect?.value || ctx.orchardConnect?.value)?.devices?.length
        ? (ctx.veyraConnect?.value || ctx.orchardConnect?.value).devices.length === 1
          ? t('ui.readiness.onePhonePaired')
          : t('ui.readiness.pairedPhones', { count: (ctx.veyraConnect?.value || ctx.orchardConnect?.value).devices.length })
        : t('ui.readiness.pairPhone'),
      done: ((ctx.veyraConnect?.value || ctx.orchardConnect?.value)?.devices?.length || 0) > 0 || Boolean(ctx.setupState.value.connectReviewed),
      action: async () => {
        await (ctx.loadVeyraConnectInfo || ctx.loadOrchardConnectInfo)({ refresh: true });
        ctx.updateSetupState({ connectReviewed: true });
        document.getElementById('settings-connect')?.scrollIntoView({ behavior: 'smooth' });
      }
    },
    {
      key: 'discord',
      icon: 'extension',
      title: t('ui.readiness.discordTitle'),
      detail: ctx.discordRpcEnabled.value ? t('ui.readiness.discordEnabled') : t('ui.readiness.discordOff'),
      done: Boolean(ctx.setupState.value.discordReviewed),
      action: () => {
        ctx.updateSetupState({ discordReviewed: true });
        document.getElementById('settings-integrations')?.scrollIntoView({ behavior: 'smooth' });
      }
    },
    {
      key: 'backup',
      icon: 'archive',
      title: t('ui.readiness.backupTitle'),
      detail: t('ui.readiness.lastBackup', { time: formatTime(ctx.setupState.value.backupCreatedAt) }),
      done: Boolean(ctx.setupState.value.backupCreatedAt),
      action: () => (ctx.exportVeyraBackup || ctx.exportOrchardBackup)()
    }
  ]);

  ctx.finishSetup = function finishSetup() {
    ctx.updateSetupState({ completed: true });
    ctx.setupPanelOpen.value = false;
  };

  ctx.completeWelcomeSetup = async function completeWelcomeSetup() {
    if (!ctx.authState.value.signedIn) {
      ctx.errorMessage.value = t('ui.readiness.signInBeforeOpen');
      return false;
    }

    ctx.updateSetupState({ completed: true, welcomeCompleted: true });
    ctx.setupPanelOpen.value = false;
    ctx.dismissWelcomeMode();
    try {
      const appBridge = window.veyraApp || window.orchardApp;
      if (typeof appBridge?.finishWelcome !== 'function') {
        throw new Error(t('ui.readiness.desktopBridgeUnavailable'));
      }
      await appBridge.finishWelcome(welcomeSettingsSnapshot(ctx));
      return true;
    } catch (error) {
      ctx.errorMessage.value = error?.message || t('ui.readiness.cannotOpenApp');
      return false;
    }
  };

  ctx.reopenSetup = function reopenSetup() {
    ctx.setupPanelOpen.value = true;
    ctx.updateSetupState({ completed: false });
  };

  ctx.reopenWelcomeWizard = async function reopenWelcomeWizard() {
    const showWelcome = window.orchardApp?.showWelcome || window.veyraApp?.showWelcome;
    if (typeof showWelcome !== 'function') {
      ctx.errorMessage.value = t('ui.readiness.welcomeUnavailable');
      return false;
    }

    const previousSetupState = { ...ctx.setupState.value };
    ctx.dismissWelcomeMode();
    ctx.updateSetupState({ welcomeCompleted: false });
    try {
      await showWelcome({ resetCompletion: true });
      return true;
    } catch (error) {
      ctx.setupState.value = previousSetupState;
      ctx.persistSetupState();
      ctx.errorMessage.value = error?.message || t('ui.readiness.reopenWelcomeFailed');
      return false;
    }
  };

  ctx.collectDiagnostics = async function collectDiagnostics() {
    if (ctx.socket.value?.connected) {
      try {
        await (ctx.loadVeyraConnectInfo || ctx.loadOrchardConnectInfo)();
      } catch {
        // The diagnostics list will still show the bridge state below.
      }
    }

    const storage = storageSnapshot();
    let appInfo = {};
    try {
      appInfo = await (window.veyraApp || window.orchardApp)?.diagnostics?.();
    } catch {
      appInfo = {};
    }

    const connectDevices = (ctx.veyraConnect?.value || ctx.orchardConnect?.value)?.devices || [];
    const serverUrl = (ctx.veyraConnect?.value || ctx.orchardConnect?.value)?.serverUrl;
    const connectStable = Boolean(serverUrl);
    const items = [
      {
        label: t('ui.readiness.desktopBridge'),
        status: ctx.socketState.value === 'connected' ? 'ok' : 'warning',
        detail: ctx.socketState.value
      },
      {
        label: t('ui.readiness.youtubeAccount'),
        status: ctx.authState.value.signedIn ? 'ok' : 'warning',
        detail: ctx.authState.value.signedIn ? t('ui.readiness.signedIn') : ctx.authState.value.status
      },
      {
        label: t('ui.readiness.updater'),
        status: ctx.updateState.value.status === 'error' ? 'error' : 'ok',
        detail: ctx.updateState.value.message || ctx.updateStatusLabel.value
      },
      {
        label: t('ui.readiness.veyraConnect'),
        status: connectStable ? 'ok' : 'warning',
        detail: connectStable ? serverUrl : t('ui.readiness.waitingBridge')
      },
      {
        label: t('ui.readiness.discordPresence'),
        status: ctx.discordRpcEnabled.value ? 'ok' : 'warning',
        detail: ctx.discordRpcEnabled.value ? t('ui.readiness.enabled') : t('ui.readiness.disabled')
      },
      {
        label: t('ui.readiness.localStorage'),
        status: canStore() ? 'ok' : 'error',
        detail: `${Object.keys(storage).length} keys, ${storageByteCount(storage)} bytes`
      },
      {
        label: t('ui.readiness.audioEngine'),
        status: typeof window.AudioContext === 'function' || typeof window.webkitAudioContext === 'function' ? 'ok' : 'warning',
        detail: ctx.audioEngineConfig.value.enabled
          ? `${ctx.audioEngineConfig.value.autoEqEnabled ? t('ui.readiness.automaticEq') : ctx.audioEngineConfig.value.eqEnabled ? t('ui.readiness.manualEq') : t('ui.readiness.direct')}, ${t(ctx.audioOutputDevices.value.length === 1 ? 'ui.readiness.oneOutputOption' : 'ui.readiness.outputOptions', { count: ctx.audioOutputDevices.value.length })}`
          : t('ui.readiness.bypassed')
      },
      {
        label: t('ui.readiness.audioFormats'),
        status: ctx.supportedAudioMimes().length ? 'ok' : 'warning',
        detail: ctx.supportedAudioMimes().map((item) => item.mimeType).join(', ') || t('ui.readiness.noReportedSupport')
      },
      {
        label: t('ui.readiness.videoFormats'),
        status: ctx.supportedVideoMimes().length ? 'ok' : 'warning',
        detail: ctx.supportedVideoMimes().slice(0, 3).map((item) => item.mimeType).join(', ') || t('ui.readiness.noReportedSupport')
      }
    ];

    const report = {
      generatedAt: new Date().toISOString(),
      app: {
        version: ctx.appVersion,
        platform: appInfo.platform || navigator.platform,
        chrome: appInfo.chrome || '',
        electron: appInfo.electron || '',
        node: appInfo.node || '',
        dev: Boolean(appInfo.dev)
      },
      state: {
        socket: ctx.socketState.value,
        auth: ctx.authState.value.status,
        update: ctx.updateState.value.status,
        connectDevices: connectDevices.length,
        activeView: ctx.activeView.value,
        activeTrack: ctx.activeTrack.value?.id || ''
      },
      storageKeys: Object.keys(storage),
      checks: items
    };

    ctx.diagnostics.value = { generatedAt: Date.now(), items, report };
    ctx.diagnosticsMessage.value = t('ui.readiness.diagnosticsRefreshed');
  };

  ctx.diagnosticTone = statusTone;

  ctx.copyDiagnostics = async function copyDiagnostics() {
    if (!ctx.diagnostics.value.report) await ctx.collectDiagnostics();
    const text = JSON.stringify(ctx.diagnostics.value.report, null, 2);
    try {
      await copyTextToClipboard(text);
      ctx.diagnosticsMessage.value = t('ui.readiness.reportCopied');
    } catch {
      ctx.diagnosticsMessage.value = t('ui.readiness.copyFailed');
    }
  };

  ctx.exportVeyraBackup = async function exportVeyraBackup() {
    const storage = storageSnapshot();
    let connectDevices = null;
    try {
      connectDevices = ctx.socket.value?.connected
        ? await ctx.emitWithReply('connect:devices-export')
        : null;
    } catch {
      connectDevices = null;
    }

    const backup = {
      app: 'veyra',
      schemaVersion: BACKUP_SCHEMA_VERSION,
      veyraVersion: ctx.appVersion,
      orchardVersion: ctx.appVersion,
      exportedAt: new Date().toISOString(),
      storage,
      connectDevices
    };
    const day = new Date().toISOString().slice(0, 10);
    downloadJson(`veyra-backup-${ctx.appVersion}-${day}.json`, backup);
    ctx.updateSetupState({ backupCreatedAt: Date.now() });
    ctx.backupMessage.value = t('ui.readiness.backupDownloaded');
  };
  ctx.exportOrchardBackup = ctx.exportVeyraBackup;

  ctx.applyImportedPreferences = function applyImportedPreferences() {
    const preferences = ctx.readUserPreferences();
    ctx.accentColorSource.value = preferences.accentColorSource;
    ctx.autoplayEnabled.value = preferences.autoplayEnabled;
    ctx.closeToTrayEnabled.value = preferences.closeToTrayEnabled;
    ctx.crossfadeEnabled.value = preferences.crossfadeEnabled;
    ctx.crossfadeMode.value = preferences.crossfadeMode;
    ctx.crossfadeSeconds.value = preferences.crossfadeSeconds;
    ctx.customArtistPagesEnabled.value = preferences.customArtistPagesEnabled;
    ctx.fullscreenLyricsVisible.value = preferences.fullscreenLyricsVisible;
    ctx.playbackStatePersistenceEnabled.value = preferences.playbackStatePersistenceEnabled;
    ctx.queueLayout.value = preferences.queueLayout;
    ctx.youtubeHistoryEnabled.value = preferences.youtubeHistoryEnabled;
    ctx.customAccentColor.value = preferences.customAccentColor;
    ctx.discordRpcEnabled.value = preferences.discordRpcEnabled;
    ctx.graphicsMode.value = preferences.graphicsMode;
    ctx.immersiveBackgroundsEnabled.value = preferences.immersiveBackgroundsEnabled;
    ctx.immersiveBackgroundIntensity.value = preferences.immersiveBackgroundIntensity;
    ctx.immersiveBackgroundMotion.value = preferences.immersiveBackgroundMotion;
    ctx.homeSectionOrder.value = preferences.homeSectionOrder;
    ctx.hiddenHomeSectionIds.value = preferences.hiddenHomeSectionIds;
    ctx.sidebarItemOrder.value = preferences.sidebarItemOrder;
    ctx.hiddenSidebarItemIds.value = preferences.hiddenSidebarItemIds;
    ctx.layoutPreset.value = preferences.layoutPreset;
    ctx.keepOldVersions.value = preferences.keepOldVersions;
    ctx.uiScale.value = preferences.uiScale;
    ctx.sponsorBlockMode.value = preferences.sponsorBlockMode;
    ctx.volumeNormalizationEnabled.value = preferences.volumeNormalizationEnabled;
    ctx.repeatMode.value = preferences.repeatMode;
    ctx.shuffleEnabled.value = preferences.shuffleEnabled;
    ctx.themePreference.value = preferences.themePreference;
    ctx.volume.value = preferences.volume;
    const audioEngineState = readStoredJson('veyra:audio-engine', null) || readStoredJson('orchard:audio-engine', null);
    if (audioEngineState?.config) ctx.audioEngineConfig.value = audioEngineState.config;
    if (audioEngineState?.trackGains) ctx.audioEngineTrackGains.value = audioEngineState.trackGains;
  };

  ctx.importVeyraBackup = async function importVeyraBackup(file) {
    if (!file) return;
    const backup = JSON.parse(await readFileAsText(file));
    if ((backup?.app !== 'orchard' && backup?.app !== 'veyra') || backup.schemaVersion !== BACKUP_SCHEMA_VERSION || !backup.storage) {
      throw new Error('That file is not a compatible Veyra Music backup.');
    }

    for (const [key, value] of Object.entries(backup.storage)) {
      if (STORAGE_KEYS.includes(key) && typeof value === 'string') {
        window.localStorage.setItem(key, value);
        if (key.startsWith('veyra:')) {
          window.localStorage.setItem(key.replace(/^veyra:/, 'orchard:'), value);
        } else if (key.startsWith('orchard:')) {
          window.localStorage.setItem(key.replace(/^orchard:/, 'veyra:'), value);
        }
      }
    }

    if (backup.connectDevices && ctx.socket.value?.connected) {
      await ctx.emitWithReply('connect:devices-import', backup.connectDevices);
      await (ctx.loadVeyraConnectInfo || ctx.loadOrchardConnectInfo)();
    }

    ctx.applyImportedPreferences();
    if (!ctx.playbackStatePersistenceEnabled.value) ctx.clearPlaybackState();
    const playbackState = ctx.playbackStatePersistenceEnabled.value
      ? readPlaybackState()
      : ctx.emptyPlaybackState();
    ctx.activeTrack.value = playbackState.activeTrack;
    ctx.queue.value = playbackState.queue;
    ctx.history.value = playbackState.history;
    ctx.shuffleSourceQueue.value = playbackState.shuffleSourceQueue;
    ctx.pinnedTracks.value = readPinnedTracks();
    ctx.replayEvents.value = readArrayStorage('veyra:replay-events') || readArrayStorage('orchard:replay-events');
    ctx.sessionHistory.value = readArrayStorage('veyra:session-history') || readArrayStorage('orchard:session-history');
    ctx.setupState.value = { ...ctx.setupState.value, ...readStoredJson(SETUP_STORAGE_KEY, {}) };
    ctx.backupMessage.value = t('ui.readiness.backupRestored');
  };
  ctx.importOrchardBackup = ctx.importVeyraBackup;
}
