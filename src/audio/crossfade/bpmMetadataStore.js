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

import { get, set } from 'idb-keyval';

const STORAGE_KEY = 'veyra:bpm-metadata:v1';
const LEGACY_STORAGE_KEY = 'orchard:bpm-metadata:v1';

export function createBpmMetadataStorage() {
  return {
    async load() {
      try {
        let records = await get(STORAGE_KEY);
        if (!Array.isArray(records)) {
          records = await get(LEGACY_STORAGE_KEY);
        }
        return Array.isArray(records) ? records : [];
      } catch {
        return [];
      }
    },
    async save(records) {
      try {
        await Promise.all([
          set(STORAGE_KEY, records),
          set(LEGACY_STORAGE_KEY, records).catch(() => {})
        ]);
      } catch {
        // BPM metadata is an optimization; storage failures fall back to memory.
      }
    }
  };
}
