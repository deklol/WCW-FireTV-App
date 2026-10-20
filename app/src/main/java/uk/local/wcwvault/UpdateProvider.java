package uk.local.wcwvault;

import android.content.*;
import android.database.*;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.*;

public final class UpdateProvider extends ContentProvider {
  @Override
  public boolean onCreate() {
    return true;
  }

  File file(Uri uri) throws FileNotFoundException {
    if ("/package.apk".equals(uri.getPath()))
      return new File(getContext().getCacheDir(), "update.apk");
    if ("/wrestling-chronology.apk".equals(uri.getPath()))
      return new File(getContext().getCacheDir(), "wrestling-chronology.apk");
    throw new FileNotFoundException();
  }

  @Override
  public String getType(Uri uri) {
    return "application/vnd.android.package-archive";
  }

  @Override
  public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
    if (!"r".equals(mode)) throw new FileNotFoundException("Read only");
    return ParcelFileDescriptor.open(file(uri), ParcelFileDescriptor.MODE_READ_ONLY);
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] args, String order) {
    try {
      File file = file(uri);
      MatrixCursor result =
          new MatrixCursor(new String[] {OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE});
      result.addRow(
          new Object[] {
            "/wrestling-chronology.apk".equals(uri.getPath())
                ? "Wrestling-Chronology.apk"
                : "wcw-chronology-update.apk",
            file.length()
          });
      return result;
    } catch (Exception error) {
      return null;
    }
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int delete(Uri uri, String selection, String[] args) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] args) {
    throw new UnsupportedOperationException();
  }
}
