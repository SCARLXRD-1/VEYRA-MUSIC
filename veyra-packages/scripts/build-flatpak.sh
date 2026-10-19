#!/bin/sh

set -eu

target=${1:-}
case "$target" in
  linux-x64) flatpak_arch=x86_64 ;;
  linux-arm64) flatpak_arch=aarch64 ;;
  *) echo "Usage: $0 linux-x64|linux-arm64" >&2; exit 1 ;;
esac

manager_dir=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
repository_dir=$(CDPATH= cd -- "$manager_dir/.." && pwd)
flatpak_dir="$repository_dir/packaging/flatpak"
version=$(bun -e "const value = await Bun.file('$manager_dir/package.json').json(); process.stdout.write(value.version)")
release_dir="$manager_dir/dist/releases/orchard-packages-$version-$target"
output_dir="$repository_dir/artifacts/flatpak"
output="$output_dir/orchard-packages-$version-$target.flatpak"

if [ ! -d "$release_dir" ]; then
  bun run "$manager_dir/scripts/build-release.ts" "$target"
fi

rm -rf -- "$flatpak_dir/bundle" "$flatpak_dir/build" "$flatpak_dir/repo"
mkdir -p "$flatpak_dir/bundle/extensions" "$output_dir"
if [ -f "$release_dir/veyra-packages" ]; then
  cp "$release_dir/veyra-packages" "$flatpak_dir/bundle/veyra-packages"
fi
if [ -f "$release_dir/orchard-packages" ]; then
  cp "$release_dir/orchard-packages" "$flatpak_dir/bundle/orchard-packages"
fi
cp "$release_dir/resources.neu" "$flatpak_dir/bundle/resources.neu"
if [ -f "$release_dir/extensions/veyra-packages-backend" ]; then
  cp "$release_dir/extensions/veyra-packages-backend" "$flatpak_dir/bundle/extensions/veyra-packages-backend"
fi
if [ -f "$release_dir/extensions/orchard-packages-backend" ]; then
  cp "$release_dir/extensions/orchard-packages-backend" "$flatpak_dir/bundle/extensions/orchard-packages-backend"
fi

if [ -f "$flatpak_dir/veyra" ]; then
  cp "$flatpak_dir/veyra" "$flatpak_dir/bundle/veyra"
elif [ -f "$flatpak_dir/orchard" ]; then
  cp "$flatpak_dir/orchard" "$flatpak_dir/bundle/veyra"
fi

if [ -f "$flatpak_dir/veyra-packages-launcher" ]; then
  cp "$flatpak_dir/veyra-packages-launcher" "$flatpak_dir/bundle/veyra-packages-launcher"
elif [ -f "$flatpak_dir/orchard-packages-launcher" ]; then
  cp "$flatpak_dir/orchard-packages-launcher" "$flatpak_dir/bundle/veyra-packages-launcher"
fi

if [ -f "$repository_dir/packaging/arch/veyra-packages.desktop" ]; then
  cp "$repository_dir/packaging/arch/veyra-packages.desktop" "$flatpak_dir/bundle/dev.akashi.veyra.desktop"
elif [ -f "$repository_dir/packaging/arch/orchard-packages.desktop" ]; then
  cp "$repository_dir/packaging/arch/orchard-packages.desktop" "$flatpak_dir/bundle/dev.akashi.veyra.desktop"
fi

if [ -f "$flatpak_dir/dev.sfg.veyra.png" ]; then
  cp "$flatpak_dir/dev.sfg.veyra.png" "$flatpak_dir/bundle/dev.akashi.veyra.png"
elif [ -f "$flatpak_dir/dev.sfg.orchard.png" ]; then
  cp "$flatpak_dir/dev.sfg.orchard.png" "$flatpak_dir/bundle/dev.akashi.veyra.png"
fi

if [ -f "$flatpak_dir/dev.sfg.veyra.metainfo.xml" ]; then
  cp "$flatpak_dir/dev.sfg.veyra.metainfo.xml" "$flatpak_dir/bundle/dev.sfg.veyra.metainfo.xml"
elif [ -f "$flatpak_dir/dev.sfg.orchard.metainfo.xml" ]; then
  cp "$flatpak_dir/dev.sfg.orchard.metainfo.xml" "$flatpak_dir/bundle/dev.sfg.veyra.metainfo.xml"
fi

flatpak_manifest="$flatpak_dir/dev.sfg.veyra.yml"
if [ ! -f "$flatpak_manifest" ]; then
  flatpak_manifest="$flatpak_dir/dev.sfg.orchard.yml"
fi

flatpak-builder --disable-rofiles-fuse --arch="$flatpak_arch" --force-clean --repo="$flatpak_dir/repo" \
  "$flatpak_dir/build" "$flatpak_manifest"
flatpak build-bundle --arch="$flatpak_arch" \
  "$flatpak_dir/repo" "$output" dev.akashi.veyra

printf '%s\n' "$output"
