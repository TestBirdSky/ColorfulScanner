package com.skybird.colorfulscanner.utils

import android.util.Log
import com.skybird.colorfulscanner.BuildConfig

/**
 * Date：2022/6/30
 * Describe:
 */
private const val TAG = "CS_LOG"

private fun i(tag: String = TAG, msg: String) {
    if (BuildConfig.DEBUG) {
        Log.i(tag, msg)
    }
}

private fun e(tag: String = TAG, msg: String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg)
    }
}

fun LogCSE(msg: String) {
    e(msg = msg)
}

fun LogCSI(msg: String) {
    i(msg = msg)
}

