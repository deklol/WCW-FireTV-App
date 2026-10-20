# Code structure

The app uses Java, native Android views and AndroidX Media3. It does not need a web server, browser wrapper or background PC.

```text
sources.json                       Editable provider/playlist/collection list
sources.schema.json                JSON editor validation
app/src/main/assets/catalogue.json Initial public catalogue, metadata and chronology
app/src/main/java/uk/local/wcwvault/
  MainActivity.java                Home rows, years, search, details and settings
  SourceConfig.java                Loads sources.json and filters disabled sources
  DirectYoutube.java               Named uploads, explicit dates, hashes and uploader checks
  Catalog.java                     Catalogue model, merging and viewing progress
  CatalogSync.java                 YouTube discovery, progress and scan reports
  ArchiveSync.java                 Archive metadata discovery and file matching
  MetadataCache.java               HTTP validators and stable SHA-256 fingerprints
  LibraryJob.java                  Fire TV's periodic metadata-check job
  Resolver.java                    Archive options and English YouTube stream selection
  YoutubeDataSource.java           Bounded YouTube media requests; no disk video cache
  PlayerActivity.java              Built-in playback, resume and chronological autoplay
  ArchiveRecovery.java             Pause-aware stall detection and bounded reconnects
  Art.java                         Bounded artwork cache
  Ui.java                          Shared native UI styles
  Updater.java                     Release check, checksum and signing verification
  UpdateRelease.java               Release metadata, numeric versions and skip policy
  UpdateProvider.java              Read-only URI for Fire TV's APK installer
app/src/test/                      Catalogue, language, migration and source-manifest tests
docs/                              Build, source and update documentation
scripts/                           Portable source check and release-manifest helpers
third_party/                       Upstream source/license material
.github/workflows/                 Build and test workflow
release/                           Compiled APK, update manifest and checksum
```

## Queue flow

1. A year produces a list of sourced broadcasts sorted by air date and show order.
2. The selected episode establishes the starting position in that list.
3. The resolver ranks known English audio first, then the preferred provider, curated copies and resolution. Known foreign-language copies remain fallbacks; unknown audio is ranked between known English and known foreign audio.
4. The built-in player streams the selected media and saves progress.
5. When the final media part ends, autoplay advances to the next item in the same queue. Unavailable episodes can be skipped with a bounded failure limit.

The dedicated Years page lists the years present in the catalogue. Each pre-1997 year includes every available programme independently of the later-era extras preference. Programme filters are generated from that year's actual content; the top navigation retains the Nitro, Thunder and PPV shortcuts. Homepage rows display newest discoveries first, with broadcast date descending as a tie-breaker. Clicking a card starts its year's full chronological timeline rather than limiting playback to that homepage row.

## Archive playback recovery

While an Archive stream is supposed to be playing, the player samples its position and buffered position once per second. After six seconds without progress, it stops and prepares the existing media playlist at the same part and timestamp to reopen the connection. Growing buffers, user pauses and audio-focus suppression prevent recovery actions. Two reconnects are allowed before trying the next listed source; 30 seconds of healthy playback restores the retry budget. Ordinary network/decoder errors still use the existing source-fallback path. Archive reads time out after eight seconds and rebuffering requires two seconds of queued media. This cannot eliminate a host outage, but it avoids waiting indefinitely for a frozen connection.

## Viewing history

Playback saves the current part/time every five seconds and when leaving the player. Episode cards show a red progress bar, watched percentage and completion tick from the same device-local history. The actual measured duration is saved when known; multipart offsets use observed previous-part lengths and the catalogue total until every part length is known. Without a known total the card shows elapsed minutes. Completing the final part marks the whole show watched; the detail panel can mark it watched or unwatched manually. Resume prioritises the saved source within its language tier, so a saved German copy cannot outrank an available known English alternative. Clear viewing history removes these progress records and source choices without clearing the catalogue or source-refresh settings.

## Refresh flow

The app displays the saved catalogue first. A six-hour OS job and an overdue check while browsing trigger refreshes. Playback has priority; a deferred refresh resumes after returning to the library. YouTube playlists and a rotating batch of Archive items are independent checks. Errors stay attached to the named source and do not cancel the others. A completed catalogue is written atomically.

Only genuinely new broadcasts receive a discovery timestamp. New encodes/hosts for an existing episode increase its source count without pretending a new episode appeared.

## Storage

Viewing progress is stored in app preferences. The catalogue is stored in the app's files directory. Artwork has an 8 MiB memory cache and a bounded disk cache; validated HTTP metadata has a separate bounded cache. Playback buffers media in RAM and does not create video files on the stick. An explicitly accepted app update downloads an APK separately from media playback.

## APK updates

Startup checks query the official GitHub latest stable release and cross-check its optional update manifest against the APK asset digest and URL. Newer releases can be accepted, deferred or skipped. Manual Settings updates bypass the skipped-version preference. Prompts wait until the library is in the foreground, and downloads show progress and cancellation. APKs are staged in the app cache, verified by SHA-256, package/version, minimum Android API and matching signer set, then shared read-only with the native package installer. API 28+ requests both signing-certificate and legacy signature fields for Fire OS archive-parser compatibility, preferring current signers whenever available. The debug-only test activity can host the same updater independently; it is excluded from release APKs.
