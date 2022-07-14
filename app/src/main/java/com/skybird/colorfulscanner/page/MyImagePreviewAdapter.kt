package com.skybird.colorfulscanner.page

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import cc.shinichi.library.view.photoview.PhotoView
import com.bumptech.glide.Glide
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.utils.LogCSI

/**
 * Date：2022/7/7
 * Describe:
 */
class MyImagePreviewAdapter(
    private val activity: AppCompatActivity,
    var imageInfo: ArrayList<String>
) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val convertView = View.inflate(activity, R.layout.image_preview_item, null)
        val photoView = convertView.findViewById<PhotoView>(R.id.iv)
        photoView.minimumScale = 0.8f
        photoView.mediumScale = 1f
        photoView.maximumScale = 5f
        Glide.with(photoView).load(imageInfo[position])
            .into(photoView)
        container.addView(convertView)
        return convertView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        LogCSI("getCount--->${imageInfo.size}")
        return imageInfo.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }
}