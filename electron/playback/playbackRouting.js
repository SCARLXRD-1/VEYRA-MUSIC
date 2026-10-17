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
 */

import { isAgeGatePlaybackError } from './playbackErrors.js';

export function shouldPreferBrowserPlayback(track = {}, androidVrCooldown = false) {
  return Boolean(track.isUpload || androidVrCooldown);
}

export function shouldTryAuthenticatedAgeGate(error, { wantsVideo = false, signedIn = false } = {}) {
  return !wantsVideo && signedIn && isAgeGatePlaybackError(error);
}
