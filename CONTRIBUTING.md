# Contributing

Start with [BUILDING.md](docs/BUILDING.md) and [ARCHITECTURE.md](docs/ARCHITECTURE.md). Source-list changes belong in the root `sources.json`; see [SOURCES.md](docs/SOURCES.md).

Keep the app responsive on the tested 1 GB Fire Stick. Preserve direct streaming, original English audio, remote navigation, bounded caches and chronological autoplay. Do not add account credentials, cookies, private API keys or video downloads to the repository.

Run `python scripts/check_sources.py`, the Java unit tests, Android lint and a debug build. For playback changes, verify real Archive and official YouTube playback, seeking and automatic advancement on a TV. Do not describe a provider as working from metadata extraction alone.

Use readable Java formatting. The current source is formatted with google-java-format 1.25.2. Keep changes focused and include a short explanation of the behavior and tests in pull requests. Screenshot UI changes when useful.

Contributions to app code are provided under GPL-3.0-or-later. Respect upstream licence notices and the separate rights in third-party artwork and branding.
