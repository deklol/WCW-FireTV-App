# Worldwide source audit - 0.3.2

Checked 20 October 2026. This pass adds **13 dated full-length broadcasts**, bringing Worldwide from **281 to 294** records. The full catalogue contains **1,950** broadcasts. No full 2000-2001 Worldwide episode was confirmed from the sources investigated.

| Year | Before | After |
| --- | ---: | ---: |
| 1985 | 0 | 1 |
| 1990 | 28 | 28 |
| 1991 | 46 | 46 |
| 1992 | 46 | 46 |
| 1993 | 8 | 8 |
| 1994 | 50 | 50 |
| 1995 | 14 | 14 |
| 1997 | 49 | 50 |
| 1999 | 40 | 51 |

## Added dates

1985-04-20, 1997-12-07, 1999-04-10, 1999-05-01, 1999-05-08, 1999-06-05, 1999-06-12, 1999-07-03, 1999-07-10, 1999-08-07, 1999-09-11, 1999-10-02, 1999-12-04.

The 1999 collection uses month/day/year filenames. Four existing records had those fields reversed:

| Previous incorrect date | Correct date |
| --- | --- |
| 1999-04-09 | 1999-09-04 |
| 1999-06-11 | 1999-11-06 |
| 1999-09-10 | 1999-10-09 |
| 1999-11-12 | 1999-12-11 |

Existing URLs remain attached to the corrected records. Cached copies are merged by source identity, and old saved positions transfer only if the correct destination has no independent progress.

## Full-show screening and limits

- Included files have individual broadcast dates, normal full-show runtimes and source context identifying full broadcasts. The 1999 complete-year collection supplies 51 dated recordings. Minimum duration alone is not proof that a recording is complete.
- Explicit partial/INC files, single matches, clips, segments and undated monthly compilations are excluded. The split 28 July 1990 recording, explicit incomplete 4 August 1990 recording and unconfirmed short 29 October 1994 copy were not added.
- New bounded delivery checks passed for 20 April 1985 (3,146 seconds) and 7 December 1997 (2,434 seconds). The 15 candidate/correction files in the 1999 collection could not be transport-verified: the default server timed out and the metadata-listed alternative returned HTTP 500. Their dates and runtimes were checked against public file metadata. They are not labelled stream-verified.
- No complete end-to-end editorial viewing is claimed. Archive availability can change. The app retains its per-source refresh failures and normal playback retries.
- Month-labelled 1998/2001 results and an October 2000 item did not establish individual full-broadcast dates and were not imported. This is a coverage gap, not a claim that no other copies exist.

## Runtime and navigation fixes

Legacy sources missing their programme field were falling through to the generic 45-minute minimum after a refresh. The canonical Worldwide identity is now attached before eligibility filtering, retaining normal 30-45 minute full episodes. Other programme thresholds are unchanged.

Year pages offer every available programme as a filter. Selecting Worldwide displays its episodes even with optional shows disabled. That preference still controls which programmes enter the **All** autoplay queue.

## Primary source records

- [20 April 1985](https://archive.org/details/04-20-1985-nwa-worldwide-wrestling)
- [1997 Worldwide collection](https://archive.org/details/wcw-worldwide-complete_202406)
- [1999 Worldwide collection](https://archive.org/details/wcw.-worldwide.-complete_202406)

The new 1985 item and explicit MDY date conventions are included in the editable root `sources.json`, so refreshes use the same source identities as the bundled catalogue.

## Additional 2000-2001 search

Eight duration-filtered YouTube searches returned 221 distinct videos. A broader Archive query returned 340 WCW/World Championship Wrestling items mentioning 2000 or 2001, including existing collections and many reviews or single matches. No new complete, individually dated US Worldwide broadcast was confirmed in this pass.

- [17 March 2000 UK version](https://www.youtube.com/watch?v=uxl681EyJaA), 35:31: dated UK broadcast lead, with opening/closing material visible in its public storyboard. This is a different UK edit; it has not been substituted for an American episode or labelled independently verified as complete.
- [27 January 2001 basement episode upload](https://www.youtube.com/watch?v=gzUmsPGLBbM), 25:03: dated lead with hosts and matches. Completeness remains unconfirmed against the [contemporary broadcast report](https://slashwrestling.com/worldwide/010127.html), so it is excluded under the full-episodes-only requirement.
- The January/February 2001 month-labelled uploads lack individual episode identities and do not establish complete broadcasts. They remain excluded.
- NWA Worldwide dated October 2000 is an unrelated NWA anniversary programme, not the WCW series. Watch parties and match-only results are excluded.

Public storyboards and metadata are useful screening evidence, not a substitute for confirming the complete original programme. Unconfirmed leads remain outside the shipped library.
