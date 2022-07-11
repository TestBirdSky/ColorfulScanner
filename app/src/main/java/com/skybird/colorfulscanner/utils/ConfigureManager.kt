package com.skybird.colorfulscanner.utils

import com.skybird.colorfulscanner.CSApp

/**
 * Date：2022/7/11
 * Describe:
 */
object ConfigureManager {

    fun getFastCity1Str(): String {
        return CSUtils.assetGsonFileStr(CSApp.mApp, "cp_f.json")
    }

    fun getSer1ListStr(): String {
        return CSUtils.assetGsonFileStr(CSApp.mApp, "cp_ser_list.json")
    }

    fun loadRemoteConfigure() {

    }

}