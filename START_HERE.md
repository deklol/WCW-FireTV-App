# Publish WCW Chronology 0.3.0

This complete source bundle includes the signed 0.3.0 APK (versionCode 10, minimum Android API 25). It adds the pre-1997 library and a dedicated Years page, retains Home/Nitro/Thunder/PPV/Search/Settings, and preserves the player, progress tracking and updater. See docs/PRE1997_LIBRARY.md for coverage and verification limits and docs/VALIDATION.md for build/device checks.

No rebuild is needed to publish the supplied APK. No private signing key or account credentials are included.

## Keep the existing Downloader URL

Publish **v0.3.0** as the latest stable release, with the APK asset named exactly **WCW-FireTV-App.apk**. This preserves:

https://github.com/deklol/WCW-FireTV-App/releases/latest/download/WCW-FireTV-App.apk

1. Extract this ZIP and copy its source tree into the existing repository checkout, preserving the checkout's .git folder.
2. Review, commit and push the changes to main on the authenticated PC.
3. Create release **v0.3.0**, targeting that commit. Use release/NOTES.md as its notes.
4. Attach release/WCW-FireTV-App.apk, release/SHA256SUMS.txt and release/update.json.
5. Publish as the latest release, with draft and pre-release off. Earlier releases can remain.
6. Download through the unchanged link above and compare its SHA-256 with release/SHA256SUMS.txt.
7. Devices on an older version can use Settings > Update app, or close and reopen the app for its startup check. Accept the update and Fire TV's installer confirmation. Skipped releases remain available through the manual settings action.

The development stick was authorised for direct installation; once it has 0.3.0, it will not offer that same version as an update. Other devices running an earlier version can test the updater after publication.

The prebuilt APK uses the existing package and signer, so an in-place update preserves data. The private signing key stays on the original development PC. A build signed with another key cannot update that existing installation.

## Optional GitHub CLI command

After committing and pushing, run from the repository root:

```powershell
gh release create v0.3.0 release/WCW-FireTV-App.apk release/SHA256SUMS.txt release/update.json --repo deklol/WCW-FireTV-App --target main --title "WCW Chronology 0.3.0 - The earlier years" --notes-file release/NOTES.md --latest
```

Inspect an existing v0.3.0 release before replacing any assets. This bundle does not publish automatically.
