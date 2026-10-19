# Building

## Requirements

- JDK 17.
- Android SDK Platform 35, Build Tools 34.0.0 and platform-tools.
- Internet access for Gradle, Maven Central, Google's Maven repository and JitPack.
- Optional Python 3.10+ for the source checker and release-manifest helper.

Install the SDK with Android Studio's SDK Manager or the Android command-line tools:

```text
sdkmanager "platforms;android-35" "build-tools;34.0.0" "platform-tools"
sdkmanager --licenses
```

Set `ANDROID_HOME` to the SDK directory, or create an uncommitted `local.properties` containing `sdk.dir=/your/android/sdk`. Set `JAVA_HOME` to JDK 17.

## Test/debug APK

Windows PowerShell:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Linux/macOS:

```sh
chmod +x gradlew
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`. Android's local debug key signs this build. A different developer/CI machine normally has a different key, so its build cannot replace a differently signed installed copy without uninstalling that copy.

The project deliberately targets API 28 for the tested Fire OS 7 sideloading environment and compiles against API 35. The Google Play target-policy lint check is disabled specifically for that reason; the rest of lint remains enabled. This is not a Google Play submission configuration.

## Signed release APK

Use a private signing key and keep it outside the repository. Configure these environment variables:

```text
WCW_KEYSTORE=/absolute/path/to/private-release.jks
WCW_STORE_PASSWORD=your-store-password
WCW_KEY_ALIAS=your-key-alias
WCW_KEY_PASSWORD=your-key-password
```

Then run:

```text
gradlew :app:assembleRelease
```

On Windows use `gradlew.bat`; on Unix use `./gradlew`. Output: `app/build/outputs/apk/release/app-release.apk`. Without signing variables, the release output is unsigned and cannot be installed directly. Signing keys and passwords are intentionally not supplied with the public source.

Preserve the same application ID and signing key across updates to retain users' app data. Increase `versionCode` for each published update and keep `versionName` aligned with the GitHub release tag.

## Install for testing

```text
adb connect FIRE_TV_IP:5555
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Accept Fire TV's ADB prompt on the TV. These commands install only this app; no root, system-package disabling or launcher changes are part of the project.

## Reproducibility

Gradle 8.7 and Android Gradle Plugin 8.6.1 are pinned. Direct dependencies are pinned in `app/build.gradle`. The wrapper download has an official SHA-256 check. The bundled catalogue is sufficient to build independently of the original Stremio add-on. NewPipeExtractor's exact upstream source archive is provided under `third_party/`; it is not needed as a local build dependency because Gradle obtains the published artifact.
