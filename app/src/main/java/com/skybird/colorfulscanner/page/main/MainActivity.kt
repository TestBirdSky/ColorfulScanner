package com.skybird.colorfulscanner.page.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.ActivityMainBinding
import com.skybird.colorfulscanner.dialog.CreateFileDialog
import com.skybird.colorfulscanner.page.*
import com.skybird.colorfulscanner.toNexAct
import com.skybird.colorfulscanner.utils.CSFileUtils
import com.skybird.colorfulscanner.utils.LogCSI
import com.skybird.colorfulscanner.utils.REQUIRED_CAMERA_PERMISSIONS
import java.io.File

class MainActivity : BaseDataBindingAc<ActivityMainBinding>() {

    private var mCurFolderPath = CSFileUtils.CS_CACHE_FOLDER_TOP
    private val mAdapter by lazy {
        MainListAdapter()
    }
    private val mViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun initUI() {
        binding.run {
            rv.layoutManager = MyGridLayoutManager(this@MainActivity, 3)
            rv.adapter = mAdapter
            rv.itemAnimator?.changeDuration = 0
        }
        setOnClickListener()
    }

    override fun initData() {
        mViewModel.run {
            curFileUiData.observe(this@MainActivity, {
                LogCSI("data change size==>${it.size}")
                mAdapter.refreshAllData(it)
                if (it.size == 0) {
                    binding.run { nullLayout.visibility = View.VISIBLE }
                } else {
                    binding.run { nullLayout.visibility = View.GONE }
                }
            })
            curFolderPath.observe(this@MainActivity, {
                LogCSI("curFolderPath==>${it}")
                mCurFolderPath = it
                refreshNormalUi()
            })
        }
    }

    private fun setOnClickListener() {
        binding.run {
            addFile.setOnClickListener {
                showAddFileDialog()
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

                        } else {
                            ToastUtils.showShort(getString(R.string.failed_permission))
                        }
                    }
                })

            }
            editFile2.setOnClickListener {
                mAdapter.isShowEditList = true
                showEditFileUi()
            }
            editFile.setOnClickListener {
                mAdapter.isShowEditList = true
                showEditFileUi()
            }
            ivClose.setOnClickListener {
                mAdapter.isShowEditList = false
                refreshNormalUi()
            }
//            tvDone.setOnClickListener {
//                showNormalUi()
//            }
            ivSetting.setOnClickListener {
                toNexAct(SettingActivity::class.java)
            }
            ivMove.setOnClickListener {
                val checkList = mAdapter.getAllCheckEdList()
                val noCheckFile = mAdapter.noCheckEdFileList
                if (noCheckFile.size == 0) {
                    ToastUtils.showShort(R.string.move_file_error)
                } else if (checkList.size > 0) {
                    toNexAct(MoveFileActivity::class.java, Bundle().apply {
                        putSerializable("moveFile", checkList)
                        putSerializable("noCheckedFile", noCheckFile)
                    }, 1000)
                }
            }
            ivDel.setOnClickListener {
                val checkList = mAdapter.getAllCheckEdList()
                if (checkList.size > 0)
                    mViewModel.delFile(checkList)
            }
            ivBack.setOnClickListener {
                onBackPressed()
            }
        }
        mAdapter.itemClickListener = ItemClickListener {
            if (it.fileType == FileType.FOLDER) {
                mViewModel.refreshFolderData(it.filePath)
            } else {

            }
        }
        mAdapter.itemNameClickListener = ItemNameClickListener {
            showEditFileNameDialog(it)
        }
    }

    private fun showEditFileUi() {
        binding.run {
            addFile.visibility = View.GONE
            editFile.visibility = View.GONE
            ivSetting.visibility = View.GONE
            addPicture.visibility = View.GONE
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
            if (mCurFolderPath == CSFileUtils.CS_CACHE_FOLDER_TOP) {
                addFile.visibility = View.VISIBLE
                editFile.visibility = View.VISIBLE
                ivSetting.visibility = View.VISIBLE


                tvName.visibility = View.INVISIBLE
                editFile2.visibility = View.GONE
                ivBack.visibility = View.GONE
            } else {
                addFile.visibility = View.GONE
                editFile.visibility = View.GONE
                ivSetting.visibility = View.GONE


                tvName.text = FileUtils.getFileName(mCurFolderPath)
                tvName.visibility = View.VISIBLE
                editFile2.visibility = View.VISIBLE
                ivBack.visibility = View.VISIBLE
            }
        }
    }

    private fun showAddFileDialog() {
        LogCSI("showAddFileDialog")
        CreateFileDialog { name, dialog ->
            if (CSFileUtils.createFolder(mCurFolderPath, name)) {
                val file = CSFileUtils.getFile(mCurFolderPath, name)
                if (file != null) {
                    mViewModel.addFileUiData(file)
                } else {
                    ToastUtils.showShort(R.string.create_folder_failed)
                }
                dialog?.dismiss()
            }
        }.show(supportFragmentManager, "addFile")
    }

    private fun showEditFileNameDialog(bean: FileUiBean) {
        LogCSI("showEditFileNameDialog")
        CreateFileDialog(getString(R.string.common_rename), bean.fileName) { newName, dialog ->
            if (CSFileUtils.renameFolder(newName, bean.fileName, mCurFolderPath)) {
                bean.fileName = newName
                bean.filePath = mCurFolderPath + File.separator + newName
                mViewModel.renameItemFile(bean)
            }
            dialog?.dismiss()
        }.show(supportFragmentManager, "renameFileName")
    }

    override fun onBackPressed() {
        if (mAdapter.isShowEditList) {
            mAdapter.isShowEditList = false
            refreshNormalUi()
        } else {
            if (mCurFolderPath == CSFileUtils.CS_CACHE_FOLDER_TOP) {
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
        mViewModel.refreshFolderData(mCurFolderPath)
    }
}