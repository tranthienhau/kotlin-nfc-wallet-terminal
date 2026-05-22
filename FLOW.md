# Regenerating the screenshots and demo GIF

The screenshots in `screenshots/` are real captures from the app running on an
emulator, not mockups. The app exposes debug intent extras so each screen can be
landed on deterministically.

## 1. Boot an emulator

```bash
emulator -list-avds
emulator -avd Medium_Phone_API_36.2 -no-snapshot -no-boot-anim &
adb wait-for-device
# wait until: adb shell getprop sys.boot_completed  -> 1
```

## 2. Build + install

```bash
./gradlew :app:installDebug
```

## 3. Capture the screenshots

```bash
./tools/capture_screens.sh
```

This launches the app once per shot with intent extras and writes PNGs:

```bash
adb shell am start -S -n com.secureflux.wallet/.MainActivity \
  --es screen charge --es variant insufficient
adb exec-out screencap -p > screenshots/04-insufficient.png
```

Supported `--es screen` values: `scan` (`variant=result`), `charge`
(`variant=insufficient`), `fallback` (`variant=success`), `history`,
`sync` (`variant=progress`). The hook lives in `MainActivity` (`Demo`) and
`WalletViewModel` (`demo*` functions).

## 4. Record the demo GIF

```bash
adb shell am start -S -n com.secureflux.wallet/.MainActivity
adb shell screenrecord --bit-rate 6000000 --size 540x1200 /sdcard/demo.mp4 &
# drive the UI with `adb shell input tap <x> <y>` across the screens
adb shell pkill -INT screenrecord
adb pull /sdcard/demo.mp4 .
ffmpeg -i demo.mp4 -vf "fps=12,scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" screenshots/demo.gif
```

## How the deterministic hook works

`MainActivity` reads `screen`/`variant` extras into a `Demo` record and, in a
`LaunchedEffect`, navigates and calls the matching `WalletViewModel.demo*`
function (e.g. `demoAmount("999.00")` to force the insufficient-balance state).
This keeps the production flow untouched while making captures repeatable.
