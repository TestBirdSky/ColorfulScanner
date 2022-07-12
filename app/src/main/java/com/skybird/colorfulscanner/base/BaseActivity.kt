package com.skybird.colorfulscanner.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.utils.LogCSI

/**
 * Date：2022/6/29
 * Describe:
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var isUseDataBinding = false
    protected var isResume = false
    protected var isUseDefaultDensity = false
    abstract fun layoutId(): Int

    abstract fun initUI()

    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preInit()
        density()
        if (!isUseDataBinding) {
            setContentView(layoutId())
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setStatusBarTransparent()
        initUI()
        initData()
    }

    private fun density() {
        val metrics: DisplayMetrics = resources.displayMetrics
        if (CSApp.mApp.originScaledDensity == -1f) {
            CSApp.mApp.originScaledDensity = metrics.scaledDensity
        }
        val td = if (isUseDefaultDensity && CSApp.mApp.originScaledDensity != -1f) {
            CSApp.mApp.originScaledDensity
        } else {
            metrics.heightPixels / 760f
        }
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
        LogCSI("density-->${metrics}")
    }

    private fun setStatusBarTransparent() {
        findViewById<View>(R.id.title_layout)?.run {
            setPadding(
                paddingLeft,
                ImmersionBar.getStatusBarHeight(this@BaseActivity),
                paddingRight,
                paddingBottom
            )
        }
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
    }

    override fun onResume() {
        super.onResume()
        isResume = true
    }

    override fun onPause() {
        super.onPause()
        isResume = false
    }

    open fun preInit() {

    }
}