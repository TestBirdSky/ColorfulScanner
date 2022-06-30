package com.skybird.colorfulscanner

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager

/**
 * Date：2022/6/29
 * Describe:
 */

fun Activity.setTransparentStatusBar() {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.toNexAct(
    activity: Class<*>,
    bundle: Bundle? = null,
    requestCode: Int? = null
): Activity {
    val intent = Intent(this, activity)
    bundle?.let { intent.putExtras(it) }
    requestCode?.let {
        startActivityForResult(intent, requestCode)
    } ?: run {
        startActivity(intent)
    }
    return this
}