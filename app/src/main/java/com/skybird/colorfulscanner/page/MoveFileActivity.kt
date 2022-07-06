package com.skybird.colorfulscanner.page

import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.AcMoveFileBinding
import com.skybird.colorfulscanner.page.main.FileType
import com.skybird.colorfulscanner.utils.CSFileUtils
import com.skybird.colorfulscanner.utils.LogCSI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Date：2022/7/1
 * Describe:
 */
class MoveFileActivity : BaseDataBindingAc<AcMoveFileBinding>() {

    private val moveFileList = arrayListOf<FileUiBean>()

    private val mAdapter by lazy { MoveFileAdapter() }

    override fun layoutId() = R.layout.ac_move_file

    override fun initUI() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            tvDone.setOnClickListener {
                if (!TextUtils.isEmpty(mAdapter.curSelectFilePath)) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        var isSuccess = false
                        moveFileList.forEach {
                            if (CSFileUtils.move(it.filePath, mAdapter.curSelectFilePath)) {
                                isSuccess = true
                            }
                            LogCSI("isSuccess==$isSuccess --${it.filePath}--\n-${mAdapter.curSelectFilePath}")
                        }
                        if (isSuccess) {
                            setResult(RESULT_OK)
                            onBackPressed()
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


}