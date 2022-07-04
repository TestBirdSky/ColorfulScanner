package com.skybird.colorfulscanner.utils

import android.Manifest
import android.os.Build

/**
 * Date：2022/7/4
 * Describe:
 */
val REQUIRED_CAMERA_PERMISSIONS =
    mutableListOf (
        Manifest.permission.CAMERA,
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()