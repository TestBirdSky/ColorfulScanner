package com.skybird.colorfulscanner.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import java.io.File

/**
 * Date：2022/6/30
 * Describe:
 */
object CSFileUtils {
    val CS_CACHE_FOLDER_TOP = CSApp.mApp.getExternalFilesDir("imageCache")?.path ?: ""

    fun saveBitmapToFile(filePath: String, fileName: String, bitmap: Bitmap) {

    }

    fun getFile(filePath: String, fileName: String): File? {
        val path = filePath + File.separator + fileName
        return FileUtils.getFileByPath(path)
    }

    fun createFolder(filePath: String, fileName: String): Boolean {
        val newFilePath = filePath + File.separator + fileName
        if (FileUtils.isFileExists(newFilePath)) {
            LogCSE("createFile $newFilePath  ---file exists")
            ToastUtils.showShort(R.string.filename_repeat)
            return false
        }
        val newFile = File(filePath, fileName)
        LogCSI("createFile ${newFile.absolutePath}  ---file mkdirs")
        val isSuccess = newFile.mkdirs()
        if (!isSuccess)
            ToastUtils.showShort(R.string.create_folder_failed)
        return isSuccess
    }

    fun renameFolder(newName: String, oldName: String, parentPath: String): Boolean {
        val newPath = parentPath + File.separator + newName
        val oldPath = parentPath + File.separator + oldName
        if (FileUtils.isFileExists(newPath)) {
            LogCSE("renameFolder $newPath  ---file exists")
            ToastUtils.showShort(R.string.filename_repeat)
            return false
        }
        val isSuccess = FileUtils.rename(oldPath, newName)
        if (!isSuccess)
            ToastUtils.showShort(R.string.rename_folder_failed)
        return isSuccess
    }

    fun getAllFileByFileName(filePath: String): List<File> {
        return FileUtils.listFilesInDir(filePath)
    }

    fun isPicture(file: File): Boolean {
        if (!file.exists()) {
            return false
        }
        val options = BitmapFactory.Options()
        BitmapFactory.decodeFile(file.toString(), options)
        options.inJustDecodeBounds = true
        return when {
            options.outWidth != -1 && options.outHeight != -1 -> true
            else -> false
        }
    }
}