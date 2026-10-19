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

export async function probeInternetReachability({
  fetchImpl = globalThis.fetch,
  endpoint = 'https://www.youtube.com/generate_204',
  timeoutMs = 4000
} = {}) {
  if (typeof fetchImpl !== 'function' || typeof AbortController !== 'function') return false;
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), timeoutMs);
  try {
    await fetchImpl(endpoint, {
      method: 'GET',
      mode: 'no-cors',
      cache: 'no-store',
      signal: controller.signal
    });
    return true;
  } catch {
    return false;
  } finally {
    clearTimeout(timeout);
  }
}
