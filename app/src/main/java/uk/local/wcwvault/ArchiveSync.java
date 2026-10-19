package uk.local.wcwvault;

import android.content.Context;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

/** Refresh only the public Archive collections supplied by the user's add-on. */
final class ArchiveSync {
  static final int BATCH = 12;

  static String collection(String url) {
    try {
      String[] parts = new URI(url).getPath().split("/");
      return parts.length > 2 ? parts[2] : "";
    } catch (Exception ignored) {
      return "";
    }
  }

  static String family(String title) {
    String value = title.toLowerCase(Locale.UK);
    if (value.contains("nitro")) return "Nitro";
    if (value.contains("thunder")) return "Thunder";
    if (value.contains("clash of")) return "Clash of the Champions";
    if (value.contains("saturday night")) return "Saturday Night";
    if (value.matches(".*world[ ._-]*wide.*")) return "Worldwide";
    if (value.matches(".*\\b(?:wcw pro|pro wrestling)\\b.*")) return "Pro";
    return null;
  }

  static String fileDate(String name, JSONObject data) throws Exception {
    String clean = name.replace('_', ' ');
    Matcher iso =
        Pattern.compile("\\b(19\\d{2}|20\\d{2})[ .-](\\d{2})[ .-](\\d{2})\\b").matcher(clean);
    if (iso.find())
      return CatalogSync.validDate(
          Integer.parseInt(iso.group(1)),
          Integer.parseInt(iso.group(2)),
          Integer.parseInt(iso.group(3)));
    Matcher numeric =
        Pattern.compile("\\b(\\d{2})[ .-](\\d{2})[ .-](19\\d{2}|20\\d{2})\\b").matcher(clean);
    if (numeric.find()) {
      int a = Integer.parseInt(numeric.group(1)),
          b = Integer.parseInt(numeric.group(2)),
          year = Integer.parseInt(numeric.group(3));
      if (a > 12) return CatalogSync.validDate(year, b, a);
      if (b > 12) return CatalogSync.validDate(year, a, b);
      // Ambiguous dates are deliberately left unmapped.
      return null;
    }
    return CatalogSync.date(clean, data);
  }

  static int merge(JSONObject data, String identifier, JSONObject item) throws Exception {
    JSONObject metadata = item.optJSONObject("metadata");
    if (item.optBoolean("is_dark") || metadata == null || metadata.optBoolean("access-restricted"))
      return 0;
    JSONArray rows = data.getJSONArray("episodes"), files = item.optJSONArray("files");
    if (files == null) return 0;
    ArrayList<String> signatures = new ArrayList<>();
    for (int i = 0; i < files.length(); i++) {
      JSONObject file = files.getJSONObject(i);
      if (file.optString("name").matches("(?i).+\\.(?:mp4|m4v)$"))
        signatures.add(
            file.optString("name")
                + ":"
                + file.optString("size")
                + ":"
                + file.optString("length")
                + ":"
                + file.optString("height")
                + ":"
                + file.optString("private")
                + ":"
                + file.optString("original"));
    }
    Collections.sort(signatures);
    String fingerprint = MetadataCache.hash("parser-v2:" + String.join("\n", signatures));
    JSONObject hashes = data.optJSONObject("archiveHashes");
    if (hashes == null) {
      hashes = new JSONObject();
      data.put("archiveHashes", hashes);
    }
    if (fingerprint.equals(hashes.optString(identifier))) return 0;
    Set<String> families = new HashSet<>();
    Map<String, JSONObject> knownFiles = new HashMap<>();
    Set<String> urls = new HashSet<>();
    for (int i = 0; i < rows.length(); i++) {
      JSONObject row = rows.getJSONObject(i);
      JSONArray sources = row.getJSONArray("sources");
      for (int j = 0; j < sources.length(); j++) {
        JSONObject source = sources.getJSONObject(j);
        if (!source.optString("type").equals("archive")) continue;
        String url = source.optString("url");
        if (url.isEmpty()) continue;
        urls.add(url);
        if (collection(url).equals(identifier)) {
          families.add(row.getString("show"));
          knownFiles.put(
              new URI(url).getPath().substring(("/download/" + identifier + "/").length()), row);
        }
      }
    }
    JSONObject hints = data.optJSONObject("archiveHints");
    String inherited =
        hints != null && hints.has(identifier)
            ? hints.getString(identifier)
            : families.size() == 1
                ? families.iterator().next()
                : family(metadata.optString("title"));
    int added = 0;
    for (int i = 0; i < files.length(); i++) {
      JSONObject file = files.getJSONObject(i);
      String name = file.optString("name");
      if (!name.matches("(?i).+\\.(?:mp4|m4v)$")
          || file.optBoolean("private")
          || name.contains("../")
          || Pattern.compile(
                  "(?i)\\b(?:highlights?|trailer|preview|sample|compilation|clips?|full["
                      + " ._-]+match|part[ ._-]*\\d+)\\b")
              .matcher(name.replace('_', ' '))
              .find()) continue;
      if (Pattern.compile("(?i)\\b(?:wwf|wwe|ecw|aew|njpw|roh)\\b")
          .matcher(name.replace('_', ' '))
          .find()) continue;
      double duration = file.optDouble("length", 0);
      if (duration < 2700) continue;
      String url =
          "https://archive.org/download/"
              + identifier
              + "/"
              + URLEncoder.encode(name, "UTF-8").replace("+", "%20").replace("%2F", "/");
      if (urls.contains(url)) continue;
      String kind = family(name.replace('_', ' ')), when = fileDate(name, data);
      JSONObject selected = knownFiles.get(name);
      if (selected == null && !file.optString("original").isEmpty())
        selected = knownFiles.get(file.optString("original"));
      if (selected == null) {
        if (kind == null) kind = inherited;
        if (kind == null && when != null) {
          JSONArray calendar = data.getJSONArray("eventCalendar");
          for (int j = 0; j < calendar.length(); j++) {
            JSONObject event = calendar.getJSONObject(j);
            String key = CatalogSync.key(event.optString("name"));
            if (when.equals(event.optString("date"))
                && event.optString("category").equals("ppv")
                && key.length() > 3
                && CatalogSync.key(name).contains(key)) {
              kind = "PPV";
              break;
            }
          }
        }
        if (kind == null || when == null) continue;
        for (int j = 0; j < rows.length(); j++) {
          JSONObject row = rows.getJSONObject(j);
          if (when.equals(row.optString("date")) && kind.equals(row.optString("show"))) {
            selected = row;
            break;
          }
        }
        if (selected == null) {
          String title = kind, poster = "", description = "";
          if (kind.equals("PPV")) {
            title = null;
            JSONArray calendar = data.getJSONArray("eventCalendar");
            for (int j = 0; j < calendar.length(); j++) {
              JSONObject event = calendar.getJSONObject(j);
              if (when.equals(event.optString("date"))
                  && event.optString("category").equals("ppv")) {
                title = event.getString("name");
                poster = event.optString("poster");
                description = event.optString("description");
                break;
              }
            }
            if (title == null) continue;
          }
          selected =
              new JSONObject()
                  .put("id", when + ":" + CatalogSync.key(title))
                  .put("date", when)
                  .put("year", Integer.parseInt(when.substring(0, 4)))
                  .put("show", kind)
                  .put("title", title)
                  .put("poster", poster)
                  .put("description", description)
                  .put("duration", (int) duration)
                  .put("sources", new JSONArray())
                  .put("discoveredAt", System.currentTimeMillis());
          JSONObject art = data.optJSONObject("showArtwork");
          if (art != null && art.optJSONObject(kind) != null)
            selected.put("thumbnail", art.getJSONObject(kind).optString("background"));
          rows.put(selected);
        }
      }
      int height = file.optInt("height");
      selected
          .getJSONArray("sources")
          .put(
              new JSONObject()
                  .put("type", "archive")
                  .put("url", url)
                  .put("filename", name)
                  .put("bytes", file.optLong("size"))
                  .put("label", "Internet Archive" + (height > 0 ? " " + height + "p" : "")));
      urls.add(url);
      added++;
    }
    hashes.put(identifier, fingerprint);
    return added;
  }

  static int refresh(Context context, JSONObject data, int limit) throws Exception {
    JSONArray identifiers = data.getJSONArray("archiveItems");
    int cursor = Catalog.prefs(context).getInt("archive_cursor", 0),
        added = 0,
        count = Math.min(limit, identifiers.length());
    for (int i = 0; i < count; i++) {
      CatalogSync.available();
      String identifier = identifiers.getString((cursor + i) % identifiers.length());
      CatalogSync.progress(
          data.getJSONArray("playlists").length() + i,
          data.getJSONArray("playlists").length() + count,
          "Archive.org · " + identifier + " · " + (i + 1) + " / " + count);
      if (!identifier.matches("[A-Za-z0-9][A-Za-z0-9_.-]{0,99}")) continue;
      try {
        int found =
            merge(
                data,
                identifier,
                new JSONObject(
                    MetadataCache.get(context, "https://archive.org/metadata/" + identifier)));
        added += found;
        CatalogSync.checked("Archive.org · " + identifier, found, false);
      } catch (Exception error) {
        CatalogSync.warning("Archive.org · " + identifier + " · " + CatalogSync.reason(error));
      }
    }
    data.put("archiveCursor", (cursor + count) % Math.max(1, identifiers.length()));
    return added;
  }
}
