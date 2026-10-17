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

export function normalizeBackgroundUrl(value) {
  return typeof value === 'string' ? value.trim() : '';
}

export function isHlsSource(value) {
  const source = normalizeBackgroundUrl(value);
  if (!source) return false;

  try {
    return new URL(source, 'https://veyra.invalid').pathname.toLowerCase().endsWith('.m3u8');
  } catch {
    return /\.m3u8(?:$|[?#])/i.test(source);
  }
}

export function interpolateRgb(from, to, progress) {
  const amount = Math.max(0, Math.min(1, Number(progress) || 0));
  return [0, 1, 2].map((index) => Math.round(from[index] + ((to[index] - from[index]) * amount)));
}
