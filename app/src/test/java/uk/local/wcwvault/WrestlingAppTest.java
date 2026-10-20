package uk.local.wcwvault;

import static org.junit.Assert.*;

import java.io.IOException;
import org.json.*;
import org.junit.Test;

public class WrestlingAppTest {
  @Test
  public void startupNoticeOnlyOffersAnUninstalledAppWhenNotHidden() {
    assertTrue(WrestlingApp.shouldOffer(true, false, false));
    assertFalse(WrestlingApp.shouldOffer(true, true, false));
    assertFalse(WrestlingApp.shouldOffer(true, false, true));
    assertFalse(WrestlingApp.shouldOffer(false, false, false));
  }

  private JSONObject github(String asset, String digest) throws Exception {
    return new JSONObject()
        .put("tag_name", "v0.1.0")
        .put(
            "assets",
            new JSONArray()
                .put(
                    new JSONObject()
                        .put("name", asset)
                        .put("digest", digest)
                        .put(
                            "browser_download_url",
                            "https://github.com/deklol/Wrestling-Chronology/releases/download/v0.1.0/"
                                + asset)));
  }

  @Test
  public void companionDownloadSelectsItsOwnVerifiedReleaseAsset() throws Exception {
    UpdateRelease release =
        UpdateRelease.github(
            github("Wrestling-Chronology.apk", "sha256:" + "a".repeat(64)), WrestlingApp.APK_NAME);
    assertEquals("0.1.0", release.version);
    assertTrue(release.url.endsWith("/Wrestling-Chronology.apk"));
    assertEquals("a".repeat(64), release.hash);
  }

  @Test
  public void companionReleaseCannotBecomeAWcwSelfUpdate() throws Exception {
    JSONObject release = github("Wrestling-Chronology.apk", "sha256:" + "a".repeat(64));
    assertThrows(IOException.class, () -> UpdateRelease.github(release));
    JSONObject wcw = github("WCW-FireTV-App.apk", "sha256:" + "a".repeat(64));
    assertThrows(IOException.class, () -> UpdateRelease.github(wcw, WrestlingApp.APK_NAME));
  }

  @Test
  public void companionReleaseWithoutAValidHashIsRejected() throws Exception {
    assertThrows(
        IOException.class,
        () -> UpdateRelease.github(github("Wrestling-Chronology.apk", ""), WrestlingApp.APK_NAME));
    assertThrows(
        IOException.class,
        () ->
            UpdateRelease.github(
                github("Wrestling-Chronology.apk", "sha256:bad"), WrestlingApp.APK_NAME));
  }
}
