package com.skybird.colorfulscanner.page

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.cpad.CPAdUtils
import com.skybird.colorfulscanner.databinding.ActivityStartUpBinding
import com.skybird.colorfulscanner.page.main.MainActivity
import com.skybird.colorfulscanner.toNexAct
import com.skybird.colorfulscanner.utils.CSUtils
import com.skybird.colorfulscanner.utils.LogCSI
import com.skybird.colorfulscanner.utils.MAX_WAIT_TIME
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2022/6/29
 * Describe:
 */
class StartUpActivity : BaseDataBindingAc<ActivityStartUpBinding>() {
    private var isHotReboot = false
    private var loadingTime = 0L
    private val jumpMainPage = MutableLiveData(false)

    override fun layoutId(): Int {
        return R.layout.activity_start_up
    }

    override fun initUI() {
        val rotate: Animation = AnimationUtils.loadAnimation(this, R.anim.start_animtor)
        binding.circularProgressBar.startAnimation(rotate)
    }

    override fun initData() {
        isHotReboot = intent.getBooleanExtra("isHotReboot", false)
        if (CSUtils.hasNetwork()) {
            loadAD()
            lifecycleScope.launch {
                delay(1000)
                var isShowAd = false
                var isPause = 0L
                while (System.currentTimeMillis() - loadingTime < MAX_WAIT_TIME) {
                    if (isResume) {
                        if (showAd()) {
                            isShowAd = true
                            break
                        }
                    } else if (System.currentTimeMillis() - isPause >= 3000L) {
                        break
                    } else if (isPause == 0L) {
                        isPause = System.currentTimeMillis()
                    }
                    delay(1000)
                }
                if (!isShowAd) {
                    toMainPage()
                }
            }
            jumpMainPage.observe(this, {
                LogCSI("jumpMainPage--> $it --$isResume")
                if (it) {
                    toMainPage()
                }
            })
        } else {
            toMainPage()
        }
    }

    private fun loadAD() {
        loadingTime = System.currentTimeMillis()
        CPAdUtils.loadSplashAd()
        CPAdUtils.loadFilterAd()
        CPAdUtils.loadAddFileAd()
    }

    private fun showAd(): Boolean {
        return CPAdUtils.showSplashAd(this) {
            if (CSApp.isAppResume) {
                jumpMainPage.value = true
            } else {
                finish()
            }
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
