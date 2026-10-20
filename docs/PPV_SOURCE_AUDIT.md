# PPV source audit — 0.2.6

Public Archive.org metadata was checked on 20 October 2026 for all 53 PPV-category catalogue entries (52 labelled PPV and one Clash event), covering 65 Archive items. The audit fetched metadata only, not entire recordings.

## Confirmed incorrect alternatives

| Event | Removed file | Runtime | Reason |
| --- | --- | --- | --- |
| Souled Out 2000 | WCW Souled Out 2000 Countdown Show.ia.mp4 | 30:01 | Countdown preshow, not the PPV |
| SuperBrawl 2000 | WCW SuperBrawl 2000 - The Reliving The War PPV [MB5krxpFNfE].mkv | 33:37 | Wrestling Bios review |
| SuperBrawl 2000 | WCW SuperBrawl 2000 - The Reliving The War PPV [MB5krxpFNfE].mp4 | 33:37 | Derivative of the same review |

The selector previously preferred a 480p alternative to an above-target 1080p file at the default 720p setting. Completeness was not checked before this resolution preference, allowing a preshow or review to win.

## Corrected selection

The [Chasing That W Souled Out upload](https://archive.org/details/jan.-16-2000) was already bundled. Its original MP4 has a reported runtime of 9,759.17 seconds (2:42:39), dimensions 1920 × 1080 and size 4,296,840,772 bytes. The corrected default Archive selection uses this upload.

Chasing That W copies were found in the existing catalogue for 51 of its 52 PPV-labelled events. Millennium Final has another existing recording; the separately classified Clash entry uses its existing sources. No missing Archive files or additional suspiciously short recordings were identified among the audited PPV alternatives. This is not proof that every video contains every match, or that nominal 1080p means native high-definition footage.

`SourcePolicy.java` rejects preshows, countdowns, recaps, reviews and known standalone runtimes below 45 minutes. Valid multipart sources remain eligible. Within the chosen provider, Chasing That W sources with a reported runtime of at least 90 minutes are preferred before resolution and file size. YouTube preference remains in effect, and valid saved-source resume still takes precedence.

The seed stores `duration` in seconds, `creator` and `height` on audited Archive alternatives. Existing cached URLs receive that metadata during seed merging. Catalogue replacement removes invalid alternatives, so upgrading requires no cache reset and preserves episode IDs and viewing history. Future Archive refreshes apply the same eligibility policy and record source metadata.

The public uploader's claim of completeness is not treated as independent visual verification. No full-length visual audit was performed. Scans still check configured Archive collections; this release does not add unrestricted Archive search.
