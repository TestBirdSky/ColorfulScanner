package com.skybird.colorfulscanner.page

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import cc.shinichi.library.bean.ImageInfo
import cc.shinichi.library.view.ImagePreviewAdapter
import cc.shinichi.library.view.helper.FingerDragHelper
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.utils.LogCSE

/**
 * Date：2022/7/7
 * Describe:
 */
class MyImagePreviewAdapter(
    private val activity: AppCompatActivity,
    private val imageInfo: List<ImageInfo>
) :
    ImagePreviewAdapter(activity, imageInfo) {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = super.instantiateItem(container, position)
        if (view is View) {
            val progressBar = view.findViewById<ProgressBar>(R.id.progress_view)
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE))
        }
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        when {
            imageInfo.isEmpty() -> {
                activity.finish()
                closePage()
            }
            position >= imageInfo.size -> {
                LogCSE("数组越界---->")
            }
            else -> {
                super.destroyItem(container, position, `object`)
            }
        }


    }
}