package com.skybird.colorfulscanner.page

import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.ActivityPictureDealBinding
import com.skybird.colorfulscanner.utils.LogCSI

/**
 * Date：2022/7/4
 * Describe:
 */
class PictureDealActivity : BaseDataBindingAc<ActivityPictureDealBinding>() {

    private var originPictureUri: String? = null

    override fun layoutId() = R.layout.activity_picture_deal

    override fun initUI() {
        setListener()
    }

    override fun initData() {
        originPictureUri = intent.getStringExtra("picture_uri")
        originPictureUri?.let {
            binding.run {

            }
        }
    }

    private fun setListener() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            tvDone.setOnClickListener {

            }
            bottomNavigation.setOnItemSelectedListener {
                LogCSI("onItemSlistener")
                when (it.itemId) {
                    R.id.del -> {

                    }
                    R.id.tailor -> {

                    }
                    R.id.rotate -> {

                    }
                    R.id.filter -> {

                    }
                    R.id.share -> {

                    }
                }
                true
            }
        }
    }
}