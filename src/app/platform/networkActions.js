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

// The proxy mode is owned by the main process, which applies it to the session
// before any window loads. The renderer only reads it back and asks for changes.
import { computed, ref } from 'vue';

export function installNetworkActions(ctx) {
  ctx.proxyMode = ref('system');
  ctx.proxyModeMessage = ref('');
  const networkBridge = () => window.veyraNetwork || window.orchardNetwork;
  ctx.proxyModeAvailable = Boolean(networkBridge()?.setProxyMode);

  ctx.loadProxyMode = async function loadProxyMode() {
    const bridge = networkBridge();
    if (!bridge?.getProxyMode) return;
    const mode = await bridge.getProxyMode();
    if (mode) ctx.proxyMode.value = mode;
  };

  ctx.setProxyMode = async function setProxyMode(mode) {
    const bridge = networkBridge();
    if (!bridge?.setProxyMode) return;
    try {
      ctx.proxyMode.value = await bridge.setProxyMode(mode);
      // Images already refused by the dead proxy stay broken until they are
      // requested again, so say so rather than leaving the listener staring at
      // the same empty artwork wondering whether the setting took.
      ctx.proxyModeMessage.value = ctx.proxyMode.value === 'direct'
        ? 'Veyra Music now ignores the system proxy. Restart Veyra Music if album art is still missing.'
        : 'Veyra Music now follows the system proxy.';
    } catch (error) {
      ctx.proxyModeMessage.value = `Could not change the proxy setting. ${error?.message || error}`;
    }
  };

  ctx.proxyBypassToggle = computed({
    get: () => ctx.proxyMode.value === 'direct',
    set: (value) => { void ctx.setProxyMode(value ? 'direct' : 'system'); }
  });
}
