package uk.local.wcwvault;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

/** Debug-only device host for the real notice, persisted opt-out and live APK installer. */
public final class WrestlingAppTestActivity extends Activity {
  WrestlingApp wrestlingApp;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    wrestlingApp = new WrestlingApp(this);
    LinearLayout root = Ui.vertical(this);
    root.setPadding(40, 40, 40, 40);
    root.setBackgroundColor(Ui.BG);
    root.addView(Ui.text(this, "Wrestling Chronology installation test", 24, Ui.WHITE));
    root.addView(Ui.button(this, "Show launch notice", false, () -> wrestlingApp.show(true)));
    root.addView(
        Ui.button(
            this,
            "Download Wrestling Chronology",
            true,
            () -> wrestlingApp.installer.check(false)));
    root.addView(Ui.button(this, "Settings download entry", false, () -> wrestlingApp.show(false)));
    root.addView(
        Ui.button(
            this,
            "Reset test opt-out",
            false,
            () -> wrestlingApp.preferences().edit().remove(WrestlingApp.HIDDEN).apply()));
    setContentView(root);
  }

  @Override
  protected void onResume() {
    super.onResume();
    wrestlingApp.onResume();
  }

  @Override
  protected void onPause() {
    wrestlingApp.onPause();
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    wrestlingApp.destroy();
    super.onDestroy();
  }
}
