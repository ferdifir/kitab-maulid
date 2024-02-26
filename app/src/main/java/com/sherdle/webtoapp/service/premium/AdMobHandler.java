package com.sherdle.webtoapp.service.premium;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sherdle.webtoapp.R;

public class AdMobHandler {
    private String ADMOB_APP_ID = "YOUR_ADMOB_APP_ID";
    private String ADMOB_BANNER_AD_UNIT_ID = "YOUR_BANNER_AD_UNIT_ID";

    private AdView mAdView;

    public AdMobHandler(Context context) {
        ADMOB_APP_ID = context.getResources().getString(R.string.ad_app_id);
        ADMOB_BANNER_AD_UNIT_ID = context.getResources().getString(R.string.ad_banner_id);
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                // Initialization complete. Proceed with loading ads.
            }
        });
    }

    public void loadBannerAd(Activity activity, LinearLayout adContainerLayout) {
        mAdView = new AdView(activity);
        mAdView.setAdUnitId(ADMOB_BANNER_AD_UNIT_ID);
        mAdView.setAdSize(AdSize.BANNER);
        adContainerLayout.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void hideBannerAd() {
        if (mAdView != null) {
            mAdView.setVisibility(View.GONE);
        }
    }

    public void showBannerAd() {
        if (mAdView != null) {
            mAdView.setVisibility(View.VISIBLE);
        }
    }

    public void setAdListener(AdListener adListener) {
        if (mAdView != null) {
            mAdView.setAdListener(adListener);
        }
    }
}

