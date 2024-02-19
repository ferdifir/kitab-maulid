package com.sherdle.webtoapp.activity;

import static com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.common.util.concurrent.ListenableFuture;
import com.sherdle.webtoapp.App;
import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.R;
import com.sherdle.webtoapp.drawer.menu.Action;
import com.sherdle.webtoapp.drawer.menu.MenuItemCallback;
import com.sherdle.webtoapp.drawer.menu.SimpleMenu;
import com.sherdle.webtoapp.service.AlarmReceiver;
import com.sherdle.webtoapp.service.LocationService;
import com.sherdle.webtoapp.service.api.response.schedule.Timings;
import com.sherdle.webtoapp.util.ThemeUtils;
import com.sherdle.webtoapp.utils.Helper;
import com.sherdle.webtoapp.viewmodel.MainViewModel;
import com.sherdle.webtoapp.widget.SwipeableViewPager;
import com.sherdle.webtoapp.widget.webview.WebToAppWebClient;
import com.tjeannin.apprate.AppRate;

import com.sherdle.webtoapp.adapter.NavigationAdapter;
import com.sherdle.webtoapp.fragment.WebFragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MenuInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MenuItemCallback {

    //Views
    public Toolbar mToolbar;
    public View mHeaderView;
    public TabLayout mSlidingTabLayout;
    public SwipeableViewPager mViewPager;

    //App Navigation Structure
    private NavigationAdapter mAdapter;
    private NavigationView navigationView;
    private SimpleMenu menu;

    private WebFragment CurrentAnimatingFragment = null;
    private int CurrentAnimation = 0;

    //Identify toolbar state
    private static int NO = 0;
    private static int HIDING = 1;
    private static int SHOWING = 2;

    //Keep track of the interstitials we show
    private int interstitialCount = -1;
    private InterstitialAd mInterstitialAd;
    private InterstitialAdLoadCallback callback;
    private LocationService locationService;
    private double lat, lon;
    private MainViewModel viewModel;
    private WorkManager workManager;
    private SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mHeaderView = (View) findViewById(R.id.header_container);
        mSlidingTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (SwipeableViewPager) findViewById(R.id.pager);
        workManager = WorkManager.getInstance(this);

        setSupportActionBar(mToolbar);
        locationService = new LocationService(this);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sharedPreference = getSharedPreferences("prayer_alarm", MODE_PRIVATE);

        mAdapter = new NavigationAdapter(getSupportFragmentManager(), this);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            String data = intent.getDataString();
            ((App) getApplication()).setPushUrl(data);
        }

        //Hiding ActionBar/Toolbar
        if (Config.HIDE_ACTIONBAR)
            getSupportActionBar().hide();
        if (getHideTabs())
            mSlidingTabLayout.setVisibility(View.GONE);

        hasPermissionToDo(this, Config.PERMISSIONS_REQUIRED);
        initSchedule();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        if ((Config.HIDE_ACTIONBAR && getHideTabs()) || ((Config.HIDE_ACTIONBAR || getHideTabs()) && getCollapsingActionBar())){
            lp.topMargin = 0;
        } else if ((Config.HIDE_ACTIONBAR || getHideTabs()) || (!Config.HIDE_ACTIONBAR && !getHideTabs() && getCollapsingActionBar())){
            lp.topMargin = getActionBarHeight();
        } else if (!Config.HIDE_ACTIONBAR && !getHideTabs()){
            lp.topMargin = getActionBarHeight() * 2;
        }

        mViewPager.setLayoutParams(lp);

        //Tabs
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount() - 1);

        mSlidingTabLayout.setupWithViewPager(mViewPager);
        mSlidingTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (getCollapsingActionBar()) {
                    showToolbar(getFragment());
                }
                mViewPager.setCurrentItem(tab.getPosition());
                showInterstitial();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for (int i = 0; i < mSlidingTabLayout.getTabCount(); i++) {
            if (Config.ICONS.length > i  && Config.ICONS[i] != 0) {
                mSlidingTabLayout.getTabAt(i).setIcon(Config.ICONS[i]);
            }
        }

        //Drawer
        if (Config.USE_DRAWER) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            DrawerLayout drawer =  ((DrawerLayout) findViewById(R.id.drawer_layout));
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, mToolbar, 0, 0);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            //Menu items
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            menu = new SimpleMenu(navigationView.getMenu(), this);
            configureMenu(menu);

            if (Config.HIDE_DRAWER_HEADER) {
                navigationView.getHeaderView(0).setVisibility(View.GONE);
                navigationView.setFitsSystemWindows(false);
            } else {
                if (Config.DRAWER_ICON != R.mipmap.ic_launcher)
                    ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.drawer_icon)).setImageResource(Config.DRAWER_ICON);
                else {
                    ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.launcher_icon)).setVisibility(View.VISIBLE);
                    ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.drawer_icon)).setVisibility(View.INVISIBLE);
                }
            }
        } else {
            ((DrawerLayout) findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        //Admob
        if (!getResources().getString(R.string.ad_banner_id).equals("") || getResources().getString(R.string.ad_interstitial_id).length() > 0) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }
        if (!getResources().getString(R.string.ad_banner_id).equals("")) {
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList(DEVICE_ID_EMULATOR)).build();
            MobileAds.setRequestConfiguration(configuration);

            // Look up the AdView as a resource and load a request.
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else {
            AdView adView = (AdView) findViewById(R.id.adView);
            adView.setVisibility(View.GONE);
        }
        if (getResources().getString(R.string.ad_interstitial_id).length() > 0 && Config.INTERSTITIAL_INTERVAL > 0){
            AdRequest adRequest = new AdRequest.Builder().build();

            callback = new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd;
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            InterstitialAd.load(MainActivity.this, getResources().getString(R.string.ad_interstitial_id), adRequest, callback);
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    // Handle the error
                    Log.d("INFO", loadAdError.toString());
                    mInterstitialAd = null;
                }
            };
            InterstitialAd.load(this,getResources().getString(R.string.ad_interstitial_id), adRequest, callback);
        }

        //Application rating
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.rate_title))
                .setMessage(String.format(getString(R.string.rate_message), getString(R.string.app_name)))
                .setPositiveButton(getString(R.string.rate_yes), null)
                .setNegativeButton(getString(R.string.rate_never), null)
                .setNeutralButton(getString(R.string.rate_later), null);

        new AppRate(this)
                .setShowIfAppHasCrashed(false)
                .setMinDaysUntilPrompt(2)
                .setMinLaunchesUntilPrompt(2)
                .setCustomDialog(builder)
                .init();

        //Showing the splash screen
        if (Config.SPLASH) {
            findViewById(R.id.imageLoading1).setVisibility(View.VISIBLE);
            //getFragment().browser.setVisibility(View.GONE);
        }

        //Toolbar styling
        if (Config.TOOLBAR_ICON != 0) {
            getSupportActionBar().setTitle("");
            ImageView imageView = findViewById(R.id.toolbar_icon);
            imageView.setImageResource(Config.TOOLBAR_ICON);
            imageView.setVisibility(View.VISIBLE);
            if (!Config.USE_DRAWER){
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
            }
        }

    }

    private void initSchedule() {
        if (isLocationPermissionGranted()) {
            Location location = locationService.getLastKnownLocation();
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLatitude();
                sharedPreference.edit().putFloat(Config.LAT_KEY, (float) lat).apply();
                sharedPreference.edit().putFloat(Config.LON_KEY, (float) lon).apply();
                viewModel.getPrayerSchedule(lat, lon);
                viewModel.dataStatus.observe(this, dataStatus -> {
                    switch (dataStatus) {
                        case LOADING:
                            Toast.makeText(this, "Sedang mendapatkan data jadwal sholat", Toast.LENGTH_SHORT).show();
                        case ERROR:
                            Toast.makeText(this, "Data jadwal sholat gagal didapat", Toast.LENGTH_SHORT).show();
                        case SUCCESS:
                            setAlarm();
                        default:
                            Log.d("","");
                    }
                });
            }
        }
    }

    private void setAlarm() {
        Toast.makeText(this, "Data jadwal sholat berhasil didapat", Toast.LENGTH_SHORT).show();
        viewModel.prayers.observe(this, prayerEntity -> {
            String next = findNextSchedule(Helper.getPrayerList(prayerEntity));
            int jam = Integer.parseInt(next.substring(0,2));
            int menit = Integer.parseInt(next.substring(3,5));
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, jam);
            calendar.set(Calendar.MINUTE, menit);
            calendar.set(Calendar.SECOND, 0);

            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        });
    }

    public static String findNextSchedule(List<String> jadwalSholat) {
        LocalTime waktuSekarangLocalTime = LocalTime.now();
        for (String jadwal : jadwalSholat) {
            LocalTime jadwalLocalTime = LocalTime.parse(jadwal);
            if (jadwalLocalTime.isAfter(waktuSekarangLocalTime)) {
                return jadwal;
            }
        }
        return null;
    }

    private boolean isWorkScheduled(String tag) {
        ListenableFuture<List<WorkInfo>> statuses = workManager.getWorkInfosByTag(tag);

        boolean running = false;
        List<WorkInfo> workInfoList = Collections.emptyList();

        try {
            workInfoList = statuses.get();
        } catch (ExecutionException e) {
            Log.d("TAG", "ExecutionException in isWorkScheduled: " + e);
        } catch (InterruptedException e) {
            Log.d("TAG", "InterruptedException in isWorkScheduled: " + e);
        }

        for (WorkInfo workInfo : workInfoList) {
            WorkInfo.State state = workInfo.getState();
            running = running || (state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED);
        }
        return running;
    }

    // using the back button of the device
    @Override
    public void onBackPressed() {
        View customView = null;
        WebChromeClient.CustomViewCallback customViewCallback = null;
        if (getFragment().chromeClient != null) {
            customView = getFragment().chromeClient.getCustomView();
            customViewCallback = getFragment().chromeClient.getCustomViewCallback();
        }

        if ((customView == null)
                && getFragment().browser.canGoBack()) {
            getFragment().browser.goBack();
        } else if (customView != null
                && customViewCallback != null) {
            customViewCallback.onCustomViewHidden();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        //Adjust menu item visibility/availability based on settings
        if (Config.HIDE_MENU_SHARE) {
            menu.findItem(R.id.share).setVisible(false);
        }
        if (Config.HIDE_MENU_HOME) {
            menu.findItem(R.id.home).setVisible(false);
        }
        if (Config.HIDE_MENU_NAVIGATION){
            menu.findItem(R.id.previous).setVisible(false);
            menu.findItem(R.id.next).setVisible(false);
        }
        if (!Config.SHOW_NOTIFICATION_SETTINGS || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            menu.findItem(R.id.notification_settings).setVisible(false);
        }

        ThemeUtils.tintAllIcons(menu, this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        WebView browser = getFragment().browser;
        if (item.getItemId() == (R.id.next)) {
            browser.goForward();
            return true;
        } else if (item.getItemId() == R.id.previous) {
            browser.goBack();
            return true;
        } else if (item.getItemId() == R.id.share) {
            getFragment().shareURL();
            return true;
        } else if (item.getItemId() == R.id.about) {
            AboutDialog();
            return true;
        } else if (item.getItemId() == R.id.home) {
            browser.loadUrl(getFragment().mainUrl);
            return true;
        } else if (item.getItemId() == R.id.close) {
            finish();
            Toast.makeText(getApplicationContext(),
                    getText(R.string.exit_message), Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.notification_settings){
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Showing the About Dialog
     */
    private void AboutDialog() {
        // setting the dialogs text, and making the links clickable
        final TextView message = new TextView(this);
        // i.e.: R.string.dialog_message =>
        final SpannableString s = new SpannableString(
                this.getText(R.string.dialog_about));
        Linkify.addLinks(s, Linkify.WEB_URLS);
        message.setTextSize(15f);
        int padding  = Math.round(20 * getResources().getDisplayMetrics().density);
        message.setPadding(padding, 15, padding, 15);
        message.setText(Html.fromHtml(getString(R.string.dialog_about)));
        message.setMovementMethod(LinkMovementMethod.getInstance());

        // creating the actual dialog

        AlertDialog.Builder AlertDialog = new AlertDialog.Builder(this);
        AlertDialog.setTitle(Html.fromHtml(getString(R.string.about)))
                // .setTitle(R.string.about)
                .setCancelable(true)
                // .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("ok", null).setView(message).create().show();
    }

    /**
     * Set the ActionBar Title
     * @param title title
     */
    public void setTitle(String title) {
        if (mAdapter != null && mAdapter.getCount() == 1 && !Config.USE_DRAWER && !Config.STATIC_TOOLBAR_TITLE)
            getSupportActionBar().setTitle(title);
    }

    /**
     * @return the Current WebFragment
     */
    public WebFragment getFragment(){
        return (WebFragment) mAdapter.getCurrentFragment();
    }

    /**
     * Hide the Splash Screen
     */
    public void hideSplash() {
        if (Config.SPLASH) {
            if (findViewById(R.id.imageLoading1).getVisibility() == View.VISIBLE) {
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        // hide splash image
                        findViewById(R.id.imageLoading1).setVisibility(
                                View.GONE);
                    }
                    // set a delay before splashscreen is hidden
                }, Config.SPLASH_SCREEN_DELAY);
            }
        }
    }

    /**
     * Hide the toolbar
     */
    public void hideToolbar() {
        if (CurrentAnimation != HIDING) {
            CurrentAnimation = HIDING;
            AnimatorSet animSetXY = new AnimatorSet();

            ObjectAnimator animY = ObjectAnimator.ofFloat(getFragment().rl, "y", 0);
            ObjectAnimator animY1 = ObjectAnimator.ofFloat(mHeaderView, "y", -getActionBarHeight());
            animSetXY.playTogether(animY, animY1);

            animSetXY.start();
            animSetXY.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    CurrentAnimation = NO;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

        }
    }

    /**
     * Show the toolbar
     * @param fragment for which to show the toolbar
     */
    public void showToolbar(WebFragment fragment) {
        if (CurrentAnimation != SHOWING || fragment != CurrentAnimatingFragment) {
            CurrentAnimation = SHOWING;
            CurrentAnimatingFragment = fragment;
            AnimatorSet animSetXY = new AnimatorSet();
            ObjectAnimator animY = ObjectAnimator.ofFloat(fragment.rl, "y", getActionBarHeight());
            ObjectAnimator animY1 = ObjectAnimator.ofFloat(mHeaderView, "y", 0);
            animSetXY.playTogether(animY, animY1);

            animSetXY.start();
            animSetXY.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    CurrentAnimation = NO;
                    CurrentAnimatingFragment = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

        }
    }

    public int getActionBarHeight() {
        int mHeight = mToolbar.getHeight();

        //Just in case we get a unreliable result, get it from metrics
        if (mHeight == 0){
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                mHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            }
        }

        return mHeight;
    }

    boolean getHideTabs(){
        if (mAdapter.getCount() == 1 || Config.USE_DRAWER){
            return true;
        } else {
            return Config.HIDE_TABS;
        }
    }

    public static boolean getCollapsingActionBar(){
        if (Config.COLLAPSING_ACTIONBAR && !Config.HIDE_ACTIONBAR){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check permissions on app start
     * @param context
     * @param permissions Permissions to check
     * @return if the permissions are available
     */
    private static boolean hasPermissionToDo(final Activity context, final String[] permissions) {
        boolean oneDenied = false;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ContextCompat.checkSelfPermission(context, permission)
                            != PackageManager.PERMISSION_GRANTED)
                oneDenied = true;
        }

        if (!oneDenied) return true;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.common_permission_explaination);
        builder.setPositiveButton(R.string.common_permission_grant, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Fire off an async request to actually get the permission
                // This will show the standard permission request dialog UI
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    context.requestPermissions(permissions,1);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    /**
     * Show an interstitial ad
     */
    public void showInterstitial(){
        if (interstitialCount == (Config.INTERSTITIAL_INTERVAL - 1)) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
            }

            interstitialCount = 0;
        } else {
            interstitialCount++;
        }
    }

    /**
     * Configure the navigationView
     * @param menu to modify
     */
    public void configureMenu(SimpleMenu menu){
        for (int i = 0; i < Config.TITLES.length; i++) {
            //The title
            String title = null;
            Object titleObj = Config.TITLES[i];
            if (titleObj instanceof Integer && !titleObj.equals(0)) {
                title = getResources().getString((int) titleObj);
            } else {
                title = (String) titleObj;
            }

            //The icon
            int icon = 0;
            if (Config.ICONS.length > i)
                icon = Config.ICONS[i];

            menu.add((String) Config.TITLES[i], icon, new Action(title, Config.URLS[i]));
        }

        menuItemClicked(menu.getFirstMenuItem().getValue(), menu.getFirstMenuItem().getKey());
    }

    @Override
    public void menuItemClicked(Action action, MenuItem item) {
        if (WebToAppWebClient.urlShouldOpenExternally(action.url)){
            //Load url outside WebView
            try {
                startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(action.url)));
            } catch(ActivityNotFoundException e) {
                if (action.url.startsWith("intent://")) {
                    startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(action.url.replace("intent://", "http://"))));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_app_message), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            //Uncheck all other items, check the current item
            for (MenuItem menuItem : menu.getMenuItems())
                menuItem.setChecked(false);
            item.setChecked(true);

            //Close the drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            //Load the url
            if (getFragment() == null) return;
            getFragment().browser.loadUrl("about:blank");
            getFragment().setBaseUrl(action.url);

            //Show intersitial if applicable
            showInterstitial();
            Log.v("INFO", "Drawer Item Selected");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSchedule();
            } else {
                Toast.makeText(this, "Izin lokasi ditolak. Aplikasi mungkin tidak berfungsi sepenuhnya.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
