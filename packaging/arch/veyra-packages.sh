#!/bin/sh

config_root=${XDG_CONFIG_HOME:-"$HOME/.config"}
installed_veyra="$config_root/veyra/versions/5.0.0/veyra"
installed_orchard="$config_root/orchard/versions/5.0.0/orchard"

if [ -x "$installed_veyra" ]; then
  exec "$installed_veyra" "$@"
fi

if [ -x "$installed_orchard" ]; then
  exec "$installed_orchard" "$@"
fi

if [ -x /usr/lib/veyra-packages/veyra-packages ]; then
  exec /usr/lib/veyra-packages/veyra-packages "$@"
fi

exec /usr/lib/orchard-packages/orchard-packages "$@"
