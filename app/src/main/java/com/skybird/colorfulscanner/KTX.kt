package com.skybird.colorfulscanner

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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

fun ImageView.loadImage(uri: String) {
    Glide.with(this).applyDefaultRequestOptions(
        RequestOptions().placeholder(R.drawable.ic_item_placeholder)
            .error(R.drawable.ic_item_placeholder)
    ).load(uri).into(this)
}