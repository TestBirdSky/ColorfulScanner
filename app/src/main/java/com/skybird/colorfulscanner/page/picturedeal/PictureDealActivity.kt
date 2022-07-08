package com.skybird.colorfulscanner.page.picturedeal

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.gyf.immersionbar.ImmersionBar
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.ActivityPictureDealBinding
import com.skybird.colorfulscanner.dialog.BottomShareDialog
import com.skybird.colorfulscanner.dialog.DeleteDialog
import com.skybird.colorfulscanner.dialog.LoadingDialog
import com.skybird.colorfulscanner.page.main.MainActivity
import com.skybird.colorfulscanner.toNexAct
import com.skybird.colorfulscanner.utils.CSFileUtils
import com.skybird.colorfulscanner.utils.LogCSI
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI
import kotlin.random.Random

/**
 * Date：2022/7/4
 * Describe:
 */
class PictureDealActivity : BaseDataBindingAc<ActivityPictureDealBinding>() {

    init {
        System.loadLibrary("NativeImageProcessor")
    }

    private var pageState = PageStatus.NORMAL
    private var originPictureUri: String? = null
    private var curBitmap: Bitmap? = null
    private var isCanDel = false
    private val filterAdapter by lazy { FilterAdapter { filterItemClick(it) } }

    private val loadingDialog = LoadingDialog()

    override fun layoutId() = R.layout.activity_picture_deal

    override fun initUI() {
        binding.run {
            filterRv.adapter = filterAdapter
            filterRv.layoutManager =
                LinearLayoutManager(
                    this@PictureDealActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                ).apply {
                    scrollToPosition(0)
                }
            filterRv.setHasFixedSize(true)
        }
        setListener()
    }

    override fun initData() {
        originPictureUri = intent.getStringExtra("picture_uri")
        isCanDel = intent.getBooleanExtra("is_can_del_cur_uri", false)
        LogCSI("initData---originPictureUri$originPictureUri")
        originPictureUri?.let {
            binding.run {
                if (!isCanDel) {
                    bottomNavigation.menu.removeItem(R.id.del)
                }
                val uri = Uri.parse(originPictureUri)
                loadingDialog.show(supportFragmentManager, "loading")
                Glide.with(this@PictureDealActivity).asBitmap().load(uri)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            loadingDialog.dismiss()
                            curBitmap = resource
                            Glide.with(this@PictureDealActivity).load(curBitmap).into(ivNormal)
                        }
                    })
            }
        }
    }

    private fun setListener() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            tvDone.setOnClickListener {
                finishCurOperate()
            }
            ivLeftRotate.setOnClickListener {
                cropImageView.rotateImage(-90)
            }
            ivRightRotate.setOnClickListener {
                cropImageView.rotateImage(90)
            }
            bottomNavigation.setOnItemSelectedListener { menuItem ->
                LogCSI("onItemSlistener")
                when (menuItem.itemId) {
                    R.id.del -> {
                        showDelDialog()
                    }
                    R.id.tailor -> {
                        pageState = PageStatus.TAILOR
                        refreshPage()
                    }
                    R.id.rotate -> {
                        pageState = PageStatus.ROTATE
                        refreshPage()
                    }
                    R.id.filter -> {
                        LogCSI("click--->start${System.currentTimeMillis()}")
                        pageState = PageStatus.FILTER
                        refreshPage()
                        curBitmap?.let { bitmap ->
                            filterAdapter.refreshData(bitmap)
                        }
                        LogCSI("click--->end${System.currentTimeMillis()}")
                    }
                    R.id.share -> {
                        pageState = PageStatus.SHARE
                        curBitmap?.let {
                            BottomShareDialog(it).show(supportFragmentManager, "bottom_dialog")
                        }
                    }
                }
                true
            }
        }
    }


    private fun refreshPage() {
        when (pageState) {
            PageStatus.NORMAL -> showNormal()
            PageStatus.TAILOR -> showTailor()
            PageStatus.ROTATE -> showRotate()
            PageStatus.FILTER -> showFilter()
            PageStatus.SHARE -> {// nothing do
            }
        }
    }

    private fun showTailor() {
        binding.run {
            curBitmap?.let { cropImageView.setImageBitmap(curBitmap) }
            cropImageView.visibility = View.VISIBLE
            cropImageView.isCanCrop = true
            cropImageView.guidelines = CropImageView.Guidelines.ON

            ivLeftRotate.visibility = View.GONE
            ivRightRotate.visibility = View.GONE

            ivNormal.visibility = View.GONE
            filterRv.visibility = View.GONE
            bottomNavigation.visibility = View.INVISIBLE

            titleLayout.setBackgroundColor(getColor(R.color.black_020718))
            parent.setBackgroundColor(getColor(R.color.black_020718))
            ivBack.setImageResource(R.drawable.ic_white_back)
            tvDone.setTextColor(getColor(R.color.white))
            ImmersionBar.with(this@PictureDealActivity)
                .statusBarDarkFont(false)
                .init()
        }
    }

    private fun showRotate() {
        binding.run {
            curBitmap?.let { cropImageView.setImageBitmap(curBitmap) }
            cropImageView.visibility = View.VISIBLE
            cropImageView.guidelines = CropImageView.Guidelines.OFF
            cropImageView.isCanCrop = false

            ivLeftRotate.visibility = View.VISIBLE
            ivRightRotate.visibility = View.VISIBLE

            ivNormal.visibility = View.GONE
            filterRv.visibility = View.GONE
            bottomNavigation.visibility = View.INVISIBLE

            titleLayout.setBackgroundColor(getColor(R.color.black_020718))
            parent.setBackgroundColor(getColor(R.color.black_020718))
            ivBack.setImageResource(R.drawable.ic_white_back)
            tvDone.setTextColor(getColor(R.color.white))
            ImmersionBar.with(this@PictureDealActivity)
                .statusBarDarkFont(false)
                .init()
        }
    }

    private fun showFilter() {
        LogCSI("showFilter--->start${System.currentTimeMillis()}")

        binding.run {
            cropImageView.visibility = View.GONE
            ivLeftRotate.visibility = View.GONE
            ivRightRotate.visibility = View.GONE

            ivNormal.visibility = View.VISIBLE
            filterRv.visibility = View.VISIBLE
            bottomNavigation.visibility = View.INVISIBLE

            titleLayout.setBackgroundColor(getColor(R.color.black_020718))
            parent.setBackgroundColor(getColor(R.color.black_020718))
            ivBack.setImageResource(R.drawable.ic_white_back)
            tvDone.setTextColor(getColor(R.color.white))
            ImmersionBar.with(this@PictureDealActivity)
                .statusBarDarkFont(false)
                .init()
        }
        LogCSI("showFilter--->end${System.currentTimeMillis()}")
    }

    private fun showNormal() {
        binding.run {
            Glide.with(this@PictureDealActivity).load(curBitmap).into(ivNormal)
            ivNormal.visibility = View.VISIBLE
            bottomNavigation.visibility = View.VISIBLE

            cropImageView.visibility = View.GONE
            filterRv.visibility = View.GONE
            ivLeftRotate.visibility = View.GONE
            ivRightRotate.visibility = View.GONE


            titleLayout.setBackgroundColor(getColor(R.color.white))
            parent.setBackgroundColor(getColor(R.color.white_F8F8FC))
            ivBack.setImageResource(R.drawable.ic_back)
            tvDone.setTextColor(getColor(R.color.black_020302))
            ImmersionBar.with(this@PictureDealActivity)
                .statusBarDarkFont(true)
                .init()
        }
    }

    private fun finishCurOperate() {
        when (pageState) {
            PageStatus.NORMAL -> saveBitmapToLocal()
            PageStatus.TAILOR -> {
                curBitmap = binding.cropImageView.croppedImage
                pageState = PageStatus.NORMAL
                refreshPage()
            }
            PageStatus.ROTATE -> {
                curBitmap = binding.cropImageView.croppedImage
                pageState = PageStatus.NORMAL
                refreshPage()
            }
            PageStatus.FILTER -> {
                pageState = PageStatus.NORMAL
                curBitmap = filterAdapter.getCurSelectedBitmap()
                refreshPage()
            }
            PageStatus.SHARE -> {}
        }

    }

    private fun saveBitmapToLocal() {
        curBitmap?.let {
            lifecycleScope.launch {
                loadingDialog.show(supportFragmentManager, "dialog")
                val fileName = "${System.currentTimeMillis()}--${Random.nextInt()}.jpeg"
                val isSuccess =
                    CSFileUtils.saveBitmapToFile(MainActivity.mCurFolderPath, fileName, it)
                loadingDialog.dismiss()
                if (isSuccess) {
                    toNexAct(MainActivity::class.java)
                    finish()
                }

            }
        }
    }

//    private fun testData(bitmap: Bitmap) {
//        CoroutineScope(Dispatchers.IO).launch {
//            for (i in 0 until 30) {
//                val fileName = "${System.currentTimeMillis()}--${Random.nextInt()}.jpeg"
//                val isSuccess =
//                    CSFileUtils.saveBitmapToFile(MainActivity.mCurFolderPath, fileName, bitmap)
//                LogCSI("isSuccess-->$isSuccess")
//            }
//        }
//    }

    private fun filterItemClick(bean: FilterItemBean) {
        LogCSI("filterItemClick ${bean.name}")
        binding.run {
            ivNormal.setImageBitmap(bean.bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CSFileUtils.delTempFolder()
    }


    private fun showDelDialog() {
        DeleteDialog(getString(R.string.delete_picture_tips)) {
            LogCSI("del-->$originPictureUri")
            val uri = Uri.parse(originPictureUri)
            if (CSFileUtils.deleteImage(this@PictureDealActivity, uri)) {
                ToastUtils.showShort(R.string.delete_success)
                toNexAct(MainActivity::class.java)
                finish()
            }
        }.show(supportFragmentManager, "DialogDelete")
    }

    override fun onBackPressed() {
        if (pageState == PageStatus.NORMAL) {
            super.onBackPressed()
        } else {
            when (pageState) {
                PageStatus.FILTER -> binding.run {
                    ivNormal.setImageBitmap(curBitmap)
                }
            }
            pageState = PageStatus.NORMAL
            refreshPage()
        }
    }

    enum class PageStatus {
        NORMAL, TAILOR, ROTATE, FILTER, SHARE
    }
}