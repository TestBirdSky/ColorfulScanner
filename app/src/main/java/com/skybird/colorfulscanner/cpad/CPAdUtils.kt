package com.skybird.colorfulscanner.cpad

import android.app.Activity
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.bean.CP1ConBean
import com.skybird.colorfulscanner.utils.MAX_WAIT_TIME
import com.skybird.colorfulscanner.utils.adLogE
import com.skybird.colorfulscanner.utils.adLogI

/**
 * Date：2022/7/12
 * Describe:
 */
object CPAdUtils {
    private val adImpl by lazy { CPAdmobImpl(CSApp.mApp) }
    private val splashDataWrap by lazy { AdDataWrap(Space.OPEN, MAX_WAIT_TIME, 1) }
    private val addFileDataWrap by lazy { AdDataWrap(Space.FILE) }
    private val filterDataWrap by lazy { AdDataWrap(Space.FILTER) }
    private val homeNativeDataWrap by lazy { AdDataWrap(Space.HOME) }
    private val resultDataWrap by lazy { AdDataWrap(Space.RESULT) }
    private val connectDataWrap by lazy { AdDataWrap(Space.CONNECT) }
    private val backDataWrap by lazy { AdDataWrap(Space.BACK) }

    fun loadSplashAd() {
        loadAd(splashDataWrap)
    }

    fun loadAddFileAd() {
        loadAd(addFileDataWrap)
    }

    fun loadFilterAd() {
        loadAd(filterDataWrap)
    }

    fun loadHomeNAd() {
        loadAd(homeNativeDataWrap)
    }

    fun loadResultNAd() {
        loadAd(resultDataWrap)
    }

    fun loadConnectionAd() {
        loadAd(connectDataWrap)
    }

    fun loadBackAd() {
        loadAd(backDataWrap)
    }

    private fun loadAd(dataWrap: AdDataWrap) {
        if (dataWrap.isLoading) {
            return
        }
        if (dataWrap.data != null) {
            adLogI("have cache ad-->${dataWrap.name}")
            return
        }
        val iterator1 = dataWrap.getSpaceConfigIterator()
        if (iterator1.hasNext()) {
            dataWrap.isLoading = true
            loadAd(iterator1, dataWrap)
        } else {
            adLogE("loadAd errr null config")
        }
    }

    private fun loadAd(iterator1: Iterator<CP1ConBean>, dataWrap: AdDataWrap) {
        val con = iterator1.next()
        adLogI("loadAd -->${dataWrap.name} ---${con.cp1_pro}---")
        adImpl.loadAd(con.cp1_t, con.cp1_id, {
            adLogE("loadAd Failed-->${dataWrap.name} ---${con.cp1_pro}---")
            if (iterator1.hasNext()) {
                loadAd(iterator1, dataWrap)
            } else {
                if (dataWrap.isNeedRetryLoadAd()) {
                    val iterator2 = splashDataWrap.getSpaceConfigIterator()
                    if (iterator2.hasNext()) {
                        loadAd(iterator2, dataWrap)
                    } else {
                        dataWrap.isLoading = false
                    }
                } else {
                    dataWrap.isLoading = false
                }
            }
        }, {
            adLogI("loadAd Success-->${dataWrap.name} ---${con.cp1_pro}---")
            dataWrap.data = it
            dataWrap.isLoading = false
        })
    }

    fun showSplashAd(
        activity: Activity,
        closeAd: () -> Unit,
    ): Boolean {
        return showAd(activity, splashDataWrap, closeAd, { })
    }

    fun showFileControlAd(
        activity: Activity,
        closeAd: () -> Unit,
    ): Boolean {
        return showAd(activity, addFileDataWrap, closeAd, {})
    }

    fun showFilterAd(
        activity: Activity,
        closeAd: () -> Unit,
    ): Boolean {
        return showAd(activity, filterDataWrap, closeAd, {})
    }

    fun showBackAd(
        activity: Activity,
        closeAd: () -> Unit,
    ): Boolean {
        return showAd(activity, backDataWrap, closeAd, {})
    }

    fun showConnectionAd(
        activity: Activity,
        closeAd: () -> Unit,
    ): Boolean {
        return showAd(activity, connectDataWrap, closeAd, {})
    }

    fun showHomeNAd(): NativeAd? {
        val data = homeNativeDataWrap.removeData()
        if (data is NativeAd) {
            adLogI("showAd ---${homeNativeDataWrap.name}")
            return data
        }
        return null
    }

    fun showResultNAd(): NativeAd? {
        val data = resultDataWrap.removeData()
        if (data is NativeAd) {
            adLogI("showAd ---${resultDataWrap.name}")
            return data
        }
        return null
    }

    private fun showAd(
        activity: Activity,
        dataWrap: AdDataWrap,
        closeAd: () -> Unit,
        showAd: () -> Unit
    ): Boolean {
        val callback = MyFullScreenContentCallback(closeAd, showAd)
        when (val data = dataWrap.removeData()) {
            is InterstitialAd -> {
                adImpl.showIAd(activity, data, callback)
            }
            is AppOpenAd -> adImpl.showOpenAd(activity, data, callback)
            else -> return false
        }
        adLogI("showAd ---> ${dataWrap.name}")
        return true
    }
}