#!/usr/bin/env node
/*
 * Copyright (C) 2026 SFG545 / Veyra Music (AKASHI DEV)
 * AGPL-3.0-or-later
 */

import { cp, mkdir, rm, chmod, readFile, writeFile } from 'node:fs/promises';
import { createHash } from 'node:crypto';
import { execFile } from 'node:child_process';
import { promisify } from 'node:util';
import path from 'node:path';
import { existsSync } from 'node:fs';

const exec = promisify(execFile);
const rootDir = process.cwd();
const version = '1.0.0';
const releasesDir = path.join(rootDir, 'releases', 'desktop', 'binaries');
const stagingAppDir = '/tmp/veyra-desktop-payload';

async function sha256(filePath) {
  const content = await readFile(filePath);
  return createHash('sha256').update(content).digest('hex');
}

async function run(cmd, args, options = {}) {
  console.log(`> ${cmd} ${args.join(' ')}`);
  return exec(cmd, args, { cwd: rootDir, ...options });
}

async function main() {
  console.log(`\n==================================================`);
  console.log(` 🚀 Veyra Music — Desktop Packager v${version}`);
  console.log(` Sello: AKASHI DEV`);
  console.log(`==================================================\n`);

  await mkdir(releasesDir, { recursive: true });

  // 1. Build frontend and native modules
  console.log(`[1/5] Compilando frontend y módulos nativos...`);
  await run('npm', ['run', 'build']);

  // 2. Prepare payload
  console.log(`[2/5] Preparando payload de la aplicación en ${stagingAppDir}...`);
  await rm(stagingAppDir, { recursive: true, force: true });
  await mkdir(stagingAppDir, { recursive: true });

  // Custom package.json for production
  const rootPkg = JSON.parse(await readFile(path.join(rootDir, 'package.json'), 'utf8'));
  const prodPkg = {
    name: 'veyra',
    productName: 'Veyra Music',
    version: version,
    description: 'Veyra Music - Modern Industrial & Neumorphic Audio Player by AKASHI DEV',
    main: 'electron/main/index.js',
    type: 'module',
    author: 'AKASHI DEV',
    license: 'AGPL-3.0-or-later',
    dependencies: rootPkg.dependencies
  };
  await writeFile(path.join(stagingAppDir, 'package.json'), JSON.stringify(prodPkg, null, 2), 'utf8');

  // Copy app directories
  await cp(path.join(rootDir, 'dist'), path.join(stagingAppDir, 'dist'), { recursive: true });
  await cp(path.join(rootDir, 'electron'), path.join(stagingAppDir, 'electron'), { recursive: true });
  await cp(path.join(rootDir, 'shared'), path.join(stagingAppDir, 'shared'), { recursive: true });
  await cp(path.join(rootDir, 'packages'), path.join(stagingAppDir, 'packages'), { recursive: true });
  await cp(path.join(rootDir, 'LICENSE'), path.join(stagingAppDir, 'LICENSE'));

  // Copy native addons
  await mkdir(path.join(stagingAppDir, 'native-media', 'build'), { recursive: true });
  await cp(path.join(rootDir, 'native-media', 'index.cjs'), path.join(stagingAppDir, 'native-media', 'index.cjs'));
  if (existsSync(path.join(rootDir, 'native-media', 'build'))) {
    await cp(path.join(rootDir, 'native-media', 'build'), path.join(stagingAppDir, 'native-media', 'build'), { recursive: true });
  }

  await mkdir(path.join(stagingAppDir, 'native-audio-rust', 'build'), { recursive: true });
  await cp(path.join(rootDir, 'native-audio-rust', 'index.cjs'), path.join(stagingAppDir, 'native-audio-rust', 'index.cjs'));
  if (existsSync(path.join(rootDir, 'native-audio-rust', 'build'))) {
    await cp(path.join(rootDir, 'native-audio-rust', 'build'), path.join(stagingAppDir, 'native-audio-rust', 'build'), { recursive: true });
  }

  // Copy production node_modules via rsync (excluding electron and cache)
  console.log(`[2.1] Sincronizando dependencias de producción...`);
  await run('rsync', [
    '-a',
    '--exclude=electron',
    '--exclude=@rolldown',
    '--exclude=.cache',
    '--exclude=esbuild',
    'node_modules/',
    path.join(stagingAppDir, 'node_modules/')
  ]);

  // 3. Package Linux
  console.log(`\n[3/5] Empaquetando Veyra Music para Linux x64...`);
  const linuxDir = path.join(releasesDir, 'veyra-linux-x64');
  await rm(linuxDir, { recursive: true, force: true });
  await mkdir(linuxDir, { recursive: true });

  // Copy electron runtime
  await cp(path.join(rootDir, 'node_modules', 'electron', 'dist'), linuxDir, { recursive: true });
  await rm(path.join(linuxDir, 'resources', 'default_app.asar'), { force: true });
  await renameSafe(path.join(linuxDir, 'electron'), path.join(linuxDir, 'veyra'));
  await chmod(path.join(linuxDir, 'veyra'), 0o755);

  // Copy payload to resources/app
  await cp(stagingAppDir, path.join(linuxDir, 'resources', 'app'), { recursive: true });

  // Add launcher & desktop entry
  const launcherSh = `#!/usr/bin/env sh\nHERE="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"\nexec "$HERE/veyra" "$@"\n`;
  await writeFile(path.join(linuxDir, 'veyra.sh'), launcherSh, 'utf8');
  await chmod(path.join(linuxDir, 'veyra.sh'), 0o755);

  const desktopEntry = `[Desktop Entry]\nType=Application\nName=Veyra Music\nComment=Modern Industrial & Neumorphic Audio Player by AKASHI DEV\nExec=veyra.sh %U\nIcon=icon\nTerminal=false\nCategories=Audio;AudioVideo;Player;\nStartupWMClass=veyra\n`;
  await writeFile(path.join(linuxDir, 'dev.sfg.veyra.desktop'), desktopEntry, 'utf8');
  await cp(path.join(rootDir, 'build', 'icon.png'), path.join(linuxDir, 'icon.png'));

  // Compress Linux tar.gz
  const linuxTarName = `veyra-v${version}-linux-x64.tar.gz`;
  const linuxTarPath = path.join(releasesDir, linuxTarName);
  console.log(`[3.1] Comprimiendo ${linuxTarName}...`);
  await rm(linuxTarPath, { force: true });
  await run('tar', ['-czf', linuxTarPath, '-C', releasesDir, 'veyra-linux-x64']);

  // 4. Package Windows
  console.log(`\n[4/5] Empaquetando Veyra Music para Windows x64...`);
  const winDir = path.join(releasesDir, 'veyra-win32-x64');
  await rm(winDir, { recursive: true, force: true });
  await mkdir(winDir, { recursive: true });

  const winZipCache = path.join(process.env.HOME, '.cache', 'electron', 'electron-v43.7.0-win32-x64.zip');
  if (!existsSync(winZipCache)) {
    throw new Error(`Falta el runtime de Electron Windows en ${winZipCache}`);
  }

  // Extract windows zip
  console.log(`[4.1] Extrayendo runtime de Windows...`);
  await run('unzip', ['-q', winZipCache, '-d', winDir]);
  await rm(path.join(winDir, 'resources', 'default_app.asar'), { force: true });
  await renameSafe(path.join(winDir, 'electron.exe'), path.join(winDir, 'veyra.exe'));

  // Copy payload to resources/app
  await cp(stagingAppDir, path.join(winDir, 'resources', 'app'), { recursive: true });

  // Add Windows launcher & icon
  const launcherCmd = `@echo off\nstart "" "%~dp0veyra.exe" %*\n`;
  await writeFile(path.join(winDir, 'veyra.cmd'), launcherCmd, 'utf8');
  await cp(path.join(rootDir, 'build', 'icon.ico'), path.join(winDir, 'icon.ico'));

  // Compress Windows zip via 7z
  const winZipName = `veyra-v${version}-windows-x64.zip`;
  const winZipPath = path.join(releasesDir, winZipName);
  console.log(`[4.2] Comprimiendo ${winZipName}...`);
  await rm(winZipPath, { force: true });
  await run('7z', ['a', '-tzip', winZipPath, path.join(releasesDir, 'veyra-win32-x64')]);

  // Clean staging
  await rm(stagingAppDir, { recursive: true, force: true });

  // 5. Compute Checksums
  console.log(`\n[5/5] Generando sumas de verificación SHA-256...`);
  const linuxHash = await sha256(linuxTarPath);
  const winHash = await sha256(winZipPath);

  const checksumsContent = [
    `# Veyra Music v${version} Desktop Release Checksums (SHA-256)`,
    `# Sello: AKASHI DEV`,
    ``,
    `${linuxHash}  ${linuxTarName}`,
    `${winHash}  ${winZipName}`,
    ``
  ].join('\n');

  await writeFile(path.join(releasesDir, 'SHA256SUMS.txt'), checksumsContent, 'utf8');

  console.log(`\n==================================================`);
  console.log(`✅ ¡Compilación de Releases Desktop Finalizada con Éxito!`);
  console.log(`📦 Linux:   ${linuxTarPath}`);
  console.log(`   SHA-256: ${linuxHash}`);
  console.log(`📦 Windows: ${winZipPath}`);
  console.log(`   SHA-256: ${winHash}`);
  console.log(`==================================================\n`);
}

async function renameSafe(from, to) {
  try {
    await cp(from, to);
    await rm(from, { force: true });
  } catch {
    // Already renamed
  }
}

main().catch((err) => {
  console.error('\n❌ Error en el empaquetado de Desktop:', err);
  process.exit(1);
});
