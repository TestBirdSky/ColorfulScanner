package com.skybird.colorfulscanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
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

fun Activity.shareTextToOtherApp(msg: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(
        Intent.EXTRA_TEXT,
        msg
    )
    startActivity(Intent.createChooser(intent, getString(R.string.setting_share)))
}

fun Activity.jumpEmailApp(ownerEmail: String) {
    val s = "mailto:${ownerEmail}"
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse(s)
    if (isIntentAvailable(Intent.ACTION_SENDTO)) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "Contact us email is $s", Toast.LENGTH_LONG).show()
    }
}

fun Activity.isIntentAvailable(action: String): Boolean {
    val packageManager: PackageManager = packageManager
    val intent = Intent(action)
    val list = packageManager.queryIntentActivities(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )
    return list.size > 0
}