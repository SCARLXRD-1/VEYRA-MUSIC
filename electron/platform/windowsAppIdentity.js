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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
 */

export const VEYRA_APP_USER_MODEL_ID = 'dev.akashi.veyra';
export const ORCHARD_APP_USER_MODEL_ID = VEYRA_APP_USER_MODEL_ID;

function quotedWindowsArgument(value) {
  return `"${String(value).replaceAll('"', '\\"')}"`;
}

export function windowsRelaunchCommand({ appPath, executablePath }) {
  if (!appPath || !executablePath) return '';
  return `${quotedWindowsArgument(executablePath)} ${quotedWindowsArgument(appPath)}`;
}

export function setWindowsAppDetails(window, {
  appPath,
  appIconPath,
  executablePath = process.execPath,
  platform = process.platform,
  displayName = 'Veyra Music'
} = {}) {
  if (platform !== 'win32') return;

  const details = { appId: VEYRA_APP_USER_MODEL_ID };
  const relaunchCommand = windowsRelaunchCommand({ appPath, executablePath });
  if (relaunchCommand) {
    details.appIconPath = appIconPath;
    details.appIconIndex = 0;
    details.relaunchCommand = relaunchCommand;
    details.relaunchDisplayName = displayName;
  }
  window.setAppDetails(details);
}
