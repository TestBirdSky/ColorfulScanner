package com.skybird.colorfulscanner.page

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseActivity
import com.skybird.colorfulscanner.databinding.ActivityStartUpBinding
import com.skybird.colorfulscanner.toNexAct
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2022/6/29
 * Describe:
 */
class StartUpActivity : BaseActivity() {
    private lateinit var binding: ActivityStartUpBinding
    override fun layoutId(): Int? {
        binding = ActivityStartUpBinding.inflate(layoutInflater)
        return null
    }

    override fun initUI() {
        val rotate: Animation = AnimationUtils.loadAnimation(this, R.anim.start_animtor)
        binding.circularProgressBar.startAnimation(rotate)
    }

    override fun initData() {
        lifecycleScope.launch {
            delay(1000)
            toMainPage()
        }
    }

    private fun toMainPage() {
        toNexAct(MainActivity::class.java)
        finish()
    }

    override fun onBackPressed() {

    }
}
