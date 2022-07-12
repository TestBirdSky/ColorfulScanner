package com.skybird.colorfulscanner.cpad

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.skybird.colorfulscanner.utils.adLogI

/**
 * Date：2022/7/12
 * Describe:
 */
class MyFullScreenContentCallback(val closeAd: () -> Unit, val showAd: () -> Unit) :
    FullScreenContentCallback() {
    override fun onAdClicked() {
        super.onAdClicked()
    }

    override fun onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent()
        closeAd.invoke()
    }

    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
        super.onAdFailedToShowFullScreenContent(p0)
        closeAd.invoke()
    }

    override fun onAdImpression() {
        super.onAdImpression()
    }

    override fun onAdShowedFullScreenContent() {
        super.onAdShowedFullScreenContent()
        showAd.invoke()
    }
}