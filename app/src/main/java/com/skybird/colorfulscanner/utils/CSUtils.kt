package com.skybird.colorfulscanner.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.skybird.colorfulscanner.CSApp


/**
 * Date：2022/7/1
 * Describe:
 */
object CSUtils {

    fun showSoftInput(context:Context,view: View){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun getPackInfo(): PackageInfo {
        val pm = CSApp.mApp.packageManager
        return pm.getPackageInfo(CSApp.mApp.packageName, PackageManager.GET_ACTIVITIES)
    }

}