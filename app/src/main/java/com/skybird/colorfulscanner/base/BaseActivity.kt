package com.skybird.colorfulscanner.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.skybird.colorfulscanner.R

/**
 * Date：2022/6/29
 * Describe:
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var isUseDataBinding = false
    abstract fun layoutId(): Int

    abstract fun initUI()

    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
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
}