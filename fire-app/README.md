# Buzz 321 Fire - v1

A minimal Android WebView shell for the existing Buzz 321 tracker.

## Design

- Does **not** copy or fork the Buzz 321 application logic.
- Loads the live tracker at `https://aqcroft.github.io/lrbuzz-tracker/`.
- Gives the Fire tablet a real launcher icon and app window with Silk controls removed.
- Uses Android WebView storage, including persistent DOM localStorage.
- Keeps the screen awake and uses immersive full-screen mode during events.
- Requires no Google Play Services and no Chrome installation.
- Minimum Android API 23; intended for Fire OS 7 / Android 9-era devices including the Fire HD 8 (2018, 8th Gen).

## Why the existing URL is used

A second GitHub Pages URL does not make Amazon Silk install a PWA differently. Pointing the wrapper at the existing URL means all web-app updates are picked up from the current tracker without rebuilding the APK.

## Storage behaviour

The tracker stores its event state in `localStorage` under the key `lrbuzz`.

- Silk and this Android app do **not** share browser storage. Existing Silk-only temporary/local history will therefore not automatically appear in the app.
- Once the Android app is installed, its WebView localStorage persists across normal closes/reopens and APK upgrades.
- Clearing the app's storage or uninstalling the app removes that WebView localStorage.
- Google Sheets submissions are remote and are unaffected by the change of browser/storage context.

## Build

A GitHub Actions workflow in this repository builds a debug APK automatically when Fire-app files change.

The Android project can also be opened in Android Studio and built with **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.

## Updating later

Normal Buzz 321 web changes require no APK rebuild. The wrapper loads the existing live tracker URL.

For native-shell changes, increase `versionCode` and preferably `versionName` in `app/build.gradle`, then build a replacement APK using the same application ID: `com.aqcroft.buzz321fire`.

## Launcher icon

This source currently contains a simple Buzz-colour fallback launcher icon. The exact existing PNG can be substituted later without changing the tracker itself.
