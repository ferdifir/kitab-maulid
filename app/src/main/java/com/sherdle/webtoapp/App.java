package com.sherdle.webtoapp;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import android.text.TextUtils;
import android.util.Log;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import com.android.billingclient.api.BillingClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;
import com.sherdle.webtoapp.activity.MainActivity;

import org.json.JSONObject;

import java.util.List;

public class App extends MultiDexApplication {

    private String push_url = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    private BillingClient billingClient;

    @Override
    public void onCreate() {
        super.onCreate();

        //TODO Do something else, i.e. test for presense of Firebase file or make this a boolean
        if (Config.ANALYTICS_ID.length() > 0) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            FirebaseApp.initializeApp(this);
        }

        //OneSignal Push
        if (!TextUtils.isEmpty(getString(R.string.onesignal_app_id))) {
            // OneSignal Initialization
            OneSignal.initWithContext(this);
            OneSignal.setAppId(getString(R.string.onesignal_app_id));

            // promptForPushNotifications will show the native Android notification permission prompt.
            // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
            OneSignal.promptForPushNotifications();

            OneSignal.setNotificationWillShowInForegroundHandler(new OneSignal.OSNotificationWillShowInForegroundHandler() {
                @Override
                public void notificationWillShowInForeground(OSNotificationReceivedEvent osNotificationReceivedEvent) {
                    osNotificationReceivedEvent.complete(osNotificationReceivedEvent.getNotification());
                }
            });

            OneSignal.setNotificationOpenedHandler(new NotificationHandler());

            //System.out.println("UserId: " + OneSignal.getDeviceState().getUserId());


            // Google Play Billing
            billingClient = BillingClient.newBuilder(this)
                    .enablePendingPurchases()
                    .setListener(purchasesUpdatedListener)
                    .build();

            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    // Lakukan sesuatu setelah koneksi berhasil
                }

                @Override
                public void onBillingServiceDisconnected() {
                    // Lakukan sesuatu ketika koneksi gagal
                }
            });
        }
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        }
    };

    public BillingClient getBillingClient() {
        return billingClient;
    }

    // This fires when a notification is opened by tapping on it or one is received while the app is running.
    private class NotificationHandler implements OneSignal.OSNotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            try {
                JSONObject data = result.getNotification().getAdditionalData();

                String webViewUrl = (data != null) ? data.optString("url", null) : null;
                String browserUrl = result.getNotification().getLaunchURL();

                if (browserUrl != null || webViewUrl != null) {
                    if (browserUrl != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserUrl));
                        startActivity(browserIntent);
                        Log.v("INFO", "Received notification while app was on foreground or url for browser");
                    } else {
                        push_url = webViewUrl;
                    }
                } else {
                    Intent mainIntent;
                    mainIntent = new Intent(App.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                }


            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public synchronized String getAndResetPushUrl() {
        String url = push_url;
        push_url = null;
        return url;
    }

    public synchronized void setPushUrl(String url) {
        this.push_url = url;
    }
} 