package com.skybird.colorfulscanner.utils

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Context
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.print.PrintAttributes
import android.provider.MediaStore
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.RuntimeException
import kotlin.random.Random

/**
 * Date：2022/6/30
 * Describe:
 */
object CSFileUtils {
    val CS_EXTERNAL_FOLDER_TOP = CSApp.mApp.getExternalFilesDir("fileCache")?.path ?: ""
    private val CACHE_DIR = CSApp.mApp.cacheDir.absolutePath
    private val tempFolder = CACHE_DIR + File.separator + "temp"
    suspend fun saveBitmapToTempFile(bitmap: Bitmap): String {
        val fileName = "${System.currentTimeMillis()}--${Random.nextInt()}.jpeg"
        saveBitmapToFile(tempFolder, fileName, bitmap)
        return tempFolder + File.separator + fileName
    }

    suspend fun saveBitmapToFile(filePath: String, fileName: String, bitmap: Bitmap): Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(filePath, fileName)
            FileIOUtils.writeFileFromBytesByStream(file, ConvertUtils.bitmap2Bytes(bitmap))
        }
    }

    fun getFile(filePath: String, fileName: String): File? {
        val path = filePath + File.separator + fileName
        return FileUtils.getFileByPath(path)
    }

    fun createFolder(filePath: String, fileName: String): File? {
        val newFilePath = filePath + File.separator + fileName
        if (FileUtils.isFileExists(newFilePath)) {
            LogCSE("createFile $newFilePath  ---file exists")
            ToastUtils.showShort(R.string.filename_repeat)
            return null
        }
        val newFile = File(filePath, fileName)
        LogCSI("createFile ${newFile.absolutePath}  ---file mkdirs")
        val isSuccess = newFile.mkdirs()
        if (!isSuccess) {
            ToastUtils.showShort(R.string.create_folder_failed)
            return null
        }
        return newFile
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
        return FileUtils.listFilesInDir(
            filePath
        ) { o1, o2 -> (o2.lastModified() - o1.lastModified()).toInt() }
    }

    fun isPicture(file: File): Boolean {
        if (!file.exists()) {
            return false
        }
        return file.absolutePath.endsWith(".jpeg") || file.endsWith(".png")
    }

    fun move(
        srcPath: String,
        destPath: String
    ): Boolean {
        val srcFile = FileUtils.getFileByPath(srcPath)
        val tempDestPath = destPath + File.separator + srcFile.name
        LogCSI("tempDestPath$tempDestPath ---$srcPath")
        return FileUtils.move(srcPath, tempDestPath)
    }

    const val REQUEST_CODE_DELETE_IMAGE = 1001
    fun deleteImage(activity: Activity, imgUri: Uri): Boolean {
        LogCSI("deleteImage_uri:$imgUri")
        val mContentResolver: ContentResolver = activity.contentResolver
        try {
            // 对于未启用分区存储的情况，若权限申请到位，则以下代码可以执行成功直接删除
            val count = mContentResolver.delete(imgUri, null, null)
            val result = count == 1
            LogCSI("DeleteImage result:$result")
            return result
        } catch (exception: SecurityException) {
            // 若启用了分区存储，上面代码delete将会报错，显示没有权限。
            // 需要捕获这个异常，并用下面代码，使用startIntentSenderForResult弹出弹窗向用户请求修改当前图片的权限
            LogCSE("--->${exception.printStackTrace()}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recoverableSecurityException: RecoverableSecurityException =
                    if (exception is RecoverableSecurityException) {
                        exception as RecoverableSecurityException
                    } else {
                        throw RuntimeException(exception.message, exception)
                    }
                val intentSender: IntentSender =
                    recoverableSecurityException.userAction.actionIntent.intentSender
                try {
                    activity.startIntentSenderForResult(
                        intentSender,
                        REQUEST_CODE_DELETE_IMAGE,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                    return true
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

    suspend fun saveJpegFileToLocalPhotoAlbum(context: Context, filePath: String) {
        withContext(Dispatchers.IO) {
            try {
                val path =
                    MediaStore.Images.Media.insertImage(context.contentResolver, filePath, "", "")
                withContext(Dispatchers.Main) {
                    ToastUtils.showLong(
                        context.getString(
                            R.string.save_picture_to_local_success,
                            path
                        )
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    ToastUtils.showShort(context.getString(R.string.save_picture_to_local_failed))
                }
            }
        }
    }

    suspend fun saveBitmapToLocalPhotoAlbum(context: Context, bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            try {
                val path =
                    MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "", "")
                withContext(Dispatchers.Main) {
                    ToastUtils.showLong(
                        context.getString(
                            R.string.save_picture_to_local_success,
                            path
                        )
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    ToastUtils.showShort(context.getString(R.string.save_picture_to_local_failed))
                }
            }
        }
    }

    suspend fun fileToPDFAndSaveLocalDocuments(filePath: String): File {
        return withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeFile(filePath)
            saveBitmapToAppDocumentsWithPDF(bitmap)
        }
    }

    suspend fun saveBitmapToAppDocumentsWithPDF(bitmap: Bitmap): File {
        return withContext(Dispatchers.IO) {
            val fileName =
                "ColorfulScanner${System.currentTimeMillis()}${Random.nextInt()}.pdf"
//            val path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
            saveBitmapToPdf(
                bitmap,
                path,
                fileName
            )
        }
    }

    suspend fun saveBitmapToTempPdfFile(bitmap: Bitmap): File {
        return withContext(Dispatchers.IO) {
            val fileName =
                "${System.currentTimeMillis()}--${Random.nextInt()}.pdf"
            saveBitmapToPdf(bitmap, tempFolder, fileName)
        }
    }

    private fun saveBitmapToPdf(bitmap: Bitmap, dir: String, name: String): File {
        val doc = PdfDocument()
        val pageWidth = PrintAttributes.MediaSize.ISO_A4.widthMils * 72 / 1000
        val scale = pageWidth.toFloat() / bitmap.width.toFloat()
        val pageHeight = (bitmap.height * scale).toInt()
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val newPage = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 0).create()
        val page = doc.startPage(newPage)
        val canvas = page.canvas
        canvas.drawBitmap(bitmap, matrix, paint)
        doc.finishPage(page)
        val file = File(dir, name)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            doc.writeTo(outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            doc.close()
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    fun delTempFolder() {
        FileUtils.delete(tempFolder)
    }

}