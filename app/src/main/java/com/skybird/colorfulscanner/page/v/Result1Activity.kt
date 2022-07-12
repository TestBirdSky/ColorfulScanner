package com.skybird.colorfulscanner.page.v

import com.github.shadowsocks.bg.BaseService
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.AcResult1Binding

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
            if (CSApp.mApp.isConnectedV){
                centerPB.progressBarColorStart= getColor(R.color.blue_5056F2)
                centerPB.progressBarColorEnd= getColor(R.color.blue_C82DFB)
            }
        }
    }

    override fun initData() {
    }
}