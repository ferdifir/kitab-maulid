package com.sherdle.webtoapp;

import android.Manifest;

public class Config {

    /**
     * MAIN SETTINGS
     */

    //Set to true to use a drawer, or to false to use tabs (or neither)
    public static boolean USE_DRAWER = true;
    //Set to true if you would like to hide the actionbar at all times
    public static boolean HIDE_ACTIONBAR = false;

    //Set to true if you would like to display a splash screen on boot. Drawable shown is 'vert_loading'
    public static boolean SPLASH = true;
    //Set to true if you would like to hide the tabs. Only applies is USE_DRAWER is false & if you have more then 1 tab
    public static boolean HIDE_TABS = false;
    //Set to true if you would like to enable pull to refresh
    public static boolean PULL_TO_REFRESH = true;
    //Set to true if you would like to hide the actionbar on when scrolling down. Only applies if HIDE_ACTIONBAR is false
    public static boolean COLLAPSING_ACTIONBAR = false;
    //Set to true if you would like to use the round 'pull to refresh style' loading indicator instead of a progress bar
    public static boolean LOAD_AS_PULL = true;
    //Set to true if you want to use a light toolbar
    public static boolean LIGHT_TOOLBAR_THEME = false;

    /**
     * URL / LIST OF URLS
     */
    //The titles of your web items
    public static final Object[] TITLES = new Object[]{"Kitab Maulid"};
    //The URL's of your web items
    public static final String[] URLS = new String[]{"file:///android_asset/index.html"};
    //The icons of your web items
    public static final int[] ICONS = new int[]{};

    /**
     * IDS
     */

    //If you would like to use analytics, you can enter an analytics ID here
    public static String ANALYTICS_ID = "UA-46717142-1";

    //OneSignal and Admob IDs have to be configured in Strings.xml

    /**
     * ADVANCED SETTINGS
     */

    //All urls that should always be opened outside the WebView and in the browser, download manager, or their respective app
    public static final String[] OPEN_OUTSIDE_WEBVIEW = new String[]{"market://", "play.google.com", "plus.google.com", "mailto:", "tel:", "vid:", "geo:", "whatsapp:", "sms:", "intent://", "https://wa.me"};
    //Defines a set of urls/patterns that should exlusively load inside the webview. All other urls are loaded outside the WebView. Ignored if no urls are defined.
    public static final String[] OPEN_ALL_OUTSIDE_EXCEPT = new String[]{};

    //Set to true if you would like to hide the drawer header. (requires USE_DRAWER)
    public static boolean HIDE_DRAWER_HEADER =false;
    //Set to true if you would like to hide navigation in the toolbar (i.e. back, forward)
    public static boolean HIDE_MENU_NAVIGATION = false;
    //Set to true if you would like to sharing in the toolbar
    public static boolean HIDE_MENU_SHARE = false;
    //Set to true if you would like to hide the home button
    public static boolean HIDE_MENU_HOME = true;
    //Set to true if you would like to show a link to the apps notification settings
    public static boolean SHOW_NOTIFICATION_SETTINGS = true;

    //Set to true if you would like to support popup windows, e.g. for Facebook login
    public static boolean MULTI_WINDOWS = false;
    //If you would like to show the splash screen for an additional amount of time after page load, define it here (MS)
    public static int SPLASH_SCREEN_DELAY = 0;
    //Permissions required to use the app (should also be in manifest.xml)
    public static String[] PERMISSIONS_REQUIRED = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    }; //Manifest.permission.PERMISSION_NAME
    //Always use the app name as actionbar title (only applies for if USE_DRAWER is false and number of tabs == 1)
    public static boolean STATIC_TOOLBAR_TITLE = false;
    //Load a webpage when no internet connection was found (must be in assets). Leave empty to show dialog.
    public static String NO_CONNECTION_PAGE = "";
    //The image/icon used for in the drawer header (use R.mipmap.ic_launcher to show centered app icon)
    public static int DRAWER_ICON = R.drawable.vert_loading;
    //The image/icon used for the toolbar
    public static int TOOLBAR_ICON = 0;

    //Show interstitials when browsing web pages (or only during drawer/tab navigation)
    public static final boolean INTERSTITIAL_PAGE_LOAD = true;
    //The frequency in which interstitial ads are shown
    public static final int INTERSTITIAL_INTERVAL = 2;
    public static final String ADZAN_TIME_API = "http://api.aladhan.com/v1/calendar/";
    public static final String PREFS_KEY = "prayer_alarm";
    public static final String NOTIF_ID_KEY = "notif_key";
    public static final String IMSAK_NOTIFICATION = "imsak_notification";
    public static final String SUBUH_NOTIFICATION = "subuh_notification";
    public static final String TERBIT_NOTIFICATION = "terbit_notification";
    public static final String DZUHUR_NOTIFICATION = "dzuhur_notification";
    public static final String ASHAR_NOTIFICATION = "ashar_notification";
    public static final String MAGHRIB_NOTIFICATION = "maghrib_notification";
    public static final String ISYA_NOTIFICATION = "isya_notification";
    public static final String LAT_KEY = "latitudekey";
    public static final String LON_KEY = "longitudekey";
    public static final String IMSAK_URI = "sound_imsak_path";
    public static final String SUBUH_URI = "sound_subuh_path";
    public static final String TERBIT_URI = "sound_terbit_path";
    public static final String DHUHUR_URI = "sound_dhuhur_path";
    public static final String ASHR_URI = "sound_ashr_path";
    public static final String MAGHRIB_URI = "sound_maghrib_path";
    public static final String ISYA_URI = "sound_isya_path";
    public static final int IMSAK_REQ_CODE = 0;
    public static final int SUBUH_REQ_CODE = 1;
    public static final int TERBIT_REQ_CODE = 2;
    public static final int DHUHUR_REQ_CODE = 3;
    public static final int ASHAR_REQ_CODE = 4;
    public static final int MAGHRIB_REQ_CODE = 5;
    public static final int ISYA_REQ_CODE = 6;


}