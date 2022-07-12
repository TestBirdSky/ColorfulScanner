package com.skybird.colorfulscanner.cpad

import android.app.Activity
import android.app.Application
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions

/**
 * Date：2022/7/12
 * Describe:
 */
class CPAdmobImpl(val mApp:Application) {

    fun loadAd(type: String, id: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        when (type) {
            "i" -> {
                loadIAd(id, loadFailed, loadSuccess)
            }
            "o" -> {
                loadOAd(id, loadFailed, loadSuccess)
            }
            "n" -> {
                loadNativeAd(id, loadFailed, loadSuccess)
            }
        }
    }

    private fun loadIAd(id: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            mApp,
            id,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    loadFailed.invoke()
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    loadSuccess.invoke(p0)
                }
            }
        )
    }

    private fun loadOAd(id: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            mApp,
            id,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    loadFailed.invoke()
                }

                override fun onAdLoaded(p0: AppOpenAd) {
                    loadSuccess.invoke(p0)
                }
            }
        )
    }

    private fun loadNativeAd(id: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        val builder = AdLoader.Builder(mApp, id)
        builder.forNativeAd { nativeAd ->
            loadSuccess.invoke(nativeAd)
        }
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                loadFailed.invoke()
            }
        }).withNativeAdOptions(
            NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
                .build()
        ).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun showOpenAd(activity: Activity, openAd: AppOpenAd, callback: FullScreenContentCallback) {
        openAd.fullScreenContentCallback = callback
        openAd.show(activity)
    }

    fun showIAd(
        activity: Activity,
        interstitialAd: InterstitialAd,
        callback: FullScreenContentCallback
    ) {
        interstitialAd.fullScreenContentCallback = callback
        interstitialAd.show(activity)
    }
}