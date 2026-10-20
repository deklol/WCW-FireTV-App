package uk.local.wcwvault;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

/** Optional introduction and a permanent route to the separate, wider wrestling app. */
final class WrestlingApp {
  static final String PACKAGE = "uk.local.wrestlingchronology";
  static final String APK_NAME = "Wrestling-Chronology.apk";
  static final String RELEASE_API =
      "https://api.github.com/repos/deklol/Wrestling-Chronology/releases/latest";
  static final String GITHUB = "https://github.com/deklol/Wrestling-Chronology/releases/latest";
  static final String DOWNLOADER_CODE = "2909774";
  static final String SHORT_URL = "https://aftv.news/2909774";
  static final String HIDDEN = "hide_startup_notice";
  static final String OVERVIEW =
      "Watch WCW, ECW, WWF/WWE and TNA/Impact in one app, with chronological playback."
          + "\n\nYou can download Wrestling Chronology at any time from WCW Chronology's Settings."
          + "\n\nDirect installation needs permission for WCW Chronology to install apps"
          + " from unknown sources. Your device will ask you to confirm installation."
          + "\n\nAlternatively, download the APK from GitHub or enter "
          + DOWNLOADER_CODE
          + " in Downloader (aftv.news/2909774).";

  final Activity activity;
  final Updater installer;
  boolean resumed, considered, destroyed;
  boolean startupAllowed = true;
  AlertDialog notice;

  WrestlingApp(Activity activity) {
    this.activity = activity;
    installer = new Updater(activity, PACKAGE, 0, "0.0", true);
    installer.failure = this::failed;
  }

  SharedPreferences preferences() {
    return activity.getSharedPreferences("wrestling_chronology", 0);
  }

  static boolean shouldOffer(boolean allowed, boolean hidden, boolean installed) {
    return allowed && !hidden && !installed;
  }

  boolean installed() {
    try {
      activity.getPackageManager().getPackageInfo(PACKAGE, 0);
      return true;
    } catch (android.content.pm.PackageManager.NameNotFoundException ignored) {
      return false;
    }
  }

  boolean busy() {
    return (notice != null && notice.isShowing()) || installer.checking || Updater.DOWNLOAD.get();
  }

  void offerStartup() {
    if (!resumed || destroyed || considered || busy() || PlayerActivity.playbackActive) return;
    considered = true;
    if (shouldOffer(startupAllowed, preferences().getBoolean(HIDDEN, false), installed()))
      show(true);
  }

  void show(boolean startup) {
    if (destroyed || !resumed || activity.isFinishing()) return;
    boolean available = installed();
    AlertDialog.Builder builder =
        new AlertDialog.Builder(activity)
            .setTitle("Wrestling Chronology")
            .setMessage(OVERVIEW)
            .setPositiveButton(
                available ? "Open app" : "Download and install",
                (dialog, which) -> {
                  if (available) openApp();
                  else installer.check(false);
                })
            .setNegativeButton(startup ? "Not now" : "Close", null);
    if (startup) {
      builder.setNeutralButton(
          "Don't show again",
          (dialog, which) -> {
            preferences().edit().putBoolean(HIDDEN, true).apply();
            toast("Notice hidden. Wrestling Chronology is still available in Settings.");
          });
    } else {
      builder.setNeutralButton("GitHub / Downloader", (dialog, which) -> alternatives());
    }
    present(builder);
  }

  void alternatives() {
    if (destroyed || !resumed) return;
    present(
        new AlertDialog.Builder(activity)
            .setTitle("Get Wrestling Chronology")
            .setMessage(
                "GitHub releases:\n"
                    + GITHUB
                    + "\n\nDownloader code: "
                    + DOWNLOADER_CODE
                    + "\n"
                    + SHORT_URL
                    + "\n\n"
                    + "In Fire TV's Developer Options, enable Install unknown apps for the app you"
                    + " use to install the APK. On Fire OS 6 this may be called Apps from Unknown"
                    + " Sources.\n\n"
                    + "You can return here from WCW Chronology Settings at any time.")
            .setPositiveButton(
                "Open GitHub",
                (dialog, which) -> {
                  try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB)));
                  } catch (Exception error) {
                    toast(
                        "Open "
                            + SHORT_URL
                            + " in Downloader, or use code "
                            + DOWNLOADER_CODE
                            + ".");
                  }
                })
            .setNeutralButton("Open Downloader", (dialog, which) -> openDownloader())
            .setNegativeButton("Close", null));
  }

  void failed(String message) {
    if (destroyed || activity.isFinishing()) return;
    present(
        new AlertDialog.Builder(activity)
            .setTitle("Couldn't install Wrestling Chronology")
            .setMessage(
                message
                    + "\n\nYou can try again from Settings, download from GitHub,"
                    + " or use Downloader code "
                    + DOWNLOADER_CODE
                    + ".")
            .setPositiveButton("GitHub / Downloader", (dialog, which) -> alternatives())
            .setNegativeButton("Close", null));
  }

  private void present(AlertDialog.Builder builder) {
    notice = builder.create();
    notice.setOnDismissListener(
        dialog -> {
          if (notice == dialog) notice = null;
        });
    notice.show();
  }

  private void openApp() {
    Intent intent = activity.getPackageManager().getLeanbackLaunchIntentForPackage(PACKAGE);
    if (intent == null) intent = activity.getPackageManager().getLaunchIntentForPackage(PACKAGE);
    try {
      if (intent == null) throw new IllegalStateException();
      activity.startActivity(intent);
    } catch (Exception error) {
      toast("Couldn't open Wrestling Chronology. You can reinstall it from Settings.");
    }
  }

  private void openDownloader() {
    String downloader = "com.esaba.downloader";
    Intent intent = activity.getPackageManager().getLeanbackLaunchIntentForPackage(downloader);
    if (intent == null) intent = activity.getPackageManager().getLaunchIntentForPackage(downloader);
    try {
      if (intent == null) throw new IllegalStateException();
      activity.startActivity(intent);
      toast("Enter " + DOWNLOADER_CODE + " in Downloader.");
    } catch (Exception error) {
      toast("Install Downloader from your app store, then enter " + DOWNLOADER_CODE + ".");
    }
  }

  private void toast(String value) {
    if (!destroyed) Toast.makeText(activity, value, Toast.LENGTH_LONG).show();
  }

  void onResume() {
    resumed = true;
    installer.onResume();
  }

  void onPause() {
    resumed = false;
    installer.onPause();
  }

  void destroy() {
    destroyed = true;
    installer.destroy();
    if (notice != null) notice.dismiss();
  }
}
