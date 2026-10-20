# Publish the PPV source correction release from the authenticated PC

This is the complete WCW Chronology 0.2.6 source bundle and signed APK (versionCode 8, minimum Android API 25). It includes six-second Archive.org stall recovery, episode progress bars, watched/unwatched tracking, source-aware resume and the Fire OS 6 compatibility fix. Clash events now appear under PPV instead of a separate filter. The APK is ready to upload; no rebuild is needed. No private signing key or account credentials are included.

This app includes automatic GitHub update checks on launch, Update/Later/Skip this version choices, download progress and a manual **Settings → Update app** button that works even for skipped releases. Publish this release once using the steps below; future versions can then be installed from inside the app. Fire TV's own installer confirmation is still required.

The homepage hero now shows the last unfinished episode's artwork, date, description and progress. Without an unfinished show, the heading reads **WCW, in order.**

This release corrects the Souled Out countdown and SuperBrawl review sources, updates audited PPV metadata and prioritises Chasing That W full-event copies. It has not been installed on the test stick, so the user can test the GitHub update flow.

Repository: https://github.com/deklol/WCW-FireTV-App

## Keep the existing Downloader URL

The asset must be named exactly **WCW-FireTV-App.apk**. Publish **v0.2.6** as the latest stable release. This keeps this exact URL working:

https://github.com/deklol/WCW-FireTV-App/releases/latest/download/WCW-FireTV-App.apk

1. Extract this ZIP. Read `docs/VALIDATION.md` for the completed checks and the remaining Fire OS 6 device test.
2. Copy this source tree into the existing repository checkout, preserving its `.git` folder. Review and commit the changes, then push to `main`. Do not copy private signing keys into the repository.
3. Create a new GitHub release tagged **v0.2.6**, targeting that pushed commit. Use `release/NOTES.md` as the release notes.
4. Attach `release/WCW-FireTV-App.apk`, `release/SHA256SUMS.txt` and `release/update.json` to the release. GitHub will generate source ZIP/TAR archives from the tagged commit.
5. Publish it as the **latest release**, with draft and pre-release both off. Earlier releases can stay available.
6. Download through the unchanged URL above and compare its SHA-256 with `release/SHA256SUMS.txt`.
7. To test the in-app updater, leave the existing version installed on the Fire Stick. After publishing, open Settings > Update app, or fully close and reopen the app for its startup check. Accept the update and Fire TV installation confirmation. Verify version 0.2.6 in Settings and try Souled Out 2000. Do not sideload this APK first, because that would bypass the update test.

The prebuilt APK has the same signer and package name as the existing app, so installing it as an update preserves viewing progress. The private signing key remains on the original development PC; rebuilding on another PC with a different key will produce an APK that cannot update the existing installation.

## Optional GitHub CLI release command

After committing and pushing the source, run this from the repository root on the authenticated PC:

```powershell
gh release create v0.2.6 release/WCW-FireTV-App.apk release/SHA256SUMS.txt release/update.json --repo deklol/WCW-FireTV-App --target main --title "WCW Chronology 0.2.6 - PPV source corrections" --notes-file release/NOTES.md --latest
```

If a v0.2.6 tag or release already exists, inspect it before replacing any asset. This bundle does not publish anything automatically.
