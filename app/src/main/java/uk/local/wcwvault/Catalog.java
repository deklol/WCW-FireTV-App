package uk.local.wcwvault;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.*;

final class Catalog {
  static volatile List<Episode> episodes = Collections.emptyList();
  static JSONObject raw;

  static synchronized void load(Context context) throws Exception {
    if (!episodes.isEmpty()) return;
    File cache = new File(context.getFilesDir(), "catalogue.json");
    try {
      raw = cache.exists() ? new JSONObject(read(new FileInputStream(cache))) : null;
    } catch (Exception ignored) {
      raw = null;
    }
    JSONObject seed = new JSONObject(read(context.getAssets().open("catalogue.json")));
    if (raw == null || !raw.has("episodes")) raw = seed;
    else mergeSeed(raw, seed);
    if (raw.optInt("sourceValidation") < 4) {
      normalizeYoutube(raw);
      raw.put("sourceValidation", 4);
    }
    SourceConfig.load(context, raw);
    replace(raw);
  }

  static void normalizeYoutube(JSONObject data) throws Exception {
    JSONArray rows = data.getJSONArray("episodes");
    for (int i = rows.length() - 1; i >= 0; i--) {
      JSONObject row = rows.getJSONObject(i);
      JSONArray sources = row.getJSONArray("sources");
      for (int j = sources.length() - 1; j >= 0; j--) {
        JSONObject source = sources.getJSONObject(j);
        if (!source.optString("type").equals("youtube")) continue;
        String date = CatalogSync.date(source.optString("title"), data),
            show = CatalogSync.show(source.optString("title"));
        if (row.optString("date").equals(date) && row.optString("show").equals(show)) continue;
        if (date != null)
          for (int k = 0; k < rows.length(); k++) {
            JSONObject target = rows.getJSONObject(k);
            if (target.optString("date").equals(date) && target.optString("show").equals(show)) {
              JSONArray options = target.getJSONArray("sources");
              boolean exists = false;
              for (int n = 0; n < options.length(); n++)
                exists |= sourceKey(options.getJSONObject(n)).equals(sourceKey(source));
              if (!exists) options.put(source);
              break;
            }
          }
        sources.remove(j);
      }
      if (sources.length() == 0) rows.remove(i);
    }
  }

  static void mergeSeed(JSONObject cache, JSONObject seed) throws JSONException {
    JSONArray rows = cache.getJSONArray("episodes"), bundled = seed.getJSONArray("episodes");
    Map<String, JSONObject> known = new HashMap<>();
    for (int i = 0; i < rows.length(); i++) {
      JSONObject row = rows.getJSONObject(i);
      known.put(row.getString("date") + ":" + row.getString("show"), row);
    }
    for (int i = 0; i < bundled.length(); i++) {
      JSONObject row = bundled.getJSONObject(i),
          existing = known.get(row.getString("date") + ":" + row.getString("show"));
      if (existing == null) {
        rows.put(row);
        continue;
      }
      for (String key : new String[] {"poster", "description", "title"})
        if (!row.optString(key).isEmpty()) existing.put(key, row.get(key));
      JSONArray sources = existing.getJSONArray("sources"),
          newSources = row.getJSONArray("sources");
      Set<String> keys = new HashSet<>();
      for (int j = 0; j < sources.length(); j++) keys.add(sourceKey(sources.getJSONObject(j)));
      for (int j = 0; j < newSources.length(); j++)
        if (keys.add(sourceKey(newSources.getJSONObject(j))))
          sources.put(newSources.getJSONObject(j));
    }
    cache.put("eventCalendar", seed.getJSONArray("eventCalendar"));
    cache.put("playlists", seed.getJSONArray("playlists"));
    cache.put("archiveItems", seed.getJSONArray("archiveItems"));
  }

  static String sourceKey(JSONObject source) {
    return source.optString("type")
        + ":"
        + source.optString("videoId", source.optString("url", source.optString("urls")));
  }

  static ArrayList<Episode> recent(String show) {
    ArrayList<Episode> rows = new ArrayList<>();
    for (Episode item : episodes) if (item.show.equals(show)) rows.add(item);
    rows.sort(
        Comparator.comparingLong((Episode item) -> item.addedAt)
            .reversed()
            .thenComparing((Episode item) -> item.date, Comparator.reverseOrder()));
    if (rows.size() > 20) rows.subList(20, rows.size()).clear();
    return rows;
  }

  static synchronized void replace(JSONObject data) throws Exception {
    ArrayList<Episode> next = new ArrayList<>();
    JSONArray rows = data.getJSONArray("episodes");
    for (int i = 0; i < rows.length(); i++) {
      Episode item = new Episode(rows.getJSONObject(i));
      if (item.date.compareTo("1997-01-01") >= 0
          && item.date.compareTo("2001-03-26") <= 0
          && !item.sources.isEmpty()) next.add(item);
    }
    next.sort(
        Comparator.comparing((Episode item) -> item.date)
            .thenComparingInt(item -> showOrder(item.show))
            .thenComparing(item -> item.title));
    episodes = Collections.unmodifiableList(next);
    raw = data;
  }

  static int showOrder(String show) {
    return Arrays.asList(
            "Saturday Night",
            "Worldwide",
            "Pro",
            "Nitro",
            "Thunder",
            "Clash of the Champions",
            "PPV")
        .indexOf(show);
  }

  static synchronized ArrayList<Episode> forYear(int year, String filter, boolean extras) {
    ArrayList<Episode> result = new ArrayList<>();
    for (Episode item : episodes)
      if (item.year == year
          && (extras || item.main())
          && (filter.equals("All") || filter.equals(item.show))) result.add(item);
    return result;
  }

  static synchronized Episode find(String id) {
    for (Episode item : episodes) if (item.id.equals(id)) return item;
    return null;
  }

  static String read(InputStream stream) throws IOException {
    try (InputStream input = stream;
        ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[16384];
      int count;
      while ((count = input.read(buffer)) >= 0) {
        output.write(buffer, 0, count);
        if (output.size() > 18 * 1024 * 1024) throw new IOException("Response too large");
      }
      return output.toString(StandardCharsets.UTF_8.name());
    }
  }

  static SharedPreferences prefs(Context context) {
    return context.getSharedPreferences("wcw", Context.MODE_PRIVATE);
  }

  static long position(Context context, Episode item) {
    return prefs(context).getLong("pos:" + item.id, 0);
  }

  static boolean watched(Context context, Episode item) {
    return prefs(context).getBoolean("watched:" + item.id, false);
  }

  static long displayPosition(Context context, Episode item) {
    return prefs(context)
        .getLong(
            "seen:" + item.id,
            prefs(context).getInt("part:" + item.id, 0) == 0 ? position(context, item) : 0);
  }

  static long displayDuration(Context context, Episode item) {
    return prefs(context).getLong("length:" + item.id, item.duration * 1000L);
  }

  static int progressPercent(long position, long duration, boolean watched) {
    if (watched) return 100;
    if (duration <= 0 || position <= 0) return 0;
    return (int) Math.min(99, Math.max(1, 100d * position / duration));
  }

  static void progress(Context context, Episode item, long position, boolean watched) {
    prefs(context)
        .edit()
        .putLong("pos:" + item.id, watched ? 0 : Math.max(0, position))
        .putBoolean("watched:" + item.id, watched)
        .putString("last:" + item.year, item.id)
        .putString("last", item.id)
        .apply();
  }

  static String dateLabel(String iso) {
    try {
      return new java.text.SimpleDateFormat("d MMM yyyy", Locale.UK)
          .format(new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.UK).parse(iso));
    } catch (Exception ignored) {
      return iso;
    }
  }

  static final class Episode {
    final JSONObject json;
    final String id, date, title, show, description, thumbnail, poster;
    final long addedAt;
    final int year, duration;
    final ArrayList<JSONObject> sources = new ArrayList<>();

    Episode(JSONObject data) throws JSONException {
      json = data;
      id = data.getString("id");
      date = data.getString("date");
      year = data.getInt("year");
      title = data.getString("title");
      String category = data.getString("show");
      show = category.equals("Clash of the Champions") ? "PPV" : category;
      description = data.optString("description");
      thumbnail = data.optString("thumbnail");
      poster = data.optString("poster");
      addedAt = data.optLong("discoveredAt");
      duration = data.optInt("duration");
      JSONArray list = data.optJSONArray("sources");
      if (list != null) for (int i = 0; i < list.length(); i++) sources.add(list.getJSONObject(i));
    }

    boolean main() {
      return show.equals("Nitro")
          || show.equals("Thunder")
          || show.equals("PPV")
          || show.equals("Clash of the Champions");
    }

    String sourceLabel() {
      boolean archive = false, youtube = false;
      for (JSONObject source : sources) {
        archive |= source.optString("type").equals("archive");
        youtube |= source.optString("type").equals("youtube");
      }
      return archive && youtube
          ? "Archive + YouTube"
          : youtube ? "Official WCW · YouTube" : "Internet Archive";
    }

    String heading() {
      return show.equals("PPV") ? title : show;
    }
  }
}
