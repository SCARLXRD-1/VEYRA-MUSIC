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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
 */

const AUDIO_ENGINE_STORAGE_KEY = 'veyra:audio-engine';
const LEGACY_AUDIO_ENGINE_STORAGE_KEY = 'orchard:audio-engine';

function storedValue(target, key) {
  try {
    return target.localStorage.getItem(key);
  } catch {
    return null;
  }
}

export function welcomeSettingsSnapshot(ctx, target = window) {
  return {
    userPreferences: storedValue(target, ctx.USER_PREFERENCES_STORAGE_KEY) || storedValue(target, 'orchard:user-preferences'),
    audioEngine: storedValue(target, AUDIO_ENGINE_STORAGE_KEY) || storedValue(target, LEGACY_AUDIO_ENGINE_STORAGE_KEY)
  };
}

export function bindSettingsStorageSync(ctx, target = window, bridge = (typeof window !== 'undefined' ? (window.veyraApp || window.orchardApp) : null)) {
  const synchronizedKeys = new Set([
    ctx.USER_PREFERENCES_STORAGE_KEY,
    'orchard:user-preferences',
    AUDIO_ENGINE_STORAGE_KEY,
    LEGACY_AUDIO_ENGINE_STORAGE_KEY
  ]);
  const applySettings = () => ctx.applyImportedPreferences();
  const handleStorage = (event) => {
    if (!synchronizedKeys.has(event?.key)) return;
    applySettings();
  };

  target.addEventListener('storage', handleStorage);
  const clearBridgeListener = bridge?.onSettingsSync?.(applySettings);
  return () => {
    target.removeEventListener('storage', handleStorage);
    clearBridgeListener?.();
  };
}
