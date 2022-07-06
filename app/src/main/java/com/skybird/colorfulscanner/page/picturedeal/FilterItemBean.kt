package com.skybird.colorfulscanner.page.picturedeal

import android.graphics.Bitmap
import com.zomato.photofilters.imageprocessors.Filter

/**
 * Date：2022/7/5
 * Describe:
 */
data class FilterItemBean(val filter: Filter, var bitmap: Bitmap, val name: String)
