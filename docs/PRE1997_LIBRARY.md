# Pre-1997 library in 0.3.2

Inventory checked on 20 October 2026. The seed adds **1,278 distinct pre-1997 broadcasts**, for **1,950 total entries**. Early-year coverage is uneven; a year card means some material exists, not that every show from that year is available.

## Year totals

| Year | Broadcasts/events |
| --- | ---: |
| 1983 | 4 |
| 1984 | 1 |
| 1985 | 12 |
| 1986 | 42 |
| 1987 | 39 |
| 1988 | 8 |
| 1989 | 10 |
| 1990 | 203 |
| 1991 | 245 |
| 1992 | 191 |
| 1993 | 105 |
| 1994 | 128 |
| 1995 | 135 |
| 1996 | 155 |

## Programme totals

| Programme | Broadcasts/events |
| --- | ---: |
| All Nighter | 2 |
| Clash of the Champions | 33 |
| Main Event | 186 |
| Nitro | 68 |
| PPV | 72 |
| Power Hour | 189 |
| Prime | 42 |
| Pro | 219 |
| Saturday Night | 274 |
| Worldwide | 193 |

World Championship Wrestling and Saturday Night share the same internal programme family. The earlier name is displayed before the 4 April 1992 rename. Clash events are grouped under PPV in the interface while keeping their event titles.

The 68 pre-1997 Nitro dates match the independent TVmaze episode guide: 17 in 1995 and 51 in 1996, with no missing or unexpected dates. All 33 pre-1997 Clash dates are represented. A separate audit paginated the official WCW channel's 716 listed uploads and all 12 playlists; the configured fifth playlist supplies older World Championship Wrestling episodes. The WCW-channel early weekly uploads identified in that audit date from 1985-1987. The separate WWE Vault additions now supply World Championship Wrestling episodes from 18 June, 25 June and 2 July 1983. Those Georgia Championship Wrestling broadcasts appear under the historical World Championship Wrestling name. The 1983 and 1984 Starrcade events remain included from Archive.org.

Sources include separate Great American Bash tour editions, Crockett Cup 1986, When Worlds Collide 1994, the 1991 WCW/New Japan Supershow, Collision in Korea and both All Nighter specials. Collision in Korea is dated by its US PPV broadcast on 4 August 1995; it was recorded on 28-29 April. Its English broadcast edit and longer mixed/Japanese versions are alternatives for the same event.

The full-length Worldwide broadcast dated 20 April 1985 is also included and passed bounded delivery/runtime checks.

## What was verified

- The original 1,262-candidate pass confirmed 1,176 Archive deliveries and 86 YouTube-only broadcasts. Fourteen transient Archive failures and one YouTube timeout passed subsequent retries.
- Archive checks fetched bounded beginning/end byte ranges, confirmed media-container delivery and independently parsed the MP4 runtime. Full media files were not downloaded or stored.
- The 86 YouTube-only entries were resolved with the same NewPipeExtractor version used by the app. Checks confirmed the official WCW channel, compatible H.264 video, English/original audio, and actual partial video/audio delivery.
- Eleven additional curated events/specials also passed Archive delivery/runtime checks. Every imported broadcast therefore has at least one checked delivery source. An untested YouTube alternative may also be present when a checked Archive source exists.
- Six individually registered WWE Vault uploads also passed compatible 720p H.264 and actual partial video/audio delivery checks with the app's extractor version. Four add broadcasts; two add source options. Their legacy audio tracks have no language metadata, so these checks do not assert a language tag that the host does not provide. See [WWE_VAULT.md](WWE_VAULT.md).
- These are transport, identity and runtime checks, not complete end-to-end viewings. They cannot establish that every recording is unedited, high quality or permanently available. Some Archive copies are VHS-derived; short/explicitly partial candidates were excluded by programme-specific rules.

The [public source register](PRE1997_SOURCES.csv) lists each imported source, its broadcast identity, runtime and whether that particular source was stream-checked. The editable runtime manifest is the root [sources.json](../sources.json).

## Existing library and Millennium Final

Existing source references are retained. Four legacy 1999 Worldwide dates are corrected in 0.3.2 with saved-progress recovery; see [the Worldwide audit](WORLDWIDE_SOURCE_AUDIT.md). One legacy duplicate of Clash XIII, incorrectly filed as 14 January 2001, is merged into 20 November 1990 without losing its YouTube link. Existing cached history is transferred when no independent progress already exists on the correct entry.

No better public Chasing That W copy of Millennium Final was confirmed. The creator/title index and 50 relevant 2000 items containing 129 video files were checked, including date-only uploads. The two existing links appear to be the same 240p recording with German commentary. They remain available as fallback sources rather than being replaced with unrelated matches or an invented quality upgrade. Known English copies take priority whenever available.

## Reference checks

- [Official WCW channel](https://www.youtube.com/@WCW)
- [Older WCW full episodes](https://www.youtube.com/playlist?list=PLqeI_ua6LQHF4w___rh980zXRn1ES83M0)
- [Official Nitro playlist](https://www.youtube.com/playlist?list=PLqeI_ua6LQHFR45AlUQEPNjfig3o0W9zx)
- [Official event playlist](https://www.youtube.com/playlist?list=PLqeI_ua6LQHFBDB6i0rJTw4Z2SRUr_Fb4)
- [Official Clash playlist](https://www.youtube.com/playlist?list=PLqeI_ua6LQHHSKExkeTqIq2hhqCtFr9jf)
- [TVmaze Nitro episode guide](https://www.tvmaze.com/shows/11446/wcw-monday-nitro/episodes)
- [Clash event dates](https://en.wikipedia.org/wiki/Clash_of_the_Champions)
- [WCW Saturday Night history](https://en.wikipedia.org/wiki/WCW_Saturday_Night)
- [Chasing That W public creator search](https://archive.org/search?query=creator%3A%22Chasing+That+W%22)
