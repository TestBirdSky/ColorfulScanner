package com.skybird.colorfulscanner.page

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.viewpager.widget.ViewPager
import cc.shinichi.library.bean.ImageInfo
import cc.shinichi.library.view.ImagePreviewAdapter
import com.blankj.utilcode.util.ConvertUtils
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.AcPicturePreviewBinding

/**
 * Date：2022/7/6
 * Describe:
 */
class PicturePreviewActivity : BaseDataBindingAc<AcPicturePreviewBinding>() {


    override fun layoutId() = R.layout.ac_picture_preview

    override fun initUI() {

    }

    override fun initData() {
        binding.run {
            viewPager.adapter = ImagePreviewAdapter(this@PicturePreviewActivity, imageList)
            viewPager.currentItem = currentItem
            setImageIdentifier()
            viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentItem = position
                    setImageIdentifier()
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })
        }
    }

    companion object {
        private var imageList = arrayListOf<ImageInfo>()
        private var currentItem = 0
        fun activityStart(context: Context?, list: List<String>, index: Int) {
            if (context == null) {
                return
            }
            imageList.clear()
            for (s in list) {
                val info = ImageInfo()
                info.originUrl = s
                info.thumbnailUrl = s
                imageList.add(info)
            }
            currentItem = index
            val intent = Intent()
            intent.setClass(context, PicturePreviewActivity::class.java)
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(
                    cc.shinichi.library.R.anim.fade_in,
                    cc.shinichi.library.R.anim.fade_out
                )
            }
        }
    }

    private fun setImageIdentifier() {
        val num = imageList.size
        binding.run {
            iconLayout.removeAllViews()
            for (i: Int in 0 until num) {
                if (currentItem == i) {
                    val view = ImageView(this@PicturePreviewActivity)
                    view.setImageResource(R.drawable.bg_pic_view_index)
                    val layoutParams =
                        LinearLayout.LayoutParams(
                            ConvertUtils.dp2px(9f),
                            ConvertUtils.dp2px(4.5f)
                        )
                    iconLayout.addView(view, layoutParams)
                    layoutParams.setMargins(ConvertUtils.dp2px(2f), 0, ConvertUtils.dp2px(2f), 0)
                    view.layoutParams = layoutParams
                } else {
                    val view = ImageView(this@PicturePreviewActivity)
                    view.setImageResource(R.drawable.bg_cricle)
                    val layoutParams = LinearLayout.LayoutParams(
                        ConvertUtils.dp2px(4.5f),
                        ConvertUtils.dp2px(4.5f)
                    )
                    layoutParams.setMargins(ConvertUtils.dp2px(2f), 0, ConvertUtils.dp2px(2f), 0)
                    iconLayout.addView(view, layoutParams)
                }
            }
        }

    }

}