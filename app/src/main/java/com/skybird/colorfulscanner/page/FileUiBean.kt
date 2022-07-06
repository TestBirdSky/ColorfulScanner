package com.skybird.colorfulscanner.page

import com.skybird.colorfulscanner.page.main.FileType
import java.io.Serializable
import kotlin.random.Random

data class FileUiBean(
    var filePath: String,
    var fileName: String,
    var fileType: FileType = FileType.IMAGE,
    var pictureNum: Int = 0,
    var isChecked: Boolean = false,
    val id: Long = System.currentTimeMillis()+Random.nextInt(),
) : Serializable