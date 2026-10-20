# Publish WCW Chronology 0.3.3

This bundle includes the signed 0.3.3 APK (versionCode 13, minimum Android API 25), complete source and update manifest. It adds the optional Wrestling Chronology launch notice, persistent dismissal, Settings download entry and Downloader code 2909774. See docs/WRESTLING_CHRONOLOGY.md for the new app.

No rebuild is required to publish the included APK. No private signing keys or account credentials are included.

## Publish from the authenticated PC

1. Extract this ZIP and copy the source tree into the existing WCW-FireTV-App checkout, preserving its .git folder.
2. Review, commit and push to main.
3. Create stable release **v0.3.3**, targeting that commit; use release/NOTES.md for its notes.
4. Attach **release/WCW-FireTV-App.apk**, **release/SHA256SUMS.txt** and **release/update.json**.
5. Publish as the latest release, with draft and pre-release off.

Keep the APK asset named exactly **WCW-FireTV-App.apk** so the existing Downloader link remains:

https://github.com/deklol/WCW-FireTV-App/releases/latest/download/WCW-FireTV-App.apk

Users of an earlier WCW version can use Settings > Update app after publication. The included APK retains the current package and signing certificate, preserving app data during an in-place update. Keep the signing key private and use it for future releases.

## New app link

The introduction downloads Wrestling Chronology from its separate live GitHub repository. The supplied Downloader code **2909774** resolves to:

https://github.com/deklol/Wrestling-Chronology/releases/latest/download/Wrestling-Chronology.apk

The new app need not be rebuilt or republished to release this WCW notice update. This bundle does not push or publish either repository automatically.
