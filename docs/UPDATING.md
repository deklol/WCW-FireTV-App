# Publishing and app updates

Catalogue/source refresh is automatic and independent of APK updates. The online APK release feed is **not connected by default** because the final GitHub repository URL has not been chosen.

## Publish a release

1. Build and test a signed release APK using your private key.
2. Increase `versionCode` and `versionName` for each future release.
3. Create a GitHub release, for example `v0.2.0`.
4. Upload the APK, a SHA-256 checksum file and `update.json` as release assets. GitHub supplies a source archive from the corresponding tag; tag the exact source used to build the APK.
5. Keep private keystores, passwords and local build directories out of Git.

Generate a manifest after the repository URL is known:

```text
python scripts/make_update_manifest.py release/WCW-Chronology-0.2.0.apk --url https://github.com/YOUR_NAME/YOUR_REPO/releases/download/v0.2.0/WCW-Chronology-0.2.0.apk --output release/update.json
```

The helper reads the version from `app/build.gradle` and computes the actual APK SHA-256.

## Connect the installed app

In **Settings → Update feed**, enter:

```text
https://github.com/YOUR_NAME/YOUR_REPO/releases/latest/download/update.json
```

**Check for app updates** reads that HTTPS manifest. The app verifies the APK hash, package name, version and matching signing certificate before invoking Fire TV's own package installer. The user confirms installation. The app does not silently install APKs or accept updates signed by someone else's key.

The included `release/update.json.example` needs your real GitHub URL before use. The original maintainer's private signing key is not in this source bundle. Forks should use their own key; they cannot silently replace a differently signed installed app.
