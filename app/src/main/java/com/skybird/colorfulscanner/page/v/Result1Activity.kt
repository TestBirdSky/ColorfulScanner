package com.skybird.colorfulscanner.page.v

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.Utils
import com.github.shadowsocks.bg.BaseService
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.cpad.CPAdUtils
import com.skybird.colorfulscanner.databinding.AcResult1Binding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2022/7/8
 * Describe:
 */
class Result1Activity : BaseDataBindingAc<AcResult1Binding>() {
    override fun layoutId() = R.layout.ac_result1

    override fun initUI() {
        binding.run {
            tvTitle.text = CSApp.mApp.connectedName
            tvStatus.text =
                getString(
                    if (CSApp.mApp.isConnectedV) R.string.connect_status_connected
                    else R.string.connect_status_disconnected
                )
            ivNative.setImageResource(CSApp.mApp.connectedIcon)
            ivBack.setOnClickListener {
                onBackPressed()
            }
            if (CSApp.mApp.isConnectedV) {
                centerPB.progressBarColorStart = getColor(R.color.blue_5056F2)
                centerPB.progressBarColorEnd = getColor(R.color.blue_C82DFB)
            }
        }
    }

    override fun initData() {
    }

    private var curNativeAd: NativeAd? = null

    override fun onResume() {
        super.onResume()
        if (curNativeAd == null) {
            lifecycleScope.launch {
                var isSuccess = false
                while (isResume && !isSuccess) {
                    val ad = CPAdUtils.showResultNAd()
                    ad?.let {
                        curNativeAd?.destroy()
                        curNativeAd = it
                        showNativeAdUi(it)
                        CPAdUtils.loadResultNAd()
                        isSuccess = true
                    }
                    delay(100)
                }
            }
        }
    }

    private fun showNativeAdUi(nativeAd: NativeAd) {
        val adView = layoutInflater
            .inflate(R.layout.result1_natvie_layout, null) as NativeAdView

        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.media_view)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.bodyView = adView.findViewById(R.id.ad_body)

        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
        binding.run {
            val lap = (adLayout.layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = ConvertUtils.dp2px(24f)
                marginEnd = ConvertUtils.dp2px(16f)
                marginStart = ConvertUtils.dp2px(16f)
            }
            adLayout.layoutParams = lap
            adLayout.removeAllViews()
            adLayout.addView(adView)
            adLayout.setBackgroundResource(R.drawable.bg_ad_splace)
        }
    }

    override fun onDestroy() {
        curNativeAd?.destroy()
        curNativeAd = null
        super.onDestroy()
    }
}