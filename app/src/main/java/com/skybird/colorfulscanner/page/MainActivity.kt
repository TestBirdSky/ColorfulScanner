package com.skybird.colorfulscanner.page

import android.view.View
import com.skybird.colorfulscanner.base.BaseActivity
import com.skybird.colorfulscanner.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun layoutId(): Int? {
        binding = ActivityMainBinding.inflate(layoutInflater)
        return null
    }

    override fun initUI() {
        setOnClickListener()
    }

    override fun initData() {
    }

    private fun setOnClickListener() {
        binding.addFile.setOnClickListener {

        }
        binding.addPicture.setOnClickListener {

        }
        binding.editFile.setOnClickListener {

        }
    }

    private fun showEditFileUi() {
        binding.run {
            addFile.visibility = View.GONE
            editFile.visibility = View.GONE
            ivSetting.visibility = View.GONE

            ivClose.visibility = View.VISIBLE
            tvDone.visibility = View.VISIBLE
            bottomLayout.visibility = View.VISIBLE
        }
    }

    private fun showNormalUi() {
        binding.run {
            addFile.visibility = View.VISIBLE
            editFile.visibility = View.VISIBLE
            ivSetting.visibility = View.VISIBLE

            ivClose.visibility = View.GONE
            tvDone.visibility = View.GONE
            bottomLayout.visibility = View.GONE
        }
    }

}