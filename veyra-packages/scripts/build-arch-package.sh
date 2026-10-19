#!/bin/sh

set -eu

manager_dir=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
repository_dir=$(CDPATH= cd -- "$manager_dir/.." && pwd)
packaging_dir="$repository_dir/packaging/arch"
output_dir="$repository_dir/artifacts/arch"
version=$(bun -e "const value = await Bun.file('$manager_dir/package.json').json(); process.stdout.write(value.version)")
source_name="veyra-packages-$version"
source_archive="$packaging_dir/$source_name.tar.zst"
temporary_dir=$(mktemp -d "${TMPDIR:-/tmp}/veyra-packages-arch.XXXXXX")
cleanup() {
  rm -rf -- "$temporary_dir"
}
trap cleanup EXIT HUP INT TERM

cd "$manager_dir"
bun install --frozen-lockfile --ignore-scripts
bunx neu update

mkdir -p "$temporary_dir/$source_name/assets"
cp -a \
  package.json bun.lock go.mod go.sum tsconfig.json neutralino.config.json src scripts backend node_modules bin \
  "$temporary_dir/$source_name/"
cp "$repository_dir/build/icon.png" "$temporary_dir/$source_name/assets/veyra-packages.png"
cp "$repository_dir/build/icon.png" "$temporary_dir/$source_name/assets/orchard-packages.png"
cp "$repository_dir/LICENSE" "$temporary_dir/$source_name/LICENSE"

rm -f -- "$source_archive"
ZSTD_CLEVEL=6 tar \
  --sort=name \
  --mtime=@0 \
  --owner=0 \
  --group=0 \
  --numeric-owner \
  --zstd \
  -cf "$source_archive" \
  -C "$temporary_dir" \
  "$source_name"

if [ -f "$packaging_dir/veyra-packages.desktop" ]; then
  desktop_file="$packaging_dir/veyra-packages.desktop"
else
  desktop_file="$packaging_dir/orchard-packages.desktop"
fi
if [ -f "$packaging_dir/veyra-packages.sh" ]; then
  launcher_file="$packaging_dir/veyra-packages.sh"
else
  launcher_file="$packaging_dir/orchard-packages.sh"
fi

source_sha256=$(sha256sum "$source_archive" | awk '{print $1}')
desktop_sha256=$(sha256sum "$desktop_file" | awk '{print $1}')
launcher_sha256=$(sha256sum "$launcher_file" | awk '{print $1}')
sed -i \
  -e "s/^pkgver=.*/pkgver=$version/" \
  -e "s/^sha256sums=.*/sha256sums=('$source_sha256' '$desktop_sha256' '$launcher_sha256')/" \
  "$packaging_dir/PKGBUILD"

cd "$packaging_dir"
makepkg --force --cleanbuild --clean

mkdir -p "$output_dir"
find "$output_dir" -maxdepth 1 -type f -name '*.pkg.tar.zst' -delete
package_file=$(makepkg --packagelist | sed -n '1p')
test -n "$package_file"
test -f "$package_file"
cp "$package_file" "$output_dir/"

printf '%s\n' "$output_dir/$(basename -- "$package_file")"
