package uk.local.wcwvault;

import android.content.Context;
import android.graphics.*;
import android.os.*;
import android.util.LruCache;
import android.widget.ImageView;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.concurrent.*;

final class Art {
  static final ExecutorService work = Executors.newFixedThreadPool(2);
  static final Handler main = new Handler(Looper.getMainLooper());
  static final LruCache<String, Bitmap> cache =
      new LruCache<String, Bitmap>(8 * 1024 * 1024) {
        protected int sizeOf(String key, Bitmap value) {
          return value.getAllocationByteCount();
        }
      };

  static void load(ImageView view, String url) {
    view.setTag(url);
    view.setImageDrawable(null);
    if (url == null || !url.startsWith("https://")) return;
    Bitmap ready = cache.get(url);
    if (ready != null) {
      view.setImageBitmap(ready);
      return;
    }
    Context context = view.getContext().getApplicationContext();
    work.execute(
        () -> {
          try {
            File folder = new File(context.getCacheDir(), "art");
            folder.mkdirs();
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(url.getBytes("UTF-8"));
            StringBuilder name = new StringBuilder();
            for (byte value : hash) name.append(String.format("%02x", value));
            File file = new File(folder, name.toString());
            if (!file.exists()) {
              File[] entries = folder.listFiles();
              long size = 0;
              if (entries != null) for (File entry : entries) size += entry.length();
              if (size > 40 * 1024 * 1024 && entries != null) {
                java.util.Arrays.sort(
                    entries, java.util.Comparator.comparingLong(File::lastModified));
                for (File entry : entries) {
                  if (size < 28 * 1024 * 1024) break;
                  long length = entry.length();
                  if (entry.delete()) size -= length;
                }
              }
              HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
              connection.setConnectTimeout(12000);
              connection.setReadTimeout(15000);
              try (InputStream input = connection.getInputStream();
                  FileOutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[8192];
                int count, total = 0;
                while ((count = input.read(buffer)) >= 0) {
                  total += count;
                  if (total > 5 * 1024 * 1024) throw new IOException("Artwork too large");
                  output.write(buffer, 0, count);
                }
              } finally {
                connection.disconnect();
              }
            }
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.toString(), bounds);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 1;
            while (bounds.outWidth / options.inSampleSize > 800) options.inSampleSize *= 2;
            Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options);
            if (bitmap == null) return;
            cache.put(url, bitmap);
            main.post(
                () -> {
                  if (url.equals(view.getTag())) view.setImageBitmap(bitmap);
                });
          } catch (Exception ignored) {
          }
        });
  }
}
