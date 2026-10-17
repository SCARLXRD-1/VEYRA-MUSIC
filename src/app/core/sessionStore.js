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

// Where "where was I?" state is kept: the playback queue and the last page.
//
// Both used to be written straight to localStorage, which Chromium flushes to
// disk on its own schedule -- so a force quit, an OS shutdown or a crash could
// drop the newest writes and bring the app back a song or a page behind. In the
// desktop app these now go through the main process, which owns an atomic file
// write and flushes it before the app exits.
//
// localStorage stays as the fallback for builds without the bridge, and as the
// migration path: an existing install reads its old value once, and the next
// change writes it to the durable store.
function bridge() {
  return globalThis.window?.veyraSessionState || globalThis.window?.orchardSessionState || null;
}

function localStorageAvailable() {
  return typeof window !== 'undefined' && Boolean(window.localStorage);
}

export function readSessionValue(key, fallback = null) {
  const b = bridge();
  let stored = b?.get(key);
  if (stored === null || stored === undefined) {
    const legacyKey = key.replace(/^veyra:/, 'orchard:');
    if (legacyKey !== key) {
      stored = b?.get(legacyKey);
    }
  }

  if (stored !== null && stored !== undefined) {
    // Values are stored serialized (see writeSessionValue). Objects can still
    // come back from a store written before that, so both shapes are read.
    if (typeof stored !== 'string') return stored;
    try {
      return JSON.parse(stored);
    } catch {
      return fallback;
    }
  }

  if (!localStorageAvailable()) return fallback;
  try {
    const legacyKey = key.replace(/^veyra:/, 'orchard:');
    const raw = window.localStorage.getItem(key) ?? (legacyKey !== key ? window.localStorage.getItem(legacyKey) : null);
    return raw === null ? fallback : JSON.parse(raw);
  } catch {
    return fallback;
  }
}

export function writeSessionValue(key, value) {
  let serialized;
  try {
    serialized = JSON.stringify(value ?? null);
  } catch {
    // A value that will not serialize is a session we cannot restore, not a
    // navigation we should block.
    return;
  }

  const durable = bridge();
  if (durable) {
    // Serialized before it crosses the bridge, the way localStorage always
    // did. The IPC channel structured-clones its arguments, and app state
    // reaches here as reactive proxies, which clone throws on.
    try {
      durable.set(key, serialized);
      if (key.startsWith('veyra:')) {
        durable.set(key.replace(/^veyra:/, 'orchard:'), serialized);
      }
    } catch {
      // A value that will not serialize is a session we cannot restore, not a
      // navigation we should block.
    }
    return;
  }

  if (!localStorageAvailable()) return;
  try {
    window.localStorage.setItem(key, JSON.stringify(value));
    if (key.startsWith('veyra:')) {
      window.localStorage.setItem(key.replace(/^veyra:/, 'orchard:'), JSON.stringify(value));
    }
  } catch {
    // Restoring a session is useful, but storage failures must never block
    // playback or navigation.
  }
}

export function clearSessionValue(key) {
  const durable = bridge();
  durable?.set(key, null);
  if (key.startsWith('veyra:')) {
    durable?.set(key.replace(/^veyra:/, 'orchard:'), null);
  }

  if (!localStorageAvailable()) return;
  try {
    window.localStorage.removeItem(key);
    if (key.startsWith('veyra:')) {
      window.localStorage.removeItem(key.replace(/^veyra:/, 'orchard:'));
    }
  } catch {
    // Best-effort, as above.
  }
}
