package com.skybird.colorfulscanner.page.v

import android.content.Intent
import android.net.VpnService
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.github.shadowsocks.Core
import com.github.shadowsocks.bg.BaseService
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
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
        lifecycleScope.launch {
            val time = System.currentTimeMillis()
            val isConnectedAction = curState == BaseService.State.Stopped
            stateChange(if (isConnectedAction) BaseService.State.Connecting else BaseService.State.Stopping)
            if (isConnectedAction)
                mViewModel.choiceSer()
            delay(1000)
            while (System.currentTimeMillis() - time < MAX_WAIT_TIME) {
                if (!isResume) {
                    mViewModel.reset()
                    stateChange(if (!isConnectedAction) BaseService.State.Connecting else BaseService.State.Stopping)
                    return@launch
                }
                delay(1000)
            }
            mViewModel.toggle(isConnectedAction)
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
        super.onDestroy()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (BaseService.State.Connecting != curState && BaseService.State.Stopping != curState) {
            return super.dispatchTouchEvent(ev)
        }
        return true
    }
}