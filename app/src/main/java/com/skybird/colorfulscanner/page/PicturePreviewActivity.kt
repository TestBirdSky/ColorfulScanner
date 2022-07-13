package com.skybird.colorfulscanner.page

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import cc.shinichi.library.bean.ImageInfo
import cc.shinichi.library.view.ImagePreviewAdapter
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.gyf.immersionbar.ImmersionBar
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.AcPicturePreviewBinding
import com.skybird.colorfulscanner.dialog.BottomShareDialog
import com.skybird.colorfulscanner.dialog.DeleteDialog
import com.skybird.colorfulscanner.dialog.LoadingDialog
import com.skybird.colorfulscanner.utils.LogCSE
import com.skybird.colorfulscanner.utils.LogCSI

/**
 * Date：2022/7/6
 * Describe:
 */
class PicturePreviewActivity : BaseDataBindingAc<AcPicturePreviewBinding>() {
    private lateinit var mAdapter: MyImagePreviewAdapter

    override fun layoutId() = R.layout.ac_picture_preview

    override fun initUI() {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .init()
    }

    override fun initData() {
        binding.run {
            mAdapter = MyImagePreviewAdapter(this@PicturePreviewActivity, imageList)
            viewPager.adapter = mAdapter
            viewPager.currentItem = currentItemIndex
            viewPager.pageMargin=ConvertUtils.dp2px(16f)
            setImageIdentifier()
            ivDel.setOnClickListener {
                showDelDialog()
            }
            ivBack.setOnClickListener {
                onBackPressed()
            }
            ivShare.setOnClickListener {
                LogCSI("${imageList[currentItemIndex]}")
                BottomShareDialog(null, imageList[currentItemIndex].originUrl).show(
                    supportFragmentManager,
                    "bottomDialog"
                )
            }
            viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentItemIndex = position
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
        private var currentItemIndex = 0
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
            LogCSI("--->${imageList}")
            currentItemIndex = index
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
            if (num > 1) {
                for (i: Int in 0 until num) {
                    if (currentItemIndex == i) {
                        val view = ImageView(this@PicturePreviewActivity)
                        view.setImageResource(R.drawable.bg_pic_view_index)
                        val layoutParams =
                            LinearLayout.LayoutParams(
                                ConvertUtils.dp2px(9f),
                                ConvertUtils.dp2px(4.5f)
                            )
                        iconLayout.addView(view, layoutParams)
                        layoutParams.setMargins(
                            ConvertUtils.dp2px(2f),
                            0,
                            ConvertUtils.dp2px(2f),
                            0
                        )
                        view.layoutParams = layoutParams
                    } else {
                        val view = ImageView(this@PicturePreviewActivity)
                        view.setImageResource(R.drawable.bg_cricle)
                        val layoutParams = LinearLayout.LayoutParams(
                            ConvertUtils.dp2px(4.5f),
                            ConvertUtils.dp2px(4.5f)
                        )
                        layoutParams.setMargins(
                            ConvertUtils.dp2px(2f),
                            0,
                            ConvertUtils.dp2px(2f),
                            0
                        )
                        iconLayout.addView(view, layoutParams)
                    }
                }
            }
        }
    }

    private fun showDelDialog() {
        DeleteDialog(getString(R.string.delete_picture_tips)) {
            if (currentItemIndex == imageList.size - 1) {
                binding.viewPager.currentItem = 0
            }
            val bean = imageList.removeAt(currentItemIndex)
            LogCSE("del -->${bean.originUrl}")
            FileUtils.delete(bean.originUrl)
            mAdapter.notifyDataSetChanged()
            setImageIdentifier()
        }.show(supportFragmentManager, "DialogDelete")
    }
}