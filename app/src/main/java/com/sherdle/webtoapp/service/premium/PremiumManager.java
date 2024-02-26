package com.sherdle.webtoapp.service.premium;

import android.app.Activity;
import android.util.Log;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PremiumManager {

    private static final String TAG = "PremiumManager";
    private BillingClient billingClient;
    private Activity activity;
    private PremiumListener premiumListener;

    // SKU id untuk produk premium
    private static final String SKU_PREMIUM = "premium";

    public PremiumManager(Activity activity, PremiumListener premiumListener) {
        this.activity = activity;
        this.premiumListener = premiumListener;
        setupBillingClient();
    }

    // Menyiapkan BillingClient
    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener((billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                        handlePurchases(purchases);
                    }
                })
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    querySkuDetails();
                    queryPurchases();
                } else {
                    Log.e(TAG, "onBillingSetupFinished: Error code " + billingResult.getResponseCode());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Retry connection to the billing service
                setupBillingClient();
            }
        });
    }

    // Mengambil informasi detail SKU dari Google Play
    private void querySkuDetails() {
        List<String> skuList = new ArrayList<>();
        skuList.add(SKU_PREMIUM);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    Log.d(TAG, "querySkuDetails: " + skuDetails);
                }
            } else {
                Log.e(TAG, "querySkuDetails: Error code " + billingResult.getResponseCode());
            }
        });
    }

    // Mengambil status pembelian produk premium
    private void queryPurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                handlePurchases(purchases);
            }
        });
    }

    // Memproses pembelian produk premium
    public void purchasePremium() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        skuDetailsParamsBuilder.setSkusList(Collections.singletonList(SKU_PREMIUM))
                .setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    SkuDetails skuDetails = skuDetailsList.get(0); // Ambil detail produk premium pertama
                    BillingFlowParams.Builder flowParamsBuilder = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails);
                    BillingResult responseCode = billingClient.launchBillingFlow(activity, flowParamsBuilder.build());
                    Log.d(TAG, "purchasePremium: " + responseCode.getResponseCode());
                } else {
                    Log.e(TAG, "querySkuDetails: Error code " + billingResult.getResponseCode());
                }
            }
        });
    }

    // Memproses pembelian yang sudah dilakukan
    private void handlePurchases(List<Purchase> purchases) {
        for (Purchase purchase : purchases) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                // Produk premium telah dibeli
                premiumListener.onPremiumPurchased();
            }
        }
    }

    // Interface untuk mendengarkan kejadian pembelian produk premium
    public interface PremiumListener {
        void onPremiumPurchased();
    }
}

