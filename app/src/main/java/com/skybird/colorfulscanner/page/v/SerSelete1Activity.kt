package com.skybird.colorfulscanner.page.v

import androidx.recyclerview.widget.LinearLayoutManager
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.ActivitySerS1Binding

/**
 * Date：2022/7/8
 * Describe:
 */
class SerSelete1Activity : BaseDataBindingAc<ActivitySerS1Binding>() {
    private val adapter by lazy { SerAdapter {} }

    override fun layoutId() = R.layout.activity_ser_s1

    override fun initUI() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            rv.layoutManager = LinearLayoutManager(this@SerSelete1Activity)
            rv.adapter = adapter
        }
    }

    override fun initData() {

    }
}