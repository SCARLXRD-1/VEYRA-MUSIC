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

import { z } from 'zod';

// El JIT opcional de Zod sondea Function() antes de compilar los validadores de objetos.
// El renderer de Veyra Music lo prohíbe deliberadamente bajo su CSP estricta.
z.config({ jitless: true });

const finiteNumber = z.number().finite();

export const audioEngineConfigSchema = z.object({
  enabled: z.boolean().optional(),
  autoEqEnabled: z.boolean().optional(),
  eqEnabled: z.boolean().optional(),
  gains: z.array(finiteNumber).length(10).optional(),
  preampDb: finiteNumber.optional(),
  outputGainDb: finiteNumber.optional(),
  q: finiteNumber.optional(),
  balance: finiteNumber.optional(),
  outputDeviceId: z.string().optional()
}).passthrough();

export const audioEngineProfileSchema = z.object({
  app: z.union([z.literal('veyra'), z.literal('orchard')]),
  type: z.literal('audio-engine-profile'),
  version: z.number().int().min(1),
  config: audioEngineConfigSchema
});

export const learnedAudioProfileSchema = z.object({
  trackId: z.string().min(1),
  title: z.string().default(''),
  features: z.array(finiteNumber).length(9),
  gains: z.array(finiteNumber).length(10),
  tempo: finiteNumber.nullable().default(null),
  sampleCount: z.number().int().positive(),
  updatedAt: finiteNumber
});

export const learnedAudioProfilesSchema = z.array(learnedAudioProfileSchema);

export function parseAudioEngineProfile(value) {
  const result = audioEngineProfileSchema.safeParse(value);
  if (!result.success) throw new Error('That file is not a valid Veyra audio profile.');
  return result.data;
}
