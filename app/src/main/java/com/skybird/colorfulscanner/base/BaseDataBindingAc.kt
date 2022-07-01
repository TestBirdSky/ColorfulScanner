package com.skybird.colorfulscanner.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Date：2022/6/30
 * Describe:
 */
abstract class BaseDataBindingAc<T : ViewDataBinding>:BaseActivity() {
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        isUseDataBinding = true
        binding = DataBindingUtil.setContentView(this, layoutId())
        binding.lifecycleOwner = this
        super.onCreate(savedInstanceState)
    }
}