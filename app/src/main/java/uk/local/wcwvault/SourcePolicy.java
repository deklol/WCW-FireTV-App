package uk.local.wcwvault;

import java.util.regex.Pattern;
import org.json.JSONObject;

/** Full-show eligibility is checked before resolution or file-size preferences. */
final class SourcePolicy {
  private static final Pattern EXTRA = Pattern.compile(
      "(?i)\\b(?:countdown|pre[ ._-]*shows?|preshows?|highlights?|trailer|preview|sample|"
          + "compilation|clips?|recap|review|reliving[ ._-]+the[ ._-]+war|"
          + "full[ ._-]+match)\\b");

  static boolean eligible(JSONObject source) {
    if (!"archive".equals(source.optString("type"))) return true;
    String name = source.optString("filename") + " " + source.optString("url");
    if (EXTRA.matcher(name.replace("%20", " ").replace('_', ' ')).find()) return false;
    // Multipart recordings must be judged by their combined runtime, not one segment.
    if (source.optJSONArray("urls") != null) return true;
    double duration = source.optDouble("duration", 0);
    return duration <= 0 || duration >= 2700;
  }

  static int preference(JSONObject source) {
    return "archive".equals(source.optString("type"))
            && "Chasing That W".equalsIgnoreCase(source.optString("creator"))
            && source.optDouble("duration", 0) >= 5400
        ? 0 : 1;
  }
}
