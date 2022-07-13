package com.skybird.colorfulscanner.page

import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.cpad.CPAdUtils
import com.skybird.colorfulscanner.databinding.AcMoveFileBinding
import com.skybird.colorfulscanner.dialog.LoadingDialog
import com.skybird.colorfulscanner.page.main.FileType
import com.skybird.colorfulscanner.utils.CSFileUtils
import com.skybird.colorfulscanner.utils.LogCSI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Date：2022/7/1
 * Describe:
 */
class MoveFileActivity : BaseDataBindingAc<AcMoveFileBinding>() {

    private val moveFileList = arrayListOf<FileUiBean>()

    private val mAdapter by lazy { MoveFileAdapter() }
    var loadingDialog = LoadingDialog()
    override fun layoutId() = R.layout.ac_move_file

    override fun initUI() {
        CPAdUtils.loadAddFileAd()
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            tvDone.setOnClickListener {
                if (!TextUtils.isEmpty(mAdapter.curSelectFilePath)) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        var isSuccess = false
                        loadingDialog.show(supportFragmentManager, "loading")
                        moveFileList.forEach {
                            if (CSFileUtils.move(it.filePath, mAdapter.curSelectFilePath)) {
                                isSuccess = true
                            }
                            LogCSI("isSuccess==$isSuccess --${it.filePath}--\n-${mAdapter.curSelectFilePath}")
                        }
                        loadingDialog.dismiss()
                        if (isSuccess) {
                            ToastUtils.showShort(R.string.move_file_success)
                            if (!showAd()) {
                                setResult(RESULT_OK)
                                finish()
                            }
                        } else {
                            ToastUtils.showShort(R.string.move_file_failed)
                        }
                    }
                }
            }
            rv.layoutManager = LinearLayoutManager(this@MoveFileActivity)
            rv.adapter = mAdapter
        }
    }

    override fun initData() {
        (intent.getSerializableExtra("moveFile") as ArrayList<FileUiBean>?)?.let { it1 ->
            moveFileList.addAll(it1)
        }
        (intent.getSerializableExtra("noCheckedFile") as ArrayList<FileUiBean>?)?.let { it1 ->
            it1.forEach {
                if (it.fileType == FileType.FOLDER) {
                    mAdapter.data.add(it)
                }
            }
        }
    }

    private fun showAd(): Boolean {
        return CPAdUtils.showFileControlAd(this) {
            if (CSApp.isAppResume) {
                CPAdUtils.loadAddFileAd()
            }
            setResult(RESULT_OK)
            finish()
        }
    }
}