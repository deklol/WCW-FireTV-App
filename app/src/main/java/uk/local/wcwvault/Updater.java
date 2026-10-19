package uk.local.wcwvault;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.*;
import org.json.JSONObject;

final class Updater {
  static void check(Activity activity) {
    String feed = Catalog.prefs(activity).getString("update_feed", "");
    if (feed.isEmpty()) {
      new AlertDialog.Builder(activity)
          .setTitle("App updates")
          .setMessage(
              "This is version "
                  + BuildConfig.VERSION_NAME
                  + ". An online release feed has not been connected yet. Your catalogue refreshes"
                  + " separately.")
          .setPositiveButton("OK", null)
          .show();
      return;
    }
    if (!feed.startsWith("https://")) {
      Toast.makeText(activity, "The update feed must use HTTPS", Toast.LENGTH_LONG).show();
      return;
    }
    Toast.makeText(activity, "Checking for app updates…", Toast.LENGTH_SHORT).show();
    new Thread(
            () -> {
              try {
                JSONObject release = new JSONObject(CatalogSync.get(feed));
                int code = release.getInt("versionCode");
                if (code <= BuildConfig.VERSION_CODE) {
                  activity.runOnUiThread(
                      () ->
                          Toast.makeText(activity, "You're up to date", Toast.LENGTH_LONG).show());
                  return;
                }
                String url = release.getString("apkUrl"), hash = release.getString("sha256");
                if (!url.startsWith("https://") || !hash.matches("[a-fA-F0-9]{64}"))
                  throw new IOException("Invalid update manifest");
                activity.runOnUiThread(
                    () ->
                        new AlertDialog.Builder(activity)
                            .setTitle(
                                "Version "
                                    + release.optString("versionName", Integer.toString(code))
                                    + " is available")
                            .setMessage(
                                release.optString(
                                    "notes",
                                    "Update WCW Chronology. Your viewing progress will be kept."))
                            .setPositiveButton(
                                "Download update",
                                (dialog, which) -> download(activity, url, hash, code))
                            .setNegativeButton("Later", null)
                            .show());
              } catch (Exception error) {
                activity.runOnUiThread(
                    () ->
                        Toast.makeText(
                                activity,
                                "Couldn't check for updates. Please try again later.",
                                Toast.LENGTH_LONG)
                            .show());
              }
            },
            "WCW-UpdateCheck")
        .start();
  }

  static void download(Activity activity, String url, String expected, int version) {
    Toast.makeText(activity, "Downloading the app update…", Toast.LENGTH_LONG).show();
    new Thread(
            () -> {
              HttpURLConnection connection = null;
              try {
                File apk = new File(activity.getCacheDir(), "update.apk");
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(25000);
                if (connection.getResponseCode() != 200)
                  throw new IOException("Update host unavailable");
                try (InputStream input = connection.getInputStream();
                    FileOutputStream output = new FileOutputStream(apk)) {
                  byte[] buffer = new byte[32768];
                  int count;
                  long total = 0;
                  while ((count = input.read(buffer)) >= 0) {
                    total += count;
                    if (total > 80 * 1024 * 1024) throw new IOException("Update too large");
                    digest.update(buffer, 0, count);
                    output.write(buffer, 0, count);
                  }
                }
                StringBuilder hash = new StringBuilder();
                for (byte value : digest.digest()) hash.append(String.format("%02x", value));
                if (!hash.toString().equalsIgnoreCase(expected))
                  throw new IOException("Update checksum mismatch");
                PackageManager manager = activity.getPackageManager();
                int signingFlags =
                    Build.VERSION.SDK_INT >= 28
                        ? PackageManager.GET_SIGNING_CERTIFICATES
                        : PackageManager.GET_SIGNATURES;
                PackageInfo installed =
                    manager.getPackageInfo(activity.getPackageName(), signingFlags);
                PackageInfo archive = manager.getPackageArchiveInfo(apk.toString(), signingFlags);
                if (archive == null
                    || !activity.getPackageName().equals(archive.packageName)
                    || (Build.VERSION.SDK_INT >= 28
                            ? archive.getLongVersionCode()
                            : archive.versionCode)
                        != version
                    || !sameSigners(installed, archive))
                  throw new IOException("Update package or signing certificate mismatch");
                activity.runOnUiThread(
                    () -> {
                      try {
                        Intent install =
                            new Intent(Intent.ACTION_INSTALL_PACKAGE)
                                .setData(
                                    Uri.parse("content://uk.local.wcwvault.updates/package.apk"))
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        activity.startActivity(install);
                      } catch (Exception error) {
                        Toast.makeText(
                                activity,
                                "Fire TV couldn't open its package installer",
                                Toast.LENGTH_LONG)
                            .show();
                      }
                    });
              } catch (Exception error) {
                activity.runOnUiThread(
                    () ->
                        Toast.makeText(
                                activity,
                                "Update could not be verified. Your installed app was kept.",
                                Toast.LENGTH_LONG)
                            .show());
              } finally {
                if (connection != null) connection.disconnect();
              }
            },
            "WCW-UpdateDownload")
        .start();
  }

  @SuppressWarnings("deprecation")
  static boolean sameSigners(PackageInfo installed, PackageInfo archive) {
    Signature[] current, update;
    if (Build.VERSION.SDK_INT >= 28) {
      if (installed.signingInfo == null || archive.signingInfo == null) return false;
      current = installed.signingInfo.getApkContentsSigners();
      update = archive.signingInfo.getApkContentsSigners();
    } else {
      current = installed.signatures;
      update = archive.signatures;
    }
    if (current == null || update == null || current.length == 0 || current.length != update.length)
      return false;
    Set<Signature> currentSet = new HashSet<>(Arrays.asList(current));
    return currentSet.equals(new HashSet<>(Arrays.asList(update)));
  }
}
