# Validation

## 0.2.5 in-app updater

- Signed release build and 22 Java tests pass; Android lint has 0 errors and 22 warnings (including the debug-only test host).
- Six updater tests cover persistent version skipping with manual override, later versions after a skip, downgrade/same-version rejection, numeric version comparison, stable GitHub asset selection, SHA-256 metadata and HTTPS validation.
- On the AFTSS Fire OS 7 stick, a separate debug-only test host used the production updater against the real published v0.2.4 release. The automatic prompt appeared, Skip persisted across force-stop/relaunch, and the manual Update app action offered that skipped release again.
- The updater downloaded the real 7,048,221-byte APK, verified SHA-256 `91cdf051d79238fab443e675466530f34db847d8cab478048ed30c83c6b9d625`, checked package/version/Android compatibility and compared signing certificates with the existing WCW installation.
- The native Fire TV installer opened and completed installation with **App installed.** Production viewing data was retained. Installation permission was granted only to the disposable test host, which was then uninstalled; the production app's installation permission was not changed.
- Fire OS required requesting the legacy signature field alongside API 28 signing information when parsing the APK archive. Both signing paths retain strict matching signer-set checks.
- Test APKs and device records are excluded from the public bundle. The test activity and manifest exist only in the debug source set and are absent from release APKs.
- The final 0.2.5 release (versionCode 7) was installed in place on the AFTSS stick and its MainActivity launch was verified, preserving viewing history.
- The updated hero was visually checked with the user's existing unfinished Nitro: episode artwork, title, 3 January 2000 broadcast date, available description, measured duration, saved time and 67% progress were displayed. With no unfinished episode the default copy is "WCW, in order." No viewing history was cleared for this check.

## 0.2.4 progress and faster recovery update

- Signed release build and all 16 Java tests passed; Android lint reports 0 errors and 21 warnings.
- Stall-policy tests check no reconnect before six seconds, recovery at six seconds, pause/focus protection, growing buffers, multipart/seek handling and bounded retry/fallback.
- New progress tests cover percentages, unknown duration and watched completion. Another test verifies Clash events use PPV while preserving their original title and ID.
- Playback history stores the actual observed duration where available and retains existing resume positions. Multipart progress uses measured prior-part durations; unknown totals fall back to catalogue metadata. Unknown durations show elapsed minutes instead of an invented percentage.
- Resuming prefers the exact previously selected source, then its provider, before other sources. Six-second stall recovery still needs confirmation against the rare live network issue.
- Installed version 0.2.3 on the existing AFTSS stick using an in-place update, keeping its data and a rollback APK. Resumed the user's existing 3 January 2000 Nitro on Archive.org at approximately 50 minutes; the active media session reported playback, and returning to the library showed a red progress bar and **52% watched**. No sample viewing-history entries were injected.

## 0.2.2 Archive recovery update

- Signed release build and all 14 Java tests passed, including five new simulated-timeline tests for stalled playback, bounded retry/fallback, growing buffers, pause/focus suppression, seeks, multipart transitions and retry-budget recovery after healthy playback.
- Android lint: 0 errors, 21 warnings, including the new non-localised reconnect status text. Minimum API remains 25.
- The recovery action reopens the existing playlist at the captured part and playback position; it does not intentionally advance the video.
- A rare live Archive.org stall has not been reproduced and recovered on a TV with this build. Unit tests validate the detection/retry policy; they do not prove end-to-end network recovery on the user's connection. This release still needs that device check.

## 0.2.1 compatibility update

- Signed release build and all nine Java tests passed with minimum Android API 25.
- Android lint: 0 errors, 20 warnings; no unguarded newer Android API calls reported.
- APK signature verified explicitly for Android API 25; manifest reports minimum API 25, versionCode 3 and versionName 0.2.1.
- Update verification uses Android's legacy signature API below API 28, while retaining package, version, checksum and complete signer-set checks.
- Fire OS 6 installation and playback have not yet been tested on the user's second device. These checks establish build/API/package compatibility, not confirmed playback on that model.

## 0.2.0 device tests

Tested on Fire TV Stick HD **AFTSS / sheldon**, Fire OS **7.7.1.7**, on 19 October 2026.

- Debug and signed release APK builds completed.
- A clean exported copy built successfully through the supplied Gradle wrapper and passed all nine Java tests, without a project-local SDK path or files from the original add-on.
- Nine Java tests passed: English/original audio preference, legacy unlabelled audio, source configuration, date matching, old-event rejection, cache migration, Archive full-show filtering, stable fingerprints and separate home rows ordered newest-discovery-first. The row test verifies that a newly discovered older broadcast appears ahead of later broadcasts.
- Android lint completed with **0 errors**. Remaining warnings concern pinned dependency versions, fixed TV orientation and UI/style/API suggestions.
- Real official YouTube playback reached the first video frame at 720p. The original English Nitro track was selected; playback was confirmed smooth on the TV after the stream-request correction.
- A YouTube PPV with unlabelled legacy audio also reached its first frame at 720p in the built-in player.
- Archive streaming reached its first video frame. An eight-second test clip completed and automatically advanced from Nitro on 20 January 1997 to Souled Out on 25 January 1997. This uses the normal queue/end-of-playback path without marking test clips watched.
- A deliberately failed YouTube source check was reported as skipped. The other 15 checks completed; the catalogue retained 661 broadcasts and did not invent a new Thunder episode.
- A subsequent real quick refresh completed in approximately 13 seconds, detected unchanged YouTube playlist fingerprints and reported zero new episodes. Archive batches rotate between checks.
- Homepage hero, three latest-found rails, year cards, PPV posters and the centred project credit were visually inspected.
- The final top menu was checked on the signed release: Home, Nitro, Thunder, PPV, Search, Settings. Home returned from the PPV catalogue to the homepage. Nitro and Thunder opened their respective catalogues with year filters; the PPV catalogue showed 52 sourced events.
- Back-button handling was checked during playback: first press hid the controls and kept the player open; a second press returned to the library.

GitHub-hosted CI execution and an online APK-update feed require publication to the maintainer's chosen repository. They were not represented as live services during device testing. Other Fire TV models/OS builds are not claimed as tested.

Release numbering corrected from the locally tested 0.2.3 build to 0.2.4 (versionCode 6). The player and library behavior are unchanged.
