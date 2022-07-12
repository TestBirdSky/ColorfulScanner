package com.skybird.colorfulscanner.page.main

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.FileUtils
import com.skybird.colorfulscanner.page.FileUiBean
import com.skybird.colorfulscanner.utils.CSFileUtils
import com.skybird.colorfulscanner.utils.DataConversionUtils
import com.skybird.colorfulscanner.utils.LogCSI
import java.io.File

/**
 * Date：2022/6/30
 * Describe:
 */
class MainViewModel : ViewModel() {

    val curFolderPath = MutableLiveData(CSFileUtils.CS_EXTERNAL_FOLDER_TOP)

    val curFileUiData = MutableLiveData<ArrayList<FileUiBean>>()

    fun refreshFolderData(folder: String) {
        if (!TextUtils.equals(curFolderPath.value, folder)) {
            curFolderPath.value = folder
        }
        curFileUiData.value = DataConversionUtils.fileToFileUiBean(
            CSFileUtils.getAllFileByFileName(
                folder
            )
        )
    }

    fun addFileUiData(file: File) {
        curFileUiData.value?.let {
            it.add(0, DataConversionUtils.newFileUiBean(file))
            curFileUiData.value = it
        }
    }

    fun delFile(list: ArrayList<FileUiBean>) {
        LogCSI("delFile -->${list.size}")
        curFileUiData.value?.let {
            list.forEach { bean ->
                val isSuccess = FileUtils.delete(bean.filePath)
                for (i: Int in 0 until it.size) {
                    if (it[i].filePath == bean.filePath) {
                        it.remove(it[i])
                        break
                    }
                }
                LogCSI("delFile curFileUiData-->${it.size}--${isSuccess}--${bean.filePath}")
            }
            curFileUiData.value = it
        }

    }

    fun renameItemFile(uiBean: FileUiBean, newName: String) {
        curFileUiData.value?.let {
            for (i: Int in 0 until it.size) {
                if (it[i].filePath == uiBean.filePath) {
                    it[i].fileName = newName
                    it[i].filePath = curFolderPath.value + File.separator + newName
                    LogCSI("renameItemFile -->${it[i]}")
                    break
                }
            }
            curFileUiData.value = it
        }
    }

}