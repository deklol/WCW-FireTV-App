# Validation for 0.2.0

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
