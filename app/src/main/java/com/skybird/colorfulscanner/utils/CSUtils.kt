package com.skybird.colorfulscanner.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Date：2022/7/1
 * Describe:
 */
object CSUtils {

    fun showSoftInput(context:Context,view: View){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}