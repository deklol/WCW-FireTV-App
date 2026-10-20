package uk.local.wcwvault;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

/** Debug-only host: exercises the production updater against an installed, signed WCW APK. */
public final class UpdaterTestActivity extends Activity {
  Updater updater;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    updater = new Updater(this, "uk.local.wcwvault", 5, "0.2.3");
    LinearLayout root = Ui.vertical(this);
    root.setPadding(40, 40, 40, 40);
    root.setBackgroundColor(Ui.BG);
    root.addView(Ui.text(this, "Updater integration test — live GitHub release", 24, Ui.WHITE));
    root.addView(Ui.button(this, "Update app", true, () -> updater.check(false)));
    setContentView(root);
    updater.startup();
  }

  @Override
  protected void onResume() {
    super.onResume();
    updater.onResume();
  }

  @Override
  protected void onPause() {
    updater.onPause();
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    updater.destroy();
    super.onDestroy();
  }
}
