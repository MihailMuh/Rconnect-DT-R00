package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.services.Service.print;
import static java.util.Collections.singletonList;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.mihalis.dtr00.R;
import com.mihalis.dtr00.services.Service;

public class AdMobActivity extends BaseActivity {
    private static final String realAdID = "ca-app-pub-6694626552209820/3041858479";
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob);

        Service.post(() -> {
            MobileAds.setRequestConfiguration(new RequestConfiguration.Builder()
                    .setTestDeviceIds(singletonList("01E916F7ED92474D2D40C9A091D8120B")).build());

            MobileAds.initialize(this, initializationStatus -> {
                print("MobileAds has initialized");

                AdRequest adRequest = new AdRequest.Builder().build();

                print("My device is tested: " + adRequest.isTestDevice(this));
                loadAd(adRequest);
            });
        });
    }

    private void loadAd(AdRequest adRequest) {
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                AdMobActivity.this.interstitialAd = null;
                print("Failed load " + loadAdError.getMessage());

                onBackPressed();
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                AdMobActivity.this.interstitialAd = interstitialAd;
                print("AD loaded");

                interstitialAd.setFullScreenContentCallback(
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                print("AdDismissedFullScreenContent");
                                AdMobActivity.this.interstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                print("AdFailedToShowFullScreenContent " + adError.getMessage());
                                AdMobActivity.this.interstitialAd = null;
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                print("AdShowedFullScreenContent");
                                runOnUiThread(() -> interstitialAd.show(AdMobActivity.this));
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (interstitialAd != null) {
            interstitialAd.show(this);
        }
    }
}
