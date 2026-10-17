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

// Registers application-level IPC that is safe to expose through the audited preload surface.
import { IPC_CHANNELS } from '../../shared/ipcChannels.js';

const { APP, DISCORD, SONG_LINKS } = IPC_CHANNELS;

function validSerializedJson(value) {
  if (typeof value !== 'string' || value.length > 1_000_000) return null;
  try {
    JSON.parse(value);
    return value;
  } catch {
    return null;
  }
}

export function registerAppHandlers({
  app,
  clearDiscordPresence,
  completeWelcome = () => {},
  graphicsMode,
  getMainWindow = () => null,
  ipcMain,
  isDev,
  resolveDiscordSongLink,
  resolveDiscordSongLinkDetails,
  licensePath,
  shell,
  setDiscordPresence,
  showMainWindow,
  showWelcomeWindow,
  resetWelcome = () => {}
}) {
  ipcMain.handle(DISCORD.SET_PRESENCE, async (_event, presence) => {
    await setDiscordPresence(presence);
  });
  ipcMain.handle(DISCORD.CLEAR_PRESENCE, async () => {
    await clearDiscordPresence();
  });
  ipcMain.handle(SONG_LINKS.RESOLVE, async (_event, presence) => {
    return presence?.includeDetails ? resolveDiscordSongLinkDetails(presence) : resolveDiscordSongLink(presence);
  });
  ipcMain.handle(APP.DIAGNOSTICS, () => ({
    version: app.getVersion(),
    platform: process.platform,
    arch: process.arch,
    chrome: process.versions.chrome,
    electron: process.versions.electron,
    node: process.versions.node,
    dev: isDev
  }));
  ipcMain.handle(APP.GRAPHICS_MODE, (_event, value) => {
    return value === undefined ? graphicsMode.state() : graphicsMode.setMode(value);
  });
  ipcMain.handle(APP.RESTART, () => {
    graphicsMode.restart();
  });
  ipcMain.handle(APP.FINISH_WELCOME, async (_event, settings = {}) => {
    completeWelcome();
    const target = getMainWindow();
    if (target && !target.isDestroyed()) {
      const synchronizedSettings = JSON.stringify({
        userPreferences: validSerializedJson(settings?.userPreferences),
        audioEngine: validSerializedJson(settings?.audioEngine)
      });
      await target.webContents.executeJavaScript(`((settings) => {
        if (settings.userPreferences !== null) {
          localStorage.setItem('veyra:user-preferences', settings.userPreferences);
          localStorage.setItem('orchard:user-preferences', settings.userPreferences);
        }
        if (settings.audioEngine !== null) {
          localStorage.setItem('veyra:audio-engine', settings.audioEngine);
          localStorage.setItem('orchard:audio-engine', settings.audioEngine);
        }
        for (const key of ['veyra:setup-state', 'orchard:setup-state']) {
          let state = {};
          try { state = JSON.parse(localStorage.getItem(key) || '{}'); } catch {}
          localStorage.setItem(key, JSON.stringify({ ...state, completed: true, welcomeCompleted: true }));
        }
      })(${synchronizedSettings})`).catch(() => {});
      const isMounted = await target.webContents.executeJavaScript(`(() => {
        try { return Boolean(document.querySelector('#app')?.childElementCount); } catch { return true; }
      })()`).catch(() => true);
      if (!isMounted && typeof target.webContents.reload === 'function') {
        target.webContents.reload();
      }
      target.webContents.send(APP.SYNC_SETTINGS);
    }
    showMainWindow();
  });
  ipcMain.handle(APP.SHOW_WELCOME, async (_event, options = {}) => {
    if (options?.resetCompletion === true) {
      await resetWelcome();
      const target = getMainWindow();
      if (target && !target.isDestroyed()) {
        await target.webContents.executeJavaScript(`(() => {
          for (const key of ['veyra:setup-state', 'orchard:setup-state']) {
            let state = {};
            try { state = JSON.parse(localStorage.getItem(key) || '{}'); } catch {}
            localStorage.setItem(key, JSON.stringify({ ...state, completed: false, welcomeCompleted: false }));
          }
        })()`).catch(() => {});
      }
    }
    await showWelcomeWindow();
    return true;
  });
  ipcMain.handle(APP.VIEW_LICENSE, async () => {
    const error = await shell.openPath(licensePath);
    if (error) throw new Error(error);
  });
}
