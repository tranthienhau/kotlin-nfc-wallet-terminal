#!/usr/bin/env bash
# Capture real screenshots from the running app on a booted emulator/device.
# The app reads `--es screen <name> [--es variant <v>]` intent extras and lands
# on a fixed state, so every shot is deterministic.
set -euo pipefail

PKG=com.secureflux.wallet
OUT="$(dirname "$0")/../screenshots"
mkdir -p "$OUT"

cap() { # <file> [screen] [variant]
  adb shell am start -S -n "$PKG/.MainActivity" \
    ${2:+--es screen "$2"} ${3:+--es variant "$3"} >/dev/null 2>&1
  sleep 3
  adb exec-out screencap -p > "$OUT/$1.png"
  echo "captured $1"
}

cap 01-scan
cap 02-scan-verified scan result
cap 03-charge          charge entry
cap 04-insufficient    charge insufficient
cap 05-stripe-fallback fallback ready
cap 06-history         history
cap 07-sync            sync queue
cap 08-sync-progress   sync progress
