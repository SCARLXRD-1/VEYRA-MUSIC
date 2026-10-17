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

import assert from 'node:assert/strict';
import test from 'node:test';
import {
  activeParticipants,
  expireParticipantReservations,
  PARTICIPANT_RESERVATION_MS
} from '../src/roomParticipants.js';

function participant(id, joinedAt, overrides = {}) {
  return { id, joinedAt, connectedAt: 0, lastSeenAt: joinedAt, leftAt: 0, ...overrides };
}

test('expires participants that never complete their socket connection', () => {
  const now = 100_000;
  const room = {
    participants: {
      stale: participant('stale', now - PARTICIPANT_RESERVATION_MS - 1),
      pending: participant('pending', now - PARTICIPANT_RESERVATION_MS)
    }
  };

  assert.equal(expireParticipantReservations(room, new Map(), now), true);
  assert.equal(room.participants.stale.leftAt, now);
  assert.equal(room.participants.pending.leftAt, 0);
  assert.deepEqual(activeParticipants(room, new Map(), now).map(({ id }) => id), ['pending']);
});

test('keeps connected participants active beyond the reservation window', () => {
  const now = 100_000;
  const connected = participant('connected', now - PARTICIPANT_RESERVATION_MS - 1);
  const room = { participants: { connected } };

  assert.equal(expireParticipantReservations(room, new Map([['connected', {}]]), now), false);
  assert.deepEqual(activeParticipants(room, new Map([['connected', {}]]), now), [connected]);
});
