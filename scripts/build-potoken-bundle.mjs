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

// Bundles mobile/tools/potoken/index.js into the single self-contained IIFE the Android
// challenge WebView loads. Committed output, because the Gradle build has no Node step —
// rerun this whenever the entry or bgutils-js changes, and commit the result.

import { build } from 'vite';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const root = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const entry = path.join(root, 'mobile/tools/potoken/index.js');
const outDir = path.join(root, 'mobile/android/app/src/main/assets');

await build({
  root,
  configFile: false,
  logLevel: 'info',
  build: {
    outDir,
    emptyOutDir: false,
    target: 'es2020',
    minify: true,
    lib: {
      entry,
      formats: ['iife'],
      // The bundle is evaluated for its side effects: it hangs orchardMintPoToken off window.
      // A named global is still required by lib mode, so give it one rather than let it pick.
      name: 'OrchardPoTokenBundle',
      fileName: () => 'yt_potoken.bundle.js'
    }
  }
});

console.log(`Wrote ${path.join(outDir, 'yt_potoken.bundle.js')}`);
