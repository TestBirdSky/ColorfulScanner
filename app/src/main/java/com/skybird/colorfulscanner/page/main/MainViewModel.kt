package com.skybird.colorfulscanner.page.main

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

    val curFolderPath = MutableLiveData(CSFileUtils.CS_CACHE_FOLDER_TOP)

    val curFileUiData = MutableLiveData<ArrayList<FileUiBean>>()

    fun refreshFolderData(folder: String) {
        curFileUiData.value = DataConversionUtils.fileToFileUiBean(
            CSFileUtils.getAllFileByFileName(
                folder
            )
        )
        curFolderPath.value = folder
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
                    if (it[i].id == bean.id) {
                        it.remove(it[i])
                        break
                    }
                }
                LogCSI("delFile curFileUiData-->${it.size}--${isSuccess}--${bean.filePath}")
            }
            curFileUiData.value = it
        }

    }

    fun renameItemFile(uiBean: FileUiBean) {
        curFileUiData.value?.let {
            for (i: Int in 0 until it.size) {
                if (it[i].id == uiBean.id) {
                    it[i].fileName = uiBean.fileName
                    it[i].filePath = uiBean.filePath
                    LogCSI("renameItemFile -->${it[i]}")
                    break
                }
            }
            curFileUiData.value = it
        }
    }

}