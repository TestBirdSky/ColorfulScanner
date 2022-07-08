package com.skybird.colorfulscanner.page

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.ActivityStartUpBinding
import com.skybird.colorfulscanner.page.main.MainActivity
import com.skybird.colorfulscanner.toNexAct
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2022/6/29
 * Describe:
 */
class StartUpActivity : BaseDataBindingAc<ActivityStartUpBinding>() {
    private var isHotReboot = false
    override fun layoutId(): Int {
        return R.layout.activity_start_up
    }

    override fun initUI() {
        val rotate: Animation = AnimationUtils.loadAnimation(this, R.anim.start_animtor)
        binding.circularProgressBar.startAnimation(rotate)
    }

    override fun initData() {
        isHotReboot = intent.getBooleanExtra("isHotReboot", false)
        lifecycleScope.launch {
            delay(2000)
            toMainPage()
        }
    }

    private fun toMainPage() {
        if (!isHotReboot) {
            toNexAct(MainActivity::class.java)
        }
        finish()
    }

    override fun onBackPressed() {

    }
}
