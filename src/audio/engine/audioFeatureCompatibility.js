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

export function crossfadeModeForAudioEngine(mode, config = {}) {
  const normalizedMode = mode === 'smart' ? 'smart' : 'standard';
  return config.autoEqEnabled || config.eqEnabled ? 'standard' : normalizedMode;
}

export function audioEngineConfigForCrossfadeMode(config = {}, mode = 'standard') {
  if (mode !== 'smart') return { ...config };
  return { ...config, autoEqEnabled: false, eqEnabled: false };
}
