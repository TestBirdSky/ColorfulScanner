package com.skybird.colorfulscanner.page.picturedeal

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.theartofdev.edmodo.cropper.CropImageView

/**
 * Date：2022/7/7
 * Describe:
 */
class MyCropImageView constructor(context: Context, attributeSet: AttributeSet) :
    CropImageView(context, attributeSet) {

    var isCanCrop = true

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!isCanCrop) {
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

}