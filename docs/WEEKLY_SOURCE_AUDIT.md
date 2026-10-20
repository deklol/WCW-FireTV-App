# Nitro and Thunder source audit — 0.2.7

The user's examples were duplicate Nitro/Thunder entries for 19 and 26 January 2000. Both dates were already correctly classified as Thunder in the bundled catalogue. The incorrect Nitro entries could be created by a subsequent Archive refresh.

## Cause

The `jan-3-2000-dec-27-2000` Archive item is titled **WCW 2000 Ortin Rednuht FULL YEAR IN ORDER** and contains both shows. Files such as `01 19 2000 T.mp4` and `01 26 2000 T.mp4` are Thunder. The scanner ignored the `T` marker and inherited an incorrect Nitro collection hint.

Public metadata for all 126 configured Archive items was checked. Three mixed items use dated short show markers:

- `jan-3-2000-dec-27-2000`: Nitro and Thunder.
- `Jan-01-2001-Mar-26-2001`: Nitro, Thunder and PPVs.
- `jan-06-1997-dec-29-1997`: Nitro, Saturday Night and events.

## Correction

The file's explicit show marker takes precedence over a collection hint or a stale cached file mapping. Anchored dated suffixes `N`, `T`, `SN` and `COTC` identify their respective shows. The whole word `Thundercage` does not identify a Thunder episode. Mixed Nitro/Thunder titles cannot supply a single inherited family.

These three collections have an explicit, verified `dateOrder: MDY` in the editable source manifest. Unconfigured ambiguous numeric dates remain unassigned. No weekday heuristic is used.

On load, cached sources with explicit conflicting show/date evidence move into the appropriate existing episode. Identical URLs are deduplicated. If the incorrect entry becomes empty and all its sources moved to one episode, resume/watched state transfers unless the correct episode already has its own recorded history. Existing history keys are retained. Library links to the last watched episode follow the corrected ID.

## Preservation tests

- All 661 bundled episodes and their source assignments remain unchanged by normalization.
- Simulated incorrect Nitro entries for both reported dates merge into Thunder with exactly the same set of playable links.
- A scan against trimmed real metadata from all three mixed collections retains all 2,202 original catalogue source links. The result has 666 episodes and 2,464 distinct source links.
- Every eligible explicitly marked Nitro/Thunder file in those snapshots appears under its indicated show and date.
- Repeat scans do not add duplicates. Repeat cache repairs are idempotent.
- Progress migration does not overwrite progress already recorded on the correct episode.

The test fixture includes only public item titles and file metadata; no video downloads, account data or private device state. Metadata matching is not a full visual inspection of every recording.
