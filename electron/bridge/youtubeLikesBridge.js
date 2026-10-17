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

// Adapts renderer like-state requests to the authenticated main-process service.
export function registerYouTubeLikesBridge({ socket, youtubeLikes, bridgeError }) {
  socket.on('music:like:status', async (payload, reply) => {
    try {
      reply({ ok: true, data: await youtubeLikes.status(payload) });
    } catch (error) {
      reply({ ok: false, error: bridgeError(error) });
    }
  });

  socket.on('music:like:set', async (payload, reply) => {
    try {
      reply({ ok: true, data: await youtubeLikes.set(payload) });
    } catch (error) {
      reply({ ok: false, error: bridgeError(error) });
    }
  });
}
