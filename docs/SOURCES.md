# Editing sources

The root **`sources.json`** is the source manifest. Gradle copies exactly that file into the APK. Keep it as valid JSON; use the `name` and `description` fields for explanations rather than JavaScript comments. `sources.schema.json` supplies editor validation.

## YouTube

Add a playlist to `youtube.playlists`:

```json
{
  "id": "PL_REPLACE_WITH_REAL_PLAYLIST_ID",
  "name": "WCW Nitro full episodes",
  "show": "Nitro",
  "enabled": true
}
```

`show` identifies the playlist's programme; the older full-episode playlist uses `Saturday Night`, with the historical World Championship Wrestling name displayed for earlier broadcasts. The configured `channelId` identifies the source channel. The default is the official WCW channel, `UCbGG2hv4cWmIrBAIhGiCdSg`.

Only titles containing **FULL EPISODE** or **FULL EVENT**, with a confidently matched broadcast/event date from 1983-01-01 through 2001-03-26 are admitted. The minimum listed duration is 20 minutes for full episodes and 45 minutes for full events. Full matches, highlights, live watch parties and compilations are excluded. `Thundercage` in a PPV title does not make that event a Thunder episode. Clash events are resolved by their Arabic/Roman event number when supplied. Unknown or conflicting dates are skipped rather than guessed.

## Archive.org

Add a collection under `archive.collections`:

```json
{
  "id": "REPLACE_WITH_ARCHIVE_ITEM_IDENTIFIER",
  "name": "WCW Nitro 1999 full broadcasts",
  "showHint": "Nitro",
  "enabled": true
}
```

The identifier is the part after `/details/` in an Archive item URL. Supply an **item containing video files**, not a top-level Archive collection containing other items. `showHint` is optional; use it when date-only filenames need a show identity. Omit it for mixed Nitro/Thunder/PPV items. Broadcast dates must still be established independently.

Use `"dateOrder": "MDY"` or `"dateOrder": "DMY"` only after checking the uploader's numeric filename convention. Otherwise ambiguous numeric dates are skipped. Explicit per-file show names and dated filename suffixes `N` (Nitro), `T` (Thunder), `SN` (Saturday Night), and `COTC` (Clash) take precedence over a collection hint. Mixed Nitro/Thunder collection titles cannot supply a single inherited show. Existing cached misclassifications are repaired on load, with source deduplication and progress transferred when an incorrect entry is completely merged into one correct entry.

The scanner reads public metadata and accepts MP4/M4V files with programme-specific minimum durations: Nitro 40 minutes; Worldwide, Pro, Power Hour and Prime 30 minutes; Main Event 20 minutes; other formats 45 minutes. These thresholds reject obvious short fragments without discarding full shorter weekly formats. They cannot prove that a recording is uncut. The scanner rejects private files, clips, samples and explicitly incomplete parts, preserves supplied complete multipart sources, and maps dated files to existing episodes where possible so a second encode is an extra source rather than a new episode.

`collectionsPerScan` defaults to **12**, with a supported range of 1–50. Collections are checked in rotation, keeping each scan light. With the current 245 items and a six-hour schedule, one complete Archive rotation takes approximately five days, subject to OS scheduling and playback deferrals. Every scan checks all five enabled YouTube playlists. The app does not search arbitrary new Archive accounts or collections automatically: add those item IDs to this file.

For an immediate complete pass, use **Settings → Full source scan**. This checks every enabled Archive item as well as all YouTube playlists; it can take longer than the normal quick refresh. A failed item is skipped with its reason while the remaining items continue.

## Disable or remove

Set an entry's `enabled` to `false`, delete the entry, or disable an entire provider with its top-level `enabled` switch. Rebuild and install the APK with the same signing key. Cached source options from disabled/removed entries are filtered out at startup; viewing progress remains saved.

## Caching and scan results

- The saved catalogue starts immediately, including when a provider is unavailable.
- HTTP ETag/Last-Modified validators are used when the host supplies them.
- Stable fingerprints detect unchanged playlist/file lists; transient YouTube tracking values are excluded from those hashes.
- The scan shows the current YouTube playlist or Archive item ID. Individual failures are reported and the other checks continue.
- **Settings → Last refresh report** lists checked and skipped sources. Failed checks are retried later; existing known-good links are retained.
- “New episode” means a previously unlisted broadcast. Another link or encode for an existing broadcast is counted separately and does not reorder the latest-episode rows.

After editing, run `python scripts/check_sources.py` and build/test the app as described in [BUILDING.md](BUILDING.md).
