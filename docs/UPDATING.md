# Publishing and app updates

Catalogue/source refresh is automatic and independent of APK updates. From 0.2.5, app updates are connected to the latest stable release at https://github.com/deklol/WCW-FireTV-App by default.

The app checks in the background on launch. A newer version offers **Update**, **Later** and **Skip this version**. Skipping persists between launches and applies only to that version. **Settings → Update app** always checks manually, including versions previously skipped. Automatic launch checks can be turned off in Settings.

Choosing Update downloads the APK with a progress bar and cancellation control, verifies its checksum, package, newer version, Android compatibility and matching signer, then opens Fire TV's installation confirmation. Fire TV may first require allowing WCW Chronology to install apps from unknown sources. Installation keeps the existing app data. This feature does not bypass the system's installation confirmation.

## Publish a release

1. Build and test a signed release APK using your private key.
2. Increase `versionCode` and `versionName` for each future release.
3. Create a GitHub release, for example `v0.2.5`, and mark it as the latest stable release.
4. Upload the APK, a SHA-256 checksum file and `update.json` as release assets. GitHub supplies a source archive from the corresponding tag; tag the exact source used to build the APK.
5. Keep private keystores, passwords and local build directories out of Git.

Keep the asset name exactly `WCW-FireTV-App.apk`. Generate its manifest:

```text
python scripts/make_update_manifest.py release/WCW-FireTV-App.apk --url https://github.com/deklol/WCW-FireTV-App/releases/download/v0.2.5/WCW-FireTV-App.apk --output release/update.json
```

The helper reads the version from `app/build.gradle` and computes the actual APK SHA-256.

## Release checks and custom feeds

No configuration is needed for the official repository. The default check reads GitHub's latest stable release API and its APK SHA-256 digest. When `update.json` is present, its version, APK URL and hash must match that release asset. Without a manifest, the release tag is compared numerically and GitHub's asset digest verifies the download.

Forks can set a custom HTTPS manifest URL in **Settings → Update feed**, for example:

```text
https://github.com/YOUR_NAME/YOUR_REPO/releases/latest/download/update.json
```

Leaving the custom feed empty restores official GitHub release checking. An offline check does not block opening the library. Updates are offered only for newer versions; a changed hash alone does not force a reinstall of the same version. Update prompts are deferred while this app is playing video.

The supplied `release/update.json` is ready for the official repository and packaged APK. The original maintainer's private signing key is not in this source bundle. Forks should use their own key; they cannot replace a differently signed installed app as an update.
