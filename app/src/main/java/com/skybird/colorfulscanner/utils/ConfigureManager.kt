package com.skybird.colorfulscanner.utils

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.skybird.colorfulscanner.BuildConfig
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.cpad.AdConfigureCache

/**
 * Date：2022/7/11
 * Describe:
 */
object ConfigureManager {
    private var remoteFC: String? = null
    private val localFC by lazy { CSUtils.assetGsonFileStr(CSApp.mApp, "cp_f.json") }
    private var remoteServers: String? = null
    private val localServers by lazy { CSUtils.assetGsonFileStr(CSApp.mApp, "cp_ser_list.json") }
    private var remoteAdcListStr: String? = null
    private val localAdcListStr by lazy { CSUtils.assetGsonFileStr(CSApp.mApp, "cp_ad.json") }

    fun getFastCity1Str(): String {
        if (remoteFC != null) {
            return remoteFC!!
        }
        return localFC
    }

    fun getSer1ListStr(): String {
        if (remoteServers != null) {
            return remoteAdcListStr!!
        }
        return localServers
    }

    fun getAdc1ListStr(): String {
        if (remoteAdcListStr != null) {
            return remoteServers!!
        }
        return localAdcListStr
    }

    fun loadRemoteConfigure() {
        if (!BuildConfig.DEBUG) {
            Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful) {
//                    remoteFC = Firebase.remoteConfig.getString("cp_f")
//                    remoteServers = Firebase.remoteConfig.getString("cp_ser")
                    remoteAdcListStr = Firebase.remoteConfig.getString("cp_adc")
                    AdConfigureCache.loadAdCList()
                    LogCSE("远程配置$remoteAdcListStr")
                } else {
                    LogCSE("远程配置加载失败")
                }
            }
        }
    }

}