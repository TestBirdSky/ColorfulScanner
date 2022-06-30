package com.skybird.colorfulscanner.base

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.skybird.colorfulscanner.setTransparentStatusBar

/**
 * Date：2022/6/29
 * Describe:
 */
abstract class BaseActivity : AppCompatActivity() {

    abstract fun layoutId(): Int?

    abstract fun initUI()

    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        density()
        layoutId()?.let { setContentView(it) }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setTransparentStatusBar()
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
}