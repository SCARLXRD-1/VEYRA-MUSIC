#!/bin/sh
# Copyright (C) 2026 SFG545
#
# This file is part of Veyra Music.
#
# Veyra Music es un fork de Orchard (https://github.com/SFG5453/Orchard),
# mantenido únicamente para consumo personal y sin afiliación con el
# proyecto original.
#
# Veyra Music is free software: you can redistribute it and/or modify it under the
# terms of the GNU Affero General Public License as published by the Free
# Software Foundation, either version 3 of the License, or (at your option) any
# later version.
#
# Veyra Music is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
# PARTICULAR PURPOSE. See the GNU Affero General Public License for more
# details.
#
# You should have received a copy of the GNU Affero General Public License
# along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.

# Builds the system-media addon for the host platform.

set -eu

project_dir=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
crate_dir="$project_dir/native-media"
output_dir="$crate_dir/build"

command -v cargo >/dev/null || {
  echo 'cargo not found; skipping media addon build (Rust toolchain required for native addons).' >&2
  exit 0
}

case "$(uname -s)" in
  Linux) platform=linux; extension=so; prefix=lib ;;
  Darwin) platform=darwin; extension=dylib; prefix=lib ;;
  MINGW*|MSYS*|CYGWIN*|Windows_NT)
    platform=win32
    extension=dll
    prefix=
    ;;
  *) echo "unsupported host platform: $(uname -s)" >&2; exit 1 ;;
esac

case "$(node -p process.arch)" in
  x64|x86_64|amd64) architecture=x64 ;;
  arm64|aarch64) architecture=arm64 ;;
  *) echo "unsupported host architecture: $(node -p process.arch)" >&2; exit 1 ;;
esac

if [ "$platform" = win32 ]; then
  case "$architecture" in
    x64) target=x86_64-pc-windows-msvc ;;
    arm64) target=aarch64-pc-windows-msvc ;;
  esac
  rustup target add "$target" >/dev/null
  cargo build --release --target "$target" --manifest-path "$crate_dir/Cargo.toml"
  binary="$crate_dir/target/$target/release/orchard_system_media.dll"
else
  cargo build --release --manifest-path "$crate_dir/Cargo.toml"
  binary="$crate_dir/target/release/${prefix}orchard_system_media.${extension}"
fi

mkdir -p "$output_dir"
cp "$binary" \
  "$output_dir/veyra-system-media-${platform}-${architecture}.node"
cp "$binary" \
  "$output_dir/orchard-system-media-${platform}-${architecture}.node"

# The addon is useless if it cannot register with Node, and a silently
# entry-point-less binary would only surface as a confusing runtime failure.
(cd "$project_dir" && node -e "
const addon = require('./native-media/build/orchard-system-media-${platform}-${architecture}.node');
if (typeof addon.SystemMediaControls !== 'function') {
  throw new Error('addon did not export SystemMediaControls');
}
console.log('media addon OK: orchard-system-media-${platform}-${architecture}.node');
")
