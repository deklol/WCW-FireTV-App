# Changelog

## 0.2.6

- Remove the Souled Out 2000 countdown preshow and two SuperBrawl 2000 review files from full-event playback, including existing cached catalogues.
- Audit Archive metadata for all 53 PPV-category entries across 65 Archive items. Store per-source runtime, creator and height.
- Prefer the available Chasing That W event copies before resolution and file-size ranking, while preserving YouTube preference and valid saved-source resume.
- Reject preshows, countdowns, reviews, recaps and known short standalone recordings before playback and during future scans.
- Preserve episode IDs, watch history and valid multipart sources. Metadata checks do not guarantee visual completeness.

## 0.2.5

- Show the unfinished episode's artwork, title, date, description and progress in the home hero. Use "WCW, in order." when there is nothing to resume.
- Check the official GitHub latest stable release automatically on launch.
- Offer Update, Later and persistent Skip this version; Settings → Update app can still install a skipped version.
- Show download progress and cancellation, verify the APK's SHA-256, package, version, Android compatibility and signer, and use Fire TV's native install confirmation.
- Allow disabling automatic checks or configuring a custom release manifest. Defer update prompts during playback.

## 0.2.4

- Reconnect stalled Archive.org streams after six seconds without progress instead of twelve.
- Display episode progress bars, watched percentages and completed-show markers on home and catalogue cards. Add Mark unwatched to correct viewing history.
- Prefer the previously used source when resuming, and track observed durations and multipart progress.
- Group Clash of the Champions events under PPV instead of a separate category, preserving event names and history IDs.

## 0.2.2

- Automatically reconnect stalled Archive.org playback at the same part and position after 12 seconds without playback or buffer progress.
- Respect user pauses and audio-focus suppression; allow two reconnects before trying the next source. Reset the retry budget after 30 seconds of healthy playback.
- Resume Archive.org playback after two seconds of buffered media and time out stalled reads after eight seconds.
- Report buffering correctly to remote media controls and allow play/pause to pause while buffering.

## 0.2.1

- Lower minimum Android version to 7.1/API 25 for Fire OS 6 devices.
- Verify update signing certificates with the legacy package API on Android 7.1 and 8.
- Retain Home navigation, newest-discovery-first home rows and chronological playback.
- Package the signed APK as WCW-FireTV-App.apk to preserve the existing Downloader URL.

## 0.2.0

- Latest-found rows show newest discoveries first, while playback remains chronological.
- Home button at the left of the top navigation returns to the homepage.
- Native dark Fire TV library with separate recently found Nitro, Thunder and PPV rows.
- Year-based chronological autoplay, resume positions, watched markers and in-app streaming.
- Original English YouTube audio selection with a fallback for unlabelled original tracks.
- Bounded YouTube media requests and memory buffering suitable for the tested Fire Stick.
- Periodic source discovery, full manual scans, individual failure reports and cache fingerprints.
- Editable root source manifest, source schema, public build instructions and GitHub Actions checks.
- WCW intro/hero artwork, app icon and centred digitalm1nd home-page credit.
