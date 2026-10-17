import { copyFile, mkdir, stat } from 'node:fs/promises';
import path from 'node:path';

const target = process.argv[2];
const outputRoot = path.resolve(process.argv[3] || 'native-inputs');
if (!/^(linux|win32|darwin)-(x64|arm64)$/.test(target || '')) {
  throw new Error('Usage: node scripts/stage-package-native.mjs <target> [output-directory]');
}

async function fileExists(filePath) {
  try {
    await stat(filePath);
    return true;
  } catch {
    return false;
  }
}

const files = [
  {
    candidates: [
      `native-media/build/veyra-system-media-${target}.node`,
      `native-media/build/orchard-system-media-${target}.node`
    ],
    destinations: [
      `native-media/build/veyra-system-media-${target}.node`,
      `native-media/build/orchard-system-media-${target}.node`
    ]
  },
  {
    candidates: [
      `native-audio-rust/build/veyra-audio-${target}.node`,
      `native-audio-rust/build/orchard-audio-${target}.node`
    ],
    destinations: [
      `native-audio-rust/build/veyra-audio-${target}.node`,
      `native-audio-rust/build/orchard-audio-${target}.node`
    ]
  }
];

for (const file of files) {
  let source = null;
  for (const candidate of file.candidates) {
    if (await fileExists(path.resolve(candidate))) {
      source = path.resolve(candidate);
      break;
    }
  }
  if (!source) {
    throw new Error(`Missing native asset for target ${target}: ${file.candidates.join(' or ')}`);
  }
  for (const relDest of file.destinations) {
    const destination = path.join(outputRoot, relDest);
    await mkdir(path.dirname(destination), { recursive: true });
    await copyFile(source, destination);
    console.log(`${target}: ${destination}`);
  }
}
