package uk.local.wcwvault;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import java.util.*;
import java.util.concurrent.*;

public final class MainActivity extends Activity {
  final ExecutorService work = Executors.newSingleThreadExecutor();
  LinearLayout root;
  int year = 1997;
  String page = "home", filter = "All", query = "";
  boolean ready;
  final Handler uiHandler = new Handler(Looper.getMainLooper());
  LinearLayout refreshStrip;
  ProgressBar refreshBar;
  TextView refreshText;
  boolean refreshWasBusy;
  final Runnable libraryTick =
      new Runnable() {
        public void run() {
          if (ready) {
            updateRefresh();
            if (CatalogSync.busy) refreshWasBusy = true;
            else if (refreshWasBusy) {
              refreshWasBusy = false;
              render();
              notifyFound(false);
            }
            checkRefresh();
          }
          uiHandler.postDelayed(this, 1000);
        }
      };

  int d(float value) {
    return Ui.dp(this, value);
  }

  TextView text(String value, float size, int color) {
    return Ui.text(this, value, size, color);
  }

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    immersive();
    if (state != null) {
      year = state.getInt("year", 1997);
      page = state.getString("page", "home");
      filter = state.getString("filter", "All");
      query = state.getString("query", "");
    }
    TextView loading = text("Opening your WCW library…", 22, Ui.WHITE);
    loading.setGravity(Gravity.CENTER);
    setContentView(loading);
    work.execute(
        () -> {
          try {
            Catalog.load(this);
            runOnUiThread(
                () -> {
                  ready = true;
                  render();
                  boolean direct = getIntent().hasExtra("play_id");
                  handlePlayIntent();
                  if (!direct) splash();
                  LibraryJob.schedule(this);
                  checkRefresh();
                  notifyFound(false);
                });
          } catch (Exception error) {
            runOnUiThread(
                () ->
                    new AlertDialog.Builder(this)
                        .setMessage("Could not open the catalogue: " + error.getMessage())
                        .setPositiveButton("Close", (dialog, which) -> finish())
                        .show());
          }
        });
  }

  void immersive() {
    getWindow().getDecorView().setSystemUiVisibility(5894);
  }

  @Override
  protected void onResume() {
    super.onResume();
    immersive();
    if (ready) {
      render();
      notifyFound(false);
    }
    uiHandler.removeCallbacks(libraryTick);
    uiHandler.post(libraryTick);
  }

  @Override
  protected void onPause() {
    uiHandler.removeCallbacks(libraryTick);
    super.onPause();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    if (ready) handlePlayIntent();
  }

  @Override
  protected void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);
    state.putInt("year", year);
    state.putString("page", page);
    state.putString("filter", filter);
    state.putString("query", query);
  }

  void handlePlayIntent() {
    if (BuildConfig.DEBUG && getIntent().getBooleanExtra("verify_source_failure", false)) {
      getIntent().removeExtra("verify_source_failure");
      CatalogSync.verifyFailNextPlaylist = true;
      refresh(false);
    }
    String id = getIntent().getStringExtra("play_id");
    if (id != null) {
      getIntent().removeExtra("play_id");
      Catalog.Episode item = Catalog.find(id);
      if (item != null)
        launch(
            item,
            Catalog.forYear(item.year, "All", extras()),
            getIntent().getStringExtra("play_provider"));
    }
  }

  void splash() {
    FrameLayout host = findViewById(android.R.id.content);
    FrameLayout overlay = new FrameLayout(this);
    overlay.setBackgroundColor(Color.BLACK);
    overlay.setClickable(true);
    overlay.setFocusable(true);
    ImageView image = new ImageView(this);
    image.setImageResource(R.drawable.wcw_opening);
    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
    image.setAlpha(0f);
    overlay.addView(image, new FrameLayout.LayoutParams(-1, -1));
    host.addView(overlay, new FrameLayout.LayoutParams(-1, -1));
    Handler handler = new Handler(Looper.getMainLooper());
    long start = SystemClock.uptimeMillis();
    handler.post(
        new Runnable() {
          public void run() {
            if (isFinishing() || isDestroyed()) {
              host.removeView(overlay);
              image.setImageDrawable(null);
              return;
            }
            long elapsed = SystemClock.uptimeMillis() - start;
            if (elapsed < 550) image.setAlpha(elapsed / 550f);
            else if (elapsed < 1200) image.setAlpha(1f);
            else if (elapsed < 1650) overlay.setAlpha(1 - (elapsed - 1200) / 450f);
            else {
              host.removeView(overlay);
              image.setImageDrawable(null);
              return;
            }
            handler.postDelayed(this, 16);
          }
        });
  }

  void render() {
    root = Ui.vertical(this);
    root.setPadding(d(30), d(18), d(30), d(16));
    root.setBackground(
        new GradientDrawable(
            GradientDrawable.Orientation.TL_BR, new int[] {Ui.BG, Color.rgb(19, 23, 35), Ui.BG}));
    setContentView(root);
    LinearLayout nav = Ui.horizontal(this);
    root.addView(nav, Ui.lp(this, -1, 48));
    ImageView logo = new ImageView(this);
    logo.setImageResource(R.drawable.wcw_logo);
    logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
    nav.addView(logo, Ui.lp(this, 62, 46));
    Ui.gap(nav, 12);
    TextView brand = text("CHRONOLOGY", 18, Ui.WHITE);
    brand.setLetterSpacing(.16f);
    brand.setTypeface(null, Typeface.BOLD);
    nav.addView(brand);
    Ui.weight(nav);
    nav.addView(
        Ui.button(
            this,
            "Home",
            page.equals("home"),
            () -> {
              page = "home";
              render();
            }));
    Ui.gap(nav, 10);
    for (String show : new String[] {"Nitro", "Thunder"}) {
      nav.addView(
          Ui.button(this, show, page.equals("show") && filter.equals(show), () -> openShow(show)));
      Ui.gap(nav, 10);
    }
    nav.addView(
        Ui.button(
            this,
            "PPV",
            page.equals("ppv"),
            () -> {
              page = "ppv";
              year = 0;
              render();
            }));
    Ui.gap(nav, 10);
    nav.addView(Ui.button(this, "Search", false, this::search));
    Ui.gap(nav, 10);
    nav.addView(Ui.button(this, "Settings", false, this::settings));
    refreshStrip = Ui.vertical(this);
    refreshStrip.setPadding(d(6), d(7), d(6), d(4));
    refreshText = text("", 11, Ui.MUTED);
    refreshStrip.addView(refreshText);
    refreshBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
    refreshBar.setProgressTintList(android.content.res.ColorStateList.valueOf(Ui.RED));
    refreshStrip.addView(refreshBar, Ui.lp(this, -1, 6));
    root.addView(refreshStrip);
    updateRefresh();
    Ui.gap(root, 14);
    if (page.equals("home")) home();
    else library();
  }

  boolean extras() {
    return Catalog.prefs(this).getBoolean("extras", false);
  }

  void home() {
    ScrollView pageScroll = new ScrollView(this);
    pageScroll.setClipToPadding(false);
    pageScroll.setFillViewport(true);
    root.addView(pageScroll, new LinearLayout.LayoutParams(-1, 0, 1));
    LinearLayout body = Ui.vertical(this);
    pageScroll.addView(body);
    Catalog.Episode current = Catalog.find(Catalog.prefs(this).getString("last", ""));
    Catalog.Episode featured = current;
    if (featured == null)
      for (Catalog.Episode item : Catalog.episodes)
        if (item.show.equals("Nitro")) {
          featured = item;
          break;
        }
    FrameLayout hero = new FrameLayout(this);
    body.addView(hero, Ui.lp(this, -1, 260));
    ImageView backdrop = new ImageView(this);
    backdrop.setScaleType(ImageView.ScaleType.CENTER);
    backdrop.setImageResource(R.drawable.wcw_opening);
    FrameLayout.LayoutParams photo = new FrameLayout.LayoutParams(d(580), -1, Gravity.RIGHT);
    hero.addView(backdrop, photo);
    View shade = new View(this);
    shade.setBackground(
        new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, new int[] {Ui.BG, 0xf50a0c12, 0x250a0c12}));
    hero.addView(shade, new FrameLayout.LayoutParams(-1, -1));
    LinearLayout copy = Ui.vertical(this);
    copy.setGravity(Gravity.CENTER_VERTICAL);
    copy.setPadding(d(10), d(12), 0, d(16));
    hero.addView(copy, new FrameLayout.LayoutParams(d(510), -1));
    TextView kicker = text("THE MONDAY NIGHT ERA", 11, Ui.RED);
    kicker.setLetterSpacing(.2f);
    kicker.setTypeface(null, Typeface.BOLD);
    copy.addView(kicker);
    Ui.gap(copy, 10);
    TextView title = text("Your WCW.\nIn order.", 44, Ui.WHITE);
    title.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
    title.setLineSpacing(0, .96f);
    copy.addView(title);
    Ui.gap(copy, 12);
    TextView description =
        text(
            "Nitro, Thunder and the pay-per-views.\n"
                + "One timeline. One player. Pick a year and let it roll.",
            14,
            Ui.MUTED);
    description.setLineSpacing(d(4), 1);
    copy.addView(description);
    Ui.gap(copy, 18);
    LinearLayout actions = Ui.horizontal(this);
    copy.addView(actions);
    if (current != null) {
      Catalog.Episode resume = current;
      actions.addView(
          Ui.button(
              this,
              "▶  Continue watching",
              true,
              () -> launch(resume, Catalog.forYear(resume.year, "All", extras()), null)));
    } else actions.addView(Ui.button(this, "Explore 1997  ›", true, () -> openYear(1997)));
    Ui.gap(actions, 10);
    actions.addView(Ui.button(this, "Refresh library", false, () -> refresh(false)));
    recentRail(body, "LATEST FOUND NITRO", "Nitro");
    recentRail(body, "LATEST FOUND THUNDER", "Thunder");
    recentRail(body, "LATEST FOUND PPVs", "PPV");
    Ui.gap(body, 12);
    TextView label = text("WATCH BY YEAR · NITRO, THUNDER + PPVs", 12, Ui.MUTED);
    label.setLetterSpacing(.1f);
    body.addView(label);
    Ui.gap(body, 10);
    HorizontalScrollView scroll = new HorizontalScrollView(this);
    scroll.setHorizontalScrollBarEnabled(false);
    scroll.setClipToPadding(false);
    body.addView(scroll, Ui.lp(this, -1, 138));
    LinearLayout years = Ui.horizontal(this);
    scroll.addView(years);
    TreeSet<Integer> values = new TreeSet<>();
    for (Catalog.Episode item : Catalog.episodes) values.add(item.year);
    for (int value : values) {
      ArrayList<Catalog.Episode> list = Catalog.forYear(value, "All", extras());
      if (list.isEmpty()) continue;
      LinearLayout card = Ui.vertical(this);
      card.setPadding(d(18), d(14), d(18), d(14));
      card.setGravity(Gravity.CENTER_VERTICAL);
      card.setBackground(Ui.focus(this, false));
      card.setFocusable(true);
      card.setClickable(true);
      card.setOnClickListener(view -> openYear(value));
      TextView number = text(Integer.toString(value), 31, Ui.WHITE);
      number.setTypeface(null, Typeface.BOLD);
      card.addView(number);
      Ui.gap(card, 5);
      card.addView(text(list.size() + " full shows  ›", 12, Ui.MUTED));
      Ui.gap(card, 5);
      long ppvCount = list.stream().filter(item -> item.show.equals("PPV")).count();
      long nitroCount = list.stream().filter(item -> item.show.equals("Nitro")).count();
      card.addView(text(nitroCount + " Nitro · " + ppvCount + " PPVs", 10, Ui.MUTED));
      LinearLayout.LayoutParams params = Ui.lp(this, 168, 128);
      params.rightMargin = d(12);
      years.addView(card, params);
    }
    Ui.gap(body, 10);
    body.addView(text("STREAMING ONLY   ·   Archive.org + official WCW on YouTube", 10, Ui.MUTED));
    Ui.gap(body, 22);
    TextView credit = text("x.com/digitalm1nd", 12, Ui.MUTED);
    credit.setGravity(Gravity.CENTER);
    body.addView(credit, Ui.lp(this, -1, 32));
  }

  void recentRail(LinearLayout body, String heading, String show) {
    ArrayList<Catalog.Episode> values = Catalog.recent(show);
    if (values.isEmpty()) return;
    Ui.gap(body, 20);
    LinearLayout row = Ui.horizontal(this);
    body.addView(row, Ui.lp(this, -1, 42));
    TextView label = text(heading, 14, Ui.WHITE);
    label.setTypeface(null, Typeface.BOLD);
    row.addView(label);
    Ui.gap(row, 12);
    row.addView(text("Newest discoveries first", 11, Ui.MUTED));
    Ui.weight(row);
    if (show.equals("PPV"))
      row.addView(
          Ui.button(
              this,
              "All PPVs  ›",
              false,
              () -> {
                page = "ppv";
                year = 0;
                render();
              }));
    RecyclerView rail = new RecyclerView(this);
    rail.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    rail.setAdapter(new EpisodeAdapter(values, true, show.equals("PPV")));
    rail.setNestedScrollingEnabled(false);
    rail.setClipToPadding(false);
    body.addView(rail, Ui.lp(this, -1, show.equals("PPV") ? 302 : 208));
  }

  void openYear(int value) {
    year = value;
    page = "year";
    filter = "All";
    render();
  }

  void openShow(String show) {
    filter = show;
    year = 0;
    page = "show";
    render();
  }

  ArrayList<Catalog.Episode> selected() {
    if (page.equals("ppv") || page.equals("show")) {
      ArrayList<Catalog.Episode> result = new ArrayList<>();
      for (Catalog.Episode item : Catalog.episodes)
        if (item.show.equals(page.equals("ppv") ? "PPV" : filter)
            && (year == 0 || item.year == year)) result.add(item);
      return result;
    }
    if (!page.equals("search")) return Catalog.forYear(year, filter, extras());
    ArrayList<Catalog.Episode> result = new ArrayList<>();
    String needle = query.toLowerCase(Locale.UK);
    for (Catalog.Episode item : Catalog.episodes)
      if ((extras() || item.main())
          && (item.title + " " + item.show + " " + item.date)
              .toLowerCase(Locale.UK)
              .contains(needle)) result.add(item);
    return result;
  }

  void library() {
    ArrayList<Catalog.Episode> items = selected();
    LinearLayout heading = Ui.horizontal(this);
    root.addView(heading, Ui.lp(this, -1, 56));
    TextView title =
        text(
            page.equals("search")
                ? "Search: " + query
                : page.equals("ppv")
                    ? "Pay-per-views"
                    : page.equals("show") ? filter : "WCW " + year,
            29,
            Ui.WHITE);
    title.setTypeface(null, Typeface.BOLD);
    heading.addView(title);
    Ui.gap(heading, 18);
    heading.addView(text(items.size() + " full shows · in broadcast order", 12, Ui.MUTED));
    Ui.weight(heading);
    if (!items.isEmpty()) {
      heading.addView(
          Ui.button(this, "▶  Play all", true, () -> launch(items.get(0), items, null)));
      String last = Catalog.prefs(this).getString("last:" + year, "");
      Catalog.Episode resume = Catalog.find(last);
      if (resume != null && items.contains(resume) && Catalog.position(this, resume) > 0) {
        Ui.gap(heading, 10);
        heading.addView(Ui.button(this, "Resume", false, () -> launch(resume, items, null)));
      }
    }
    if (page.equals("year")) {
      HorizontalScrollView filterScroll = new HorizontalScrollView(this);
      filterScroll.setHorizontalScrollBarEnabled(false);
      root.addView(filterScroll, Ui.lp(this, -1, 52));
      LinearLayout chips = Ui.horizontal(this);
      filterScroll.addView(chips);
      ArrayList<String> labels = new ArrayList<>(Arrays.asList("All", "Nitro", "Thunder", "PPV"));
      if (extras()) labels.addAll(Arrays.asList("Saturday Night", "Worldwide", "Pro"));
      for (String choice : labels) {
        TextView chip =
            Ui.button(
                this,
                choice,
                filter.equals(choice),
                () -> {
                  filter = choice;
                  render();
                });
        chips.addView(chip);
        Ui.gap(chips, 8);
      }
    }
    if (page.equals("ppv") || page.equals("show")) {
      LinearLayout years = Ui.horizontal(this);
      root.addView(years, Ui.lp(this, -1, 48));
      for (int value : new int[] {0, 1997, 1998, 1999, 2000, 2001}) {
        years.addView(
            Ui.button(
                this,
                value == 0 ? "All years" : Integer.toString(value),
                year == value,
                () -> {
                  year = value;
                  render();
                }));
        Ui.gap(years, 8);
      }
    }
    Ui.gap(root, 10);
    RecyclerView grid = new RecyclerView(this);
    grid.setLayoutManager(new GridLayoutManager(this, 4));
    grid.setClipToPadding(false);
    grid.setPadding(0, d(3), 0, d(20));
    root.addView(grid, new LinearLayout.LayoutParams(-1, 0, 1));
    grid.setAdapter(new EpisodeAdapter(items, false, page.equals("ppv")));
    if (items.isEmpty())
      root.addView(text("No sourced full shows match this selection.", 16, Ui.MUTED));
  }

  final class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.Holder> {
    final ArrayList<Catalog.Episode> items;
    final boolean horizontal, portrait;

    EpisodeAdapter(ArrayList<Catalog.Episode> values) {
      this(values, false);
    }

    EpisodeAdapter(ArrayList<Catalog.Episode> values, boolean rail) {
      this(values, rail, false);
    }

    EpisodeAdapter(ArrayList<Catalog.Episode> values, boolean rail, boolean posters) {
      items = values;
      horizontal = rail;
      portrait = posters;
    }

    class Holder extends RecyclerView.ViewHolder {
      ImageView image;
      TextView title, date, source;
      ProgressBar progress;

      Holder(LinearLayout card) {
        super(card);
        FrameLayout artwork = (FrameLayout) card.getChildAt(0);
        image = (ImageView) artwork.getChildAt(0);
        progress = (ProgressBar) artwork.getChildAt(1);
        title = (TextView) card.getChildAt(1);
        date = (TextView) card.getChildAt(2);
        source = (TextView) card.getChildAt(3);
      }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int type) {
      LinearLayout card = Ui.vertical(MainActivity.this);
      card.setPadding(d(8), d(8), d(8), d(10));
      card.setBackground(Ui.focus(MainActivity.this, false));
      card.setFocusable(true);
      card.setClickable(true);
      RecyclerView.LayoutParams params =
          new RecyclerView.LayoutParams(
              horizontal ? d(portrait ? 160 : 215) : -1, d(portrait ? 290 : 196));
      params.setMargins(d(4), d(4), d(8), d(8));
      card.setLayoutParams(params);
      ImageView image = new ImageView(MainActivity.this);
      image.setScaleType(
          portrait ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.CENTER_CROP);
      image.setBackground(Ui.shape(MainActivity.this, Color.rgb(33, 38, 52), 0));
      image.setClipToOutline(true);
      FrameLayout artwork = new FrameLayout(MainActivity.this);
      artwork.addView(image, new FrameLayout.LayoutParams(-1, -1));
      ProgressBar progress =
          new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleHorizontal);
      progress.setMax(100);
      progress.setProgressTintList(android.content.res.ColorStateList.valueOf(Ui.RED));
      progress.setProgressBackgroundTintList(
          android.content.res.ColorStateList.valueOf(0xb3404040));
      progress.setFocusable(false);
      progress.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
      artwork.addView(progress, new FrameLayout.LayoutParams(-1, d(4), Gravity.BOTTOM));
      card.addView(artwork, Ui.lp(MainActivity.this, -1, portrait ? 206 : 112));
      TextView title = text("", 14, Ui.WHITE);
      title.setTypeface(null, Typeface.BOLD);
      title.setMaxLines(1);
      title.setEllipsize(TextUtils.TruncateAt.END);
      title.setPadding(0, d(9), 0, d(5));
      card.addView(title, Ui.lp(MainActivity.this, -1, 34));
      card.addView(text("", 12, Ui.MUTED), Ui.lp(MainActivity.this, -1, 21));
      card.addView(text("", 10, Ui.MUTED), Ui.lp(MainActivity.this, -1, 17));
      return new Holder(card);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
      Catalog.Episode item = items.get(position);
      Art.load(holder.image, portrait && !item.poster.isEmpty() ? item.poster : item.thumbnail);
      boolean watched = Catalog.watched(MainActivity.this, item);
      long viewed = Catalog.displayPosition(MainActivity.this, item);
      long duration = Catalog.displayDuration(MainActivity.this, item);
      int percent = Catalog.progressPercent(viewed, duration, watched);
      holder.progress.setVisibility(
          watched || (viewed > 0 && duration > 0) ? View.VISIBLE : View.GONE);
      holder.progress.setProgress(percent);
      holder.title.setText((watched ? "✓  " : "") + item.heading());
      holder.date.setText(Catalog.dateLabel(item.date));
      String progressText =
          watched
              ? "Watched"
              : viewed > 0
                  ? (duration > 0 ? percent + "% watched" : viewed / 60000 + " min watched")
                  : "";
      holder.source.setText(progressText.isEmpty() ? item.sourceLabel() : progressText);
      holder.itemView.setContentDescription(
          item.heading() + ", " + Catalog.dateLabel(item.date) + ", " + progressText);
      holder.itemView.setOnClickListener(
          view -> details(item, horizontal ? Catalog.forYear(item.year, "All", extras()) : items));
    }

    @Override
    public int getItemCount() {
      return items.size();
    }
  }

  Dialog panel(LinearLayout content, int width) {
    Dialog dialog = new Dialog(this);
    content.setPadding(d(26), d(24), d(26), d(24));
    content.setBackground(Ui.shape(this, Ui.PANEL, Color.rgb(49, 56, 74)));
    dialog.setContentView(content);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.getWindow().setDimAmount(.8f);
    dialog.show();
    dialog.getWindow().setLayout(d(width), -2);
    return dialog;
  }

  void details(Catalog.Episode item, ArrayList<Catalog.Episode> queue) {
    LinearLayout content = Ui.vertical(this), top = Ui.horizontal(this);
    content.addView(top);
    ImageView image = new ImageView(this);
    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
    top.addView(image, Ui.lp(this, 310, 177));
    Art.load(image, item.thumbnail);
    Ui.gap(top, 22);
    LinearLayout info = Ui.vertical(this);
    top.addView(info, new LinearLayout.LayoutParams(0, -2, 1));
    TextView title = text(item.heading(), 26, Ui.WHITE);
    title.setTypeface(null, Typeface.BOLD);
    info.addView(title);
    Ui.gap(info, 12);
    info.addView(
        text(
            Catalog.dateLabel(item.date)
                + (item.duration > 0 ? "  ·  " + item.duration / 60 + " min" : ""),
            14,
            Ui.MUTED));
    Ui.gap(info, 9);
    info.addView(text(item.sourceLabel(), 12, Ui.RED));
    Ui.gap(content, 18);
    TextView synopsis =
        text(
            item.description.isEmpty()
                ? item.heading()
                    + " · Original broadcast: "
                    + Catalog.dateLabel(item.date)
                    + ". Plays in the WCW timeline with automatic next episode."
                : item.description,
            14,
            Ui.MUTED);
    synopsis.setMaxLines(5);
    synopsis.setEllipsize(TextUtils.TruncateAt.END);
    content.addView(synopsis);
    Ui.gap(content, 20);
    LinearLayout actions = Ui.horizontal(this);
    content.addView(actions);
    Dialog dialog = panel(content, 780);
    actions.addView(
        Ui.button(
            this,
            Catalog.position(this, item) > 0 ? "▶  Resume" : "▶  Play from here",
            true,
            () -> {
              dialog.dismiss();
              launch(item, queue, null);
            }));
    Ui.gap(actions, 10);
    actions.addView(
        Ui.button(
            this,
            "Choose source",
            false,
            () -> {
              ArrayList<String> names = new ArrayList<>(), providers = new ArrayList<>();
              names.add("Automatic");
              providers.add(null);
              for (String provider : new String[] {"archive", "youtube"})
                if (item.sources.stream()
                    .anyMatch(source -> source.optString("type").equals(provider))) {
                  names.add(provider.equals("archive") ? "Archive.org" : "Official YouTube");
                  providers.add(provider);
                }
              new AlertDialog.Builder(this)
                  .setTitle("Playback source")
                  .setItems(
                      names.toArray(new String[0]),
                      (choice, index) -> {
                        dialog.dismiss();
                        launch(item, queue, providers.get(index));
                      })
                  .show();
            }));
    Ui.gap(actions, 10);
    actions.addView(
        Ui.button(
            this,
            Catalog.watched(this, item) ? "Mark unwatched" : "Mark watched",
            false,
            () -> {
              boolean watched = !Catalog.watched(this, item);
              Catalog.progress(this, item, 0, watched);
              Catalog.prefs(this)
                  .edit()
                  .remove("seen:" + item.id)
                  .remove("part:" + item.id)
                  .apply();
              dialog.dismiss();
              render();
            }));
  }

  void launch(Catalog.Episode item, ArrayList<Catalog.Episode> queue, String provider) {
    Intent intent = new Intent(this, PlayerActivity.class);
    ArrayList<String> ids = new ArrayList<>();
    for (Catalog.Episode value : queue) ids.add(value.id);
    intent.putStringArrayListExtra("queue", ids);
    intent.putExtra("id", item.id);
    if (provider != null) intent.putExtra("provider", provider);
    if (BuildConfig.DEBUG && getIntent().getBooleanExtra("verify_autoplay", false)) {
      intent.putExtra("verify_autoplay", true);
      getIntent().removeExtra("verify_autoplay");
    }
    startActivity(intent);
  }

  void search() {
    EditText input = new EditText(this);
    input.setSingleLine(true);
    input.setHint("Show, event, year or date");
    new AlertDialog.Builder(this)
        .setTitle("Search your WCW library")
        .setView(input)
        .setPositiveButton(
            "Search",
            (dialog, which) -> {
              query = input.getText().toString().trim();
              page = "search";
              render();
            })
        .setNegativeButton("Cancel", null)
        .show();
  }

  void setting(LinearLayout content, String name, String value, Runnable action) {
    LinearLayout row = Ui.horizontal(this);
    row.setPadding(d(14), d(12), d(14), d(12));
    row.setBackground(Ui.focus(this, false));
    row.setFocusable(true);
    row.setClickable(true);
    row.setOnClickListener(view -> action.run());
    row.addView(text(name, 14, Ui.WHITE));
    Ui.weight(row);
    TextView selected = text(value + "  ›", 13, Ui.MUTED);
    row.addView(selected);
    content.addView(row, Ui.lp(this, -1, 46));
    Ui.gap(content, 5);
  }

  void settings() {
    SharedPreferences prefs = Catalog.prefs(this);
    LinearLayout content = Ui.vertical(this);
    TextView title = text("Settings", 27, Ui.WHITE);
    title.setTypeface(null, Typeface.BOLD);
    content.addView(title);
    Ui.gap(content, 16);
    ScrollView scroll = new ScrollView(this);
    LinearLayout options = Ui.vertical(this);
    scroll.addView(options);
    content.addView(scroll, Ui.lp(this, -1, 330));
    Dialog dialog = panel(content, 680);
    setting(
        options,
        "Autoplay next episode",
        prefs.getBoolean("autoplay", true) ? "On" : "Off",
        () -> {
          prefs.edit().putBoolean("autoplay", !prefs.getBoolean("autoplay", true)).apply();
          dialog.dismiss();
          settings();
        });
    String[] sources = {"Archive first", "YouTube first", "Archive only", "YouTube only"};
    int preference = prefs.getInt("source", 1);
    setting(
        options,
        "Preferred source",
        sources[preference],
        () -> {
          prefs.edit().putInt("source", (preference + 1) % 4).apply();
          dialog.dismiss();
          settings();
        });
    int quality = prefs.getInt("quality", 720);
    setting(
        options,
        "Maximum video quality",
        quality + "p",
        () -> {
          prefs.edit().putInt("quality", quality == 1080 ? 720 : 1080).apply();
          dialog.dismiss();
          settings();
        });
    setting(
        options,
        "Saturday Night, Worldwide and Pro",
        extras() ? "Included" : "Hidden",
        () -> {
          prefs.edit().putBoolean("extras", !extras()).apply();
          dialog.dismiss();
          render();
          settings();
        });
    setting(
        options,
        "Skip unavailable episodes",
        prefs.getBoolean("skip", true) ? "On" : "Off",
        () -> {
          prefs.edit().putBoolean("skip", !prefs.getBoolean("skip", true)).apply();
          dialog.dismiss();
          settings();
        });
    setting(
        options,
        "Automatic library refresh",
        prefs.getBoolean("auto_refresh", true) ? "Every 6 hours" : "Off",
        () -> {
          prefs.edit().putBoolean("auto_refresh", !prefs.getBoolean("auto_refresh", true)).apply();
          LibraryJob.schedule(this);
          dialog.dismiss();
          settings();
        });
    setting(
        options,
        "Refresh catalogue",
        "YouTube + Archive collections",
        () -> {
          dialog.dismiss();
          refresh(false);
        });
    setting(
        options,
        "Full source scan",
        "All configured Archive collections",
        () -> {
          dialog.dismiss();
          refresh(false, true);
        });
    setting(
        options,
        "Last refresh report",
        "View source checks",
        () -> {
          ScrollView report = new ScrollView(this);
          TextView contentText =
              text(prefs.getString("last_scan_report", "No completed scan yet."), 13, Ui.MUTED);
          contentText.setPadding(d(20), d(16), d(20), d(16));
          report.addView(contentText);
          new AlertDialog.Builder(this)
              .setTitle("Source refresh report")
              .setView(report)
              .setPositiveButton("OK", null)
              .show();
        });
    setting(
        options,
        "Check for app updates",
        "v" + BuildConfig.VERSION_NAME,
        () -> {
          dialog.dismiss();
          Updater.check(this);
        });
    setting(
        options,
        "Update feed",
        prefs.getString("update_feed", "").isEmpty() ? "Not connected" : "Configured",
        () -> {
          EditText input = new EditText(this);
          input.setSingleLine(true);
          input.setText(prefs.getString("update_feed", ""));
          input.setHint("HTTPS release-manifest URL");
          new AlertDialog.Builder(this)
              .setTitle("App update feed")
              .setView(input)
              .setPositiveButton(
                  "Save",
                  (box, which) ->
                      prefs
                          .edit()
                          .putString("update_feed", input.getText().toString().trim())
                          .apply())
              .setNegativeButton("Cancel", null)
              .show();
        });
    setting(
        options,
        "Playback and storage",
        "Streaming · no video downloads",
        () ->
            new AlertDialog.Builder(this)
                .setMessage(
                    "Videos stream directly to the built-in player. Only artwork, catalogue data"
                        + " and viewing progress are saved. YouTube is played without an account or"
                        + " its advertising player.")
                .setPositiveButton("OK", null)
                .show());
    setting(
        options,
        "Clear viewing history",
        "Reset progress",
        () ->
            new AlertDialog.Builder(this)
                .setTitle("Clear viewing progress?")
                .setMessage("This resets watched markers and resume positions.")
                .setPositiveButton(
                    "Clear",
                    (box, which) -> {
                      SharedPreferences.Editor edit = prefs.edit();
                      for (String key : prefs.getAll().keySet())
                        if (key.startsWith("pos:")
                            || key.startsWith("watched:")
                            || key.startsWith("seen:")
                            || key.startsWith("length:")
                            || key.startsWith("partLength:")
                            || key.equals("last")
                            || key.startsWith("last:")
                            || key.startsWith("part:")
                            || key.startsWith("provider:")
                            || key.startsWith("choice:")) edit.remove(key);
                      edit.apply();
                      dialog.dismiss();
                      render();
                    })
                .setNegativeButton("Cancel", null)
                .show());
    setting(
        options,
        "Open-source components",
        "Media3 + NewPipeExtractor",
        () ->
            new AlertDialog.Builder(this)
                .setTitle("WCW Chronology")
                .setMessage(
                    "Personal WCW chronology player. Not affiliated with WWE.\n\n"
                        + "Playback: AndroidX Media3 (Apache 2.0).\n"
                        + "YouTube extraction: NewPipeExtractor (GPL 3.0 or later).\n"
                        + "Source: supplied alongside this APK.\n\n"
                        + "Videos remain hosted by their original providers.")
                .setNeutralButton("Licences", (box, which) -> licences())
                .setPositiveButton("OK", null)
                .show());
  }

  void refresh(boolean silent) {
    refresh(silent, false);
  }

  void refresh(boolean silent, boolean full) {
    if (CatalogSync.busy) {
      Toast.makeText(this, "The library is already refreshing", Toast.LENGTH_SHORT).show();
      return;
    }
    if (!silent) Toast.makeText(this, "Checking full-show sources…", Toast.LENGTH_SHORT).show();
    work.execute(
        () -> {
          try {
            CatalogSync.refresh(this, full);
            runOnUiThread(
                () -> {
                  refreshWasBusy = false;
                  render();
                  notifyFound(!silent);
                });
          } catch (Exception error) {
            runOnUiThread(
                () -> {
                  updateRefresh();
                  if (!silent)
                    Toast.makeText(
                            this,
                            error instanceof CatalogSync.Deferred
                                ? error.getMessage() + " · refresh resumes when you return"
                                : "Refresh stopped · "
                                    + CatalogSync.reason(error)
                                    + " · saved library kept",
                            Toast.LENGTH_LONG)
                        .show();
                });
          }
        });
  }

  void licences() {
    try {
      String value =
          Catalog.read(getAssets().open("licenses/THIRD_PARTY_NOTICES.txt"))
              + "\n\n"
              + Catalog.read(getAssets().open("licenses/GPL-3.0.txt"))
              + "\n\n"
              + Catalog.read(getAssets().open("licenses/APACHE-2.0.txt"));
      ScrollView scroll = new ScrollView(this);
      TextView text = text(value, 12, Ui.MUTED);
      text.setPadding(d(20), d(16), d(20), d(16));
      scroll.addView(text);
      new AlertDialog.Builder(this)
          .setTitle("Open-source licences")
          .setView(scroll)
          .setPositiveButton("OK", null)
          .show();
    } catch (Exception error) {
      Toast.makeText(
              this, "Licence texts are also included with the source bundle.", Toast.LENGTH_LONG)
          .show();
    }
  }

  void updateRefresh() {
    if (refreshStrip == null) return;
    String error = Catalog.prefs(this).getString("last_refresh_error", "");
    refreshStrip.setVisibility(CatalogSync.busy || !error.isEmpty() ? View.VISIBLE : View.GONE);
    refreshText.setText(CatalogSync.busy ? CatalogSync.status : "Last scan skipped: " + error);
    refreshBar.setVisibility(CatalogSync.busy ? View.VISIBLE : View.GONE);
    refreshBar.setMax(Math.max(1, CatalogSync.total));
    refreshBar.setProgress(CatalogSync.completed);
  }

  void checkRefresh() {
    SharedPreferences prefs = Catalog.prefs(this);
    long checked =
        Math.max(prefs.getLong("last_refresh", 0), prefs.getLong("last_refresh_attempt", 0));
    if (prefs.getBoolean("auto_refresh", true)
        && !CatalogSync.busy
        && !PlayerActivity.playbackActive
        && (prefs.getBoolean("refresh_deferred", false)
            || (prefs.getLong("refresh_retry_after", 0) > 0
                && System.currentTimeMillis() > prefs.getLong("refresh_retry_after", 0))
            || System.currentTimeMillis() - checked > CatalogSync.INTERVAL)) refresh(true);
  }

  void notifyFound(boolean always) {
    SharedPreferences prefs = Catalog.prefs(this);
    int episodes = prefs.getInt("pending_episodes", 0),
        sources = prefs.getInt("pending_sources", 0);
    if (always || episodes > 0 || sources > 0) {
      Toast.makeText(
              this,
              episodes > 0
                  ? "Found "
                      + episodes
                      + " new episode"
                      + (episodes == 1 ? "" : "s")
                      + " · "
                      + sources
                      + " new sources"
                  : sources > 0
                      ? "Found " + sources + " new sources for existing shows"
                      : prefs.getInt("last_scan_skipped", 0) > 0
                          ? "Scan complete · no new episodes · "
                              + prefs.getInt("last_scan_skipped", 0)
                              + " source checks skipped"
                          : "Library up to date · no new episodes",
              Toast.LENGTH_LONG)
          .show();
      prefs.edit().remove("pending_episodes").remove("pending_sources").apply();
    }
  }

  @Override
  public void onBackPressed() {
    if (!page.equals("home")) {
      page = "home";
      render();
    } else super.onBackPressed();
  }

  @Override
  protected void onDestroy() {
    uiHandler.removeCallbacksAndMessages(null);
    super.onDestroy();
    work.shutdownNow();
  }
}
