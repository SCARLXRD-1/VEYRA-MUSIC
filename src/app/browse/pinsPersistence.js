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

import { sanitizedTrack } from '../playback/queuePersistence.js';

const PINS_STORAGE_KEY = 'veyra:pinned-tracks';
const LEGACY_PINS_STORAGE_KEY = 'orchard:pinned-tracks';
const MAX_PINNED_TRACKS = 200;

export function readPinnedTracks() {
  if (typeof window === 'undefined') return [];

  try {
    const raw = window.localStorage.getItem(PINS_STORAGE_KEY) || window.localStorage.getItem(LEGACY_PINS_STORAGE_KEY) || '[]';
    const parsed = JSON.parse(raw);
    const seen = new Set();

    return (Array.isArray(parsed) ? parsed : [])
      .map(sanitizedTrack)
      .filter((track) => {
        if (!track?.id || seen.has(track.id)) return false;
        seen.add(track.id);
        return true;
      })
      .slice(0, MAX_PINNED_TRACKS);
  } catch {
    return [];
  }
}

export function writePinnedTracks(tracks = []) {
  if (typeof window === 'undefined') return;

  try {
    const serialized = JSON.stringify(tracks.map(sanitizedTrack).filter(Boolean).slice(0, MAX_PINNED_TRACKS));
    window.localStorage.setItem(PINS_STORAGE_KEY, serialized);
    window.localStorage.setItem(LEGACY_PINS_STORAGE_KEY, serialized);
  } catch {
    // Pins are a convenience; storage failures should not interrupt playback.
  }
}
