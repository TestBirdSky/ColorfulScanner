package com.skybird.colorfulscanner.page.v

import android.content.Intent
import android.net.VpnService
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.shadowsocks.Core
import com.github.shadowsocks.bg.BaseService
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.cpad.CPAdUtils
import com.skybird.colorfulscanner.databinding.ActivityVMainBinding
import com.skybird.colorfulscanner.toNexAct
import com.skybird.colorfulscanner.utils.CSUtils
import com.skybird.colorfulscanner.utils.DataConversionUtils
import com.skybird.colorfulscanner.utils.LogCSI
import com.skybird.colorfulscanner.utils.MAX_WAIT_TIME
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2022/7/8
 * Describe:
 */
class VMainActivity : BaseDataBindingAc<ActivityVMainBinding>() {

    private val mViewModel by lazy { ViewModelProvider(this)[VMainViewModel::class.java] }

    private val vPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                dealV()
            }
        }
    private val selectedSer =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.run {
                    mViewModel.curSerName.value = getStringExtra("selectedName")
                    mViewModel.curCircleNativeIcon.value =
                        getIntExtra("selectedNative", R.drawable.ic_default_n)
                    chV()
                }
            }
        }

    companion object {
        var curState: BaseService.State = BaseService.State.Idle
    }

    private val ssHelper by lazy { SSHelper { stateChange(it) } }

    override fun layoutId() = R.layout.activity_v_main

    override fun initUI() {
        setOnClickListener()
    }

    override fun initData() {
        ssHelper.connect(this)
        ssHelper.trafficInfo = {
            binding.run {
                LogCSI("it.rxRate ${it.rxRate}  --${it.txRate}")
                tvDownSpeed.text = DataConversionUtils.byteToVMainRateStr(it.rxRate)
                tvUploadSpeed.text = DataConversionUtils.byteToVMainRateStr(it.txRate)
            }
        }
        mViewModel.run {
            curSerName.observe(this@VMainActivity, {
                binding.tvName.text = it
            })
            curCircleNativeIcon.observe(this@VMainActivity, {
                binding.ivNative.setImageResource(it)
            })
            curConnectedTimeStr.observe(this@VMainActivity, {
                binding.tvConnectionTime.text = it
            })
        }
    }

    private fun setOnClickListener() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            ivServer.setOnClickListener {
                selectedSer.launch(Intent().apply {
                    setClass(this@VMainActivity, SerSelete1Activity::class.java)
                })
            }
            ivConnection.setOnClickListener {
                chV()
            }
        }
    }

    private fun chV() {
        val intent = VpnService.prepare(this@VMainActivity)
        if (intent == null) {
            dealV()
        } else {
            vPermission.launch(intent)
        }
    }

    private fun dealV() {
        if (!CSUtils.hasNetwork()) {
            ToastUtils.showShort(R.string.connect_network_tips)
            return
        }
        CPAdUtils.loadConnectionAd()
        CPAdUtils.loadResultNAd()
        lifecycleScope.launch {
            val time = System.currentTimeMillis()
            val isConnectedAction = curState == BaseService.State.Stopped
            stateChange(if (isConnectedAction) BaseService.State.Connecting else BaseService.State.Stopping)
            if (isConnectedAction)
                mViewModel.choiceSer()
            delay(1000)
            while (System.currentTimeMillis() - time < MAX_WAIT_TIME) {
                delay(1000)
                if (!isResume) {
                    mViewModel.reset()
                    stateChange(if (!isConnectedAction) BaseService.State.Connecting else BaseService.State.Stopping)
                    return@launch
                } else {
                    if (showConnectionAd()) {
                        break
                    }
                }
            }
            mViewModel.toggle(isConnectedAction)
        }
    }

    private fun showConnectionAd(): Boolean {
        return CPAdUtils.showConnectionAd(this) {
            if (CSApp.isAppResume) {
                CPAdUtils.loadConnectionAd()
                toNexAct(Result1Activity::class.java)
            }
        }
    }

    private fun stateChange(state: BaseService.State) {
        when (state) {
            BaseService.State.Idle -> stopAnimation()
            BaseService.State.Connecting -> connectingUi()
            BaseService.State.Connected -> {
                if (curState == BaseService.State.Connecting) {
                    if (isResume) {
                        toNexAct(Result1Activity::class.java)
                    }
                }
                mViewModel.connected()
                stopAnimation()
            }
            BaseService.State.Stopping -> connectingUi()

            BaseService.State.Stopped -> {
                if (curState == BaseService.State.Stopping) {
                    if (isResume) {
                        toNexAct(Result1Activity::class.java)
                    }
                }
                mViewModel.stopped()
                stopAnimation()
            }
        }
        curState = state
    }

    private fun connectingUi() {
        binding.run {
            lottieAnimation.playAnimation()
        }
    }

    private fun stopAnimation() {
        binding.run {
            lottieAnimation.progress = 0f
            lottieAnimation.cancelAnimation()
        }
    }

    override fun onStart() {
        super.onStart()
        ssHelper.onStart()
    }

    override fun onStop() {
        super.onStop()
        ssHelper.onStop()
    }

    override fun onDestroy() {
        ssHelper.disconnect()
        curNativeAd?.destroy()
        curNativeAd = null
        super.onDestroy()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (BaseService.State.Connecting != curState && BaseService.State.Stopping != curState) {
            return super.dispatchTouchEvent(ev)
        }
        return true
    }

    private var curNativeAd: NativeAd? = null

    override fun onResume() {
        super.onResume()
        if (curNativeAd == null || CSApp.isNeedRefreshHomeNAd) {
            loadNativeAd()
            CSApp.isNeedRefreshHomeNAd = false
        }
    }


    private fun loadNativeAd() {
        lifecycleScope.launch {
            CPAdUtils.loadHomeNAd()
            delay(500)
            var isSuccess = false
            while (isResume && !isSuccess) {
                val ad = CPAdUtils.showHomeNAd()
                ad?.let {
                    curNativeAd?.destroy()
                    curNativeAd = it
                    showNativeAdUi(it)
                    CPAdUtils.loadHomeNAd()
                    isSuccess = true
                }
                delay(100)
            }
        }
    }

    private fun showNativeAdUi(nativeAd: NativeAd) {
        val adView = layoutInflater
            .inflate(R.layout.home_native_layout, null) as NativeAdView

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
            val lap = (containerAd.layoutParams as ConstraintLayout.LayoutParams).apply {
                marginEnd = ConvertUtils.dp2px(16f)
                topMargin = ConvertUtils.dp2px(-8f)
                marginStart = ConvertUtils.dp2px(16f)
            }
            containerAd.layoutParams = lap
            containerAd.removeAllViews()
            containerAd.addView(adView)
            containerAd.setBackgroundResource(R.drawable.bg_ad_splace)
        }
    }

}