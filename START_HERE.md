# Publish the Archive.org recovery update from the authenticated PC

This is the complete WCW Chronology 0.2.4 source bundle and signed APK (versionCode 6, minimum Android API 25). It includes six-second Archive.org stall recovery, episode progress bars, watched/unwatched tracking, source-aware resume and the Fire OS 6 compatibility fix. Clash events now appear under PPV instead of a separate filter. The APK is ready to upload; no rebuild is needed. No private signing key or account credentials are included.

Repository: https://github.com/deklol/WCW-FireTV-App

## Keep the existing Downloader URL

The asset must be named exactly **WCW-FireTV-App.apk**. Publish **v0.2.4** as the latest stable release. This keeps this exact URL working:

https://github.com/deklol/WCW-FireTV-App/releases/latest/download/WCW-FireTV-App.apk

1. Extract this ZIP. Read `docs/VALIDATION.md` for the completed checks and the remaining Fire OS 6 device test.
2. Copy this source tree into the existing repository checkout, preserving its `.git` folder. Review and commit the changes, then push to `main`. Do not copy private signing keys into the repository.
3. Create a new GitHub release tagged **v0.2.4**, targeting that pushed commit. Use `release/NOTES.md` as the release notes.
4. Attach `release/WCW-FireTV-App.apk`, `release/SHA256SUMS.txt` and `release/update.json` to the release. GitHub will generate source ZIP/TAR archives from the tagged commit.
5. Publish it as the **latest release**, with draft and pre-release both off. Earlier releases can stay available.
6. Download through the unchanged URL above and compare its SHA-256 with `release/SHA256SUMS.txt`.
7. On the other Fire Stick, delete the previous download in Downloader and download again through the same URL. Test installation, launch and playback.

The prebuilt APK has the same signer and package name as the existing app, so installing it as an update preserves viewing progress. The private signing key remains on the original development PC; rebuilding on another PC with a different key will produce an APK that cannot update the existing installation.

## Optional GitHub CLI release command

After committing and pushing the source, run this from the repository root on the authenticated PC:

```powershell
gh release create v0.2.4 release/WCW-FireTV-App.apk release/SHA256SUMS.txt release/update.json --repo deklol/WCW-FireTV-App --target main --title "WCW Chronology 0.2.4 - Archive playback recovery" --notes-file release/NOTES.md --latest
```

If a v0.2.4 tag or release already exists, inspect it before replacing any asset. This bundle does not publish anything automatically.
