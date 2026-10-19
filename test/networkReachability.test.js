/*
 * Copyright (C) 2026 SFG545
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 */

import assert from 'node:assert/strict';
import test from 'node:test';
import { probeInternetReachability } from '../src/app/core/networkReachability.js';

test('reachability probe treats a completed no-cors request as online', async () => {
  let request;
  const reachable = await probeInternetReachability({
    fetchImpl: async (...args) => { request = args; return { type: 'opaque' }; }
  });

  assert.equal(reachable, true);
  assert.equal(request[0], 'https://www.youtube.com/generate_204');
  assert.equal(request[1].mode, 'no-cors');
  assert.equal(request[1].cache, 'no-store');
});

test('reachability probe treats a failed request as offline', async () => {
  assert.equal(await probeInternetReachability({ fetchImpl: async () => { throw new Error('offline'); } }), false);
});
