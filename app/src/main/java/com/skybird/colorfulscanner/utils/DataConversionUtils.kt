package com.skybird.colorfulscanner.utils

import com.blankj.utilcode.util.FileUtils
import com.skybird.colorfulscanner.page.FileType
import com.skybird.colorfulscanner.page.FileUiBean
import java.io.File

/**
 * Date：2022/6/30
 * Describe:
 */
object DataConversionUtils {

    fun fileToFileUiBean(files: List<File>): ArrayList<FileUiBean> {
        val re = arrayListOf<FileUiBean>()
        for (i: Int in files.indices) {
            val temp = files[i]
            if (FileUtils.isDir(temp)) {
                re.add(
                    newFileUiBean(temp)
                )
            } else {
                if (CSFileUtils.isPicture(temp)) {
                    re.add(FileUiBean(temp.absolutePath, temp.name, FileType.IMAGE))
                }
            }
        }
        return re
    }

    fun newFileUiBean(file: File): FileUiBean {
        return FileUiBean(
            file.absolutePath,
            file.name,
            FileType.FOLDER,
            pictureNum = FileUtils.listFilesInDir(file.absolutePath).size,
        )
    }
}