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

import { readFile } from 'node:fs/promises';

export function generateAndroidUpdateMetadata({
  version,
  codename = '',
  versionCode,
  apkUrl,
  sha256,
  publishedAt,
  releaseNotes = '',
}) {
  if (!version || !apkUrl) {
    throw new Error('Version and apkUrl are required');
  }

  const payload = {
    version,
    ...(codename ? { codename } : {}),
    versionCode: typeof versionCode === 'number' ? versionCode : parseInt(versionCode, 10) || 0,
    apkUrl,
    sha256,
    publishedAt,
    releaseNotes,
  };

  return JSON.stringify(payload, null, 2) + '\n';
}

async function main() {
  const [, , version, versionCodeStr, apkUrl, sha256, publishedAt, releaseNotesPath, codenameArg] = process.argv;
  let releaseNotes = '';
  let codename = codenameArg || '';
  if (releaseNotesPath) {
    try {
      releaseNotes = await readFile(releaseNotesPath, 'utf8');
      if (!codename) {
        const match = releaseNotes.match(/^##\s+.*?["'“]([^"'”]+)["'”]/m);
        if (match) {
          codename = match[1];
        }
      }
    } catch {
      // Fallback if file not found
    }
  }

  const result = generateAndroidUpdateMetadata({
    version,
    codename,
    versionCode: parseInt(versionCodeStr, 10) || 0,
    apkUrl,
    sha256,
    publishedAt,
    releaseNotes,
  });

  process.stdout.write(result);
}

if (process.argv[1] && process.argv[1].endsWith('generate-android-update-metadata.mjs')) {
  main().catch((err) => {
    console.error(err);
    process.exit(1);
  });
}
