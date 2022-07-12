package com.skybird.colorfulscanner.page.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.cpad.CPAdUtils
import com.skybird.colorfulscanner.databinding.ActivityMainBinding
import com.skybird.colorfulscanner.dialog.CreateFileDialog
import com.skybird.colorfulscanner.dialog.DeleteDialog
import com.skybird.colorfulscanner.dialog.OpenPermissionDialog
import com.skybird.colorfulscanner.page.*
import com.skybird.colorfulscanner.page.v.VMainActivity
import com.skybird.colorfulscanner.toNexAct
import com.skybird.colorfulscanner.utils.CSFileUtils
import com.skybird.colorfulscanner.utils.LogCSI
import com.skybird.colorfulscanner.utils.REQUIRED_CAMERA_PERMISSIONS

class MainActivity : BaseDataBindingAc<ActivityMainBinding>() {

    companion object {
        var mCurFolderPath = CSFileUtils.CS_EXTERNAL_FOLDER_TOP
    }

    val recyclerViewHashMap = hashMapOf<String, RecyclerView>()

    lateinit var mCurAdapter: MainListAdapter
    private val mViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun initUI() {
        refreshRecycleView(arrayListOf())
        setOnClickListener()
    }

    override fun initData() {
        mViewModel.run {
            curFileUiData.observe(this@MainActivity, {
                LogCSI("data change size==>${it.size}")
                refreshRecycleView(it)
                setNullLayout(it.size == 0)
                refreshLayout()
            })
            curFolderPath.observe(this@MainActivity, {
                LogCSI("curFolderPath==>${it}")
                mCurFolderPath = it
                refreshLayout()
            })
        }
    }

    private fun refreshLayout() {
        if (mCurAdapter.isShowEditList) {
            if (mCurAdapter.itemCount > 0) {
                showEditFileUi()
            } else {
                mCurAdapter.isShowEditList = false
                refreshNormalUi()
            }
        } else {
            refreshNormalUi()
        }
    }

    private fun setOnClickListener() {
        binding.run {
            addFile.setOnClickListener {
                addFile.isClickable = false
                showAddFileDialog()
                addFile.isClickable = true
            }
            addPicture.setOnClickListener {
                LogCSI("addPicture")
                XXPermissions.with(this@MainActivity).permission(
                    REQUIRED_CAMERA_PERMISSIONS
                ).request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        if (all) {
                            toNexAct(TakePhotoActivity::class.java)
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)
                        if (never) {
                            OpenPermissionDialog({
                                XXPermissions.startPermissionActivity(
                                    this@MainActivity,
                                    permissions
                                )
                            }, {
                                ToastUtils.showShort(getString(R.string.failed_permission))
                            }).show(supportFragmentManager, "---")

                        } else {
                            ToastUtils.showShort(getString(R.string.failed_permission))
                        }
                    }
                })

            }
            ivVControl.setOnClickListener {
                toNexAct(VMainActivity::class.java)
            }
            editFile2.setOnClickListener {
                mCurAdapter.isShowEditList = true
                showEditFileUi()
            }
            editFile.setOnClickListener {
                mCurAdapter.isShowEditList = true
                showEditFileUi()
            }
            ivClose.setOnClickListener {
                mCurAdapter.isShowEditList = false
                refreshNormalUi()
            }
//            tvDone.setOnClickListener {
//                showNormalUi()
//            }
            ivSetting.setOnClickListener {
                toNexAct(SettingActivity::class.java)
            }
            ivMove.setOnClickListener {
                val checkList = mCurAdapter.getAllCheckedList()
                val noCheckFile = mCurAdapter.getNoCheckedFolderList()
                if (noCheckFile.size == 0) {
                    ToastUtils.showShort(R.string.move_file_error)
                } else if (checkList.size > 0) {
                    toNexAct(MoveFileActivity::class.java, Bundle().apply {
                        putSerializable("moveFile", checkList)
                        putSerializable("noCheckedFile", noCheckFile)
                    })
                }
            }
            ivDel.setOnClickListener {
                showDelFileDialog()
            }
            ivBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun showDelFileDialog() {
        DeleteDialog(getString(R.string.delete_file_tips)) {
            val checkList = mCurAdapter.getAllCheckedList()
            if (checkList.size > 0) {
                mViewModel.delFile(checkList)
                mCurAdapter.isShowEditList = false
                refreshNormalUi()
            }
        }.show(supportFragmentManager, "mainDelete")
    }

    private fun refreshRecycleView(data: ArrayList<FileUiBean>) {
        var rv = recyclerViewHashMap[mCurFolderPath]
        if (rv == null) {
            rv = addRv()
        }
        mCurAdapter = rv.adapter as MainListAdapter
        mCurAdapter.refreshAllData(data)
        binding.rvContainer.removeAllViews()
        binding.rvContainer.addView(rv)
    }

    private fun addRv(): RecyclerView {
        return RecyclerView(this@MainActivity).apply {
            layoutManager = MyGridLayoutManager(this@MainActivity, 3)
            mCurAdapter = MainListAdapter({ itemClick(it) },
                { showEditFileNameDialog(it) },
                { isCanDel, isCanMove ->
                    binding.run {
                        tvDel.alpha = if (isCanDel) 1f else 0.4f
                        ivDel.alpha = if (isCanDel) 1f else 0.4f
                        tvMove.alpha = if (isCanMove) 1f else 0.4f
                        ivMove.alpha = if (isCanMove) 1f else 0.4f
                    }
                })
            adapter = mCurAdapter
            itemAnimator?.changeDuration = 0
            itemAnimator?.addDuration = 0
            itemAnimator?.removeDuration = 0
            recyclerViewHashMap[mCurFolderPath] = this
        }
    }

    private fun itemClick(uiBean: FileUiBean) {
        LogCSI("ItemClickListener $uiBean")
        if (uiBean.fileType == FileType.FOLDER) {
            binding.rvContainer.removeAllViews()
            mViewModel.refreshFolderData(uiBean.filePath)
        } else {
            val s = mCurAdapter.getAllImageUrl()
            val index = s.indexOf(uiBean.filePath)
            showImage(s, if (index == -1) 0 else index)
        }
    }

    private fun showImage(imageList: List<String>, index: Int) {
//        ImagePreview.getInstance().setImageList()
        PicturePreviewActivity.activityStart(this, imageList, index)
    }

    private fun showEditFileUi() {
        binding.run {
            addFile.visibility = View.GONE
            editFile.visibility = View.GONE
            ivVControl.visibility = View.GONE
            ivSetting.visibility = View.GONE
            addPicture.visibility = View.INVISIBLE
            editFile2.visibility = View.INVISIBLE
            ivBack.visibility = View.INVISIBLE

            ivClose.visibility = View.VISIBLE
//            tvDone.visibility = View.VISIBLE
            bottomLayout.visibility = View.VISIBLE
        }
    }

    private fun refreshNormalUi() {
        binding.run {
            ivClose.visibility = View.GONE
//            tvDone.visibility = View.GONE
            bottomLayout.visibility = View.GONE

            addPicture.visibility = View.VISIBLE
            if (mCurFolderPath == CSFileUtils.CS_EXTERNAL_FOLDER_TOP) {
                addFile.visibility = View.VISIBLE
                editFile.visibility = View.VISIBLE
                ivSetting.visibility = View.VISIBLE
                //v1.0.1 暂时屏蔽
                ivVControl.visibility = View.GONE


                tvName.visibility = View.INVISIBLE
                editFile2.visibility = View.GONE
                ivBack.visibility = View.GONE

                if (mCurAdapter.itemCount == 0) {
                    editFile.visibility = View.GONE
                }
            } else {
                addFile.visibility = View.GONE
                editFile.visibility = View.GONE
                ivVControl.visibility = View.GONE
                ivSetting.visibility = View.GONE


                tvName.text = FileUtils.getFileName(mCurFolderPath)
                tvName.visibility = View.VISIBLE
                editFile2.visibility = View.VISIBLE
                ivBack.visibility = View.VISIBLE

                if (mCurAdapter.itemCount == 0) {
                    editFile2.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setNullLayout(isShowNullLayout: Boolean) {
        binding.run {
            nullLayout.visibility = if (isShowNullLayout) View.VISIBLE else View.GONE
        }
    }

    private fun showAddFileDialog() {
        LogCSI("showAddFileDialog")
        CPAdUtils.loadAddFileAd()
        CreateFileDialog { name, dialog ->
            dialog?.dismiss()
            showAddFileAd()
            CSFileUtils.createFolder(mCurFolderPath, name)?.let {
                mViewModel.addFileUiData(it)
            }
        }.show(supportFragmentManager, "addFile")
    }

    private fun showEditFileNameDialog(bean: FileUiBean) {
        LogCSI("showEditFileNameDialog")
        CreateFileDialog(getString(R.string.common_rename), bean.fileName) { newName, dialog ->
            if (CSFileUtils.renameFolder(newName, bean.fileName, mCurFolderPath)) {
                mViewModel.renameItemFile(bean, newName)
            }
            dialog?.dismiss()
        }.show(supportFragmentManager, "renameFileName")
    }

    override fun onBackPressed() {
        if (mCurAdapter.isShowEditList) {
            mCurAdapter.isShowEditList = false
            refreshNormalUi()
        } else {
            if (mCurFolderPath == CSFileUtils.CS_EXTERNAL_FOLDER_TOP) {
                super.onBackPressed()
            } else {
                FileUtils.getFileByPath(mCurFolderPath).parent?.let {
                    mViewModel.refreshFolderData(
                        it
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        binding.ivVControl.setImageResource(if (CSApp.mApp.isConnectedV) R.drawable.ic_v_connected else R.drawable.ic_v_disconnected)
        mViewModel.refreshFolderData(mCurFolderPath)
    }

    override fun onDestroy() {
        recyclerViewHashMap.clear()
        super.onDestroy()
    }

    private fun showAddFileAd() {
        CPAdUtils.showFileControlAd(this) { if (CSApp.isAppResume) CPAdUtils.loadAddFileAd() }
    }
}