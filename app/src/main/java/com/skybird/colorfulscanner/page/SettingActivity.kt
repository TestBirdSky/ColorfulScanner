package com.skybird.colorfulscanner.page

import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.AcSettingBinding
import com.skybird.colorfulscanner.jumpEmailApp
import com.skybird.colorfulscanner.shareTextToOtherApp
import com.skybird.colorfulscanner.toNexAct
import com.skybird.colorfulscanner.utils.CSUtils

/**
 * Date：2022/7/4
 * Describe:
 */
class SettingActivity : BaseDataBindingAc<AcSettingBinding>() {
    override fun layoutId() = R.layout.ac_setting

    override fun initUI() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            contactUs.setOnClickListener {
                jumpEmailApp(getString(R.string.contact_email))
            }
            privacyLayout.setOnClickListener {
                toNexAct(PrivacyPage::class.java)
            }
            shareLayout.setOnClickListener {
                shareTextToOtherApp("https://play.google.com/store/apps/details?id=${CSUtils.getPackInfo().packageName}")
            }
        }
    }

    override fun initData() {
    }
}