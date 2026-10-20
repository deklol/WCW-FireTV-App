# Changelog

## 0.3.3

- Introduce Wrestling Chronology on launch, covering WCW, ECW, WWF/WWE and TNA/Impact, with Download and install, Not now and Don't show again choices.
- Keep Wrestling Chronology available at the top of Settings, including GitHub and Downloader code **2909774** as alternative installation routes. Devices with the new app installed can open it from there and do not receive the launch install notice.
- Download the new app's current GitHub release directly, check its SHA-256, package, version and Android requirement, then request normal system installation. Existing copies must have a compatible signing certificate and cannot be downgraded.
- Keep the new app's download separate from WCW self-updates. Explain install permissions and provide recovery instructions when direct installation is unavailable.

## 0.3.2

- Replace the add-on's explicit missing-image search icon with a local white-on-black WCW placeholder. Retain genuine Archive and other episode thumbnails, event posters and YouTube thumbnails in cards, details and the resume hero; failed artwork requests retain the placeholder.
- Keep complete Worldwide episodes after metadata refresh by applying the programme-specific runtime rule to older cached sources.
- Add 13 dated full-length Worldwide broadcasts: 20 April 1985, 7 December 1997 and 11 missing 1999 dates. Total: 1,950 broadcasts, including 294 Worldwide.
- Correct four reversed month/day dates in 1999 without dropping existing source URLs; recover saved progress even if an earlier refresh had removed the old entry.
- Show all available programme filters inside a year. Explicit Worldwide selection works with optional shows disabled; the All autoplay preference is retained.
- Reject explicit partial/incomplete recordings and segments, without rejecting complete episodes merely because a year collection is incomplete.
- No complete 2000-2001 Worldwide episodes were confirmed. New 1999 transport checks were blocked by Archive server failures; these records are supported by dated full-length metadata, not a claim of successful playback verification.

## 0.3.1

- Add six requested WWE Vault uploads through readable individual-video entries in sources.json.
- Add World Championship Wrestling episodes dated 18 June, 25 June and 2 July 1983, plus WCW Live in England from Manchester on 13 March 1993.
- Add official YouTube alternatives for Halloween Havoc 1998 and Saturday Night on 27 August 1994, preserving their existing entries and Archive sources.
- Include individual uploads in quick/full refresh progress, metadata caching, stable fingerprints and per-source failure reports. Verify the configured uploader identity during playback.
- Keep Years ordered newest first. The bundled catalogue now contains 1,937 broadcasts, including 1,277 before 1997.

## 0.3.0

- Add 1,273 pre-1997 broadcasts, including all 68 Nitro dates in 1995–1996 and all 33 earlier Clash events, plus available older WCW TV, Saturday Night, Worldwide, Pro, Power Hour, Main Event, Prime and All Nighter specials.
- Add a dedicated Years selector beside Home, ordered newest first from 2001; retain Nitro, Thunder, PPV, Search and Settings. Early-year libraries include all available programmes, with filters inside the year.
- Use the historical World Championship Wrestling name before the Saturday Night rename. Extend discovery matching to 1983–2001, short weekly formats, two-digit filename years and numbered Clash events.
- Expand the editable source list to five official WCW playlists and 245 Archive items. Keep per-source scan failures isolated and metadata caching enabled.
- Prefer known English audio before provider/resolution and saved-source preferences. Retain other-language fallback recordings.
- Correct one legacy duplicate of Clash XIII filed under January 2001, retaining its source and transferring saved history to November 1990. Recognise Thanksgiving Thunder as a Clash event.
- Preserve the existing 1997–2001 source inventory and player features. No higher-quality public Millennium Final copy was confirmed; its existing fallback is retained.
- Include the pre-1997 inventory, transport-check scope, source register and regression coverage in the public source bundle.

## 0.2.7

- Correct Nitro/Thunder classification in mixed Archive collections using each file's explicit show marker, before collection hints or cached mappings.
- Repair existing incorrectly labelled entries by moving their sources into the matching show/date and deduplicating identical links.
- Preserve existing correct entries and playable sources. Transfer progress from completely merged incorrect entries without overwriting progress already recorded on the correct episode.
- Configure verified month/day/year conventions for three mixed collections; keep ambiguous dates unassigned elsewhere.
- Add regression coverage for 19 and 26 January 2000, real mixed-collection scans, repeated scans and migration without losing source links.
- Includes all 0.2.6 PPV source fixes.

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
