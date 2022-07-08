package com.skybird.colorfulscanner.page.v

import android.net.VpnService
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.Core
import com.github.shadowsocks.bg.BaseService
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.ActivityVMainBinding
import com.skybird.colorfulscanner.toNexAct
import com.skybird.colorfulscanner.utils.DataConversionUtils
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
                tvDownSpeed.text = DataConversionUtils.byteToVMainRateStr(it.rxRate)
                tvUploadSpeed.text = DataConversionUtils.byteToVMainRateStr(it.txRate)
            }
        }
    }

    private fun setOnClickListener() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            ivServer.setOnClickListener {
                toNexAct(SerSelete1Activity::class.java)
            }
            ivConnection.setOnClickListener {
                vPermission.launch(VpnService.prepare(this@VMainActivity))
            }
        }
    }

    private fun dealV() {
        lifecycleScope.launch {
            val time = System.currentTimeMillis()
            val isConnectedAction = curState == BaseService.State.Stopped
            stateChange(if (isConnectedAction) BaseService.State.Connecting else BaseService.State.Stopping)
            delay(1000)
            while (System.currentTimeMillis() - time < MAX_WAIT_TIME) {
                if (isResume) {

                }
            }
            if (isConnectedAction) {
                Core.startService()
            } else {
                Core.stopService()
            }
        }
    }

    private fun stateChange(state: BaseService.State) {
        when (state) {
            BaseService.State.Idle -> TODO()
            BaseService.State.Connecting -> TODO()
            BaseService.State.Connected -> TODO()
            BaseService.State.Stopping -> TODO()
            BaseService.State.Stopped -> TODO()
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