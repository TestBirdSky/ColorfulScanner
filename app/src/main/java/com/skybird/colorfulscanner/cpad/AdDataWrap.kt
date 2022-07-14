package com.skybird.colorfulscanner.cpad

import com.skybird.colorfulscanner.bean.CP1ConBean

/**
 * Date：2022/7/12
 * Describe:
 */
class AdDataWrap(
    private val space: Space,
    private val reTryTimeOut: Long = -1L,
    private val reTryMaxNum: Int = 0 //超时间内 重新尝试请求的个数
) {

    val name = space.sName
    private var loadingTime = 0L
    private var reTryNum = 0
    var isLoading: Boolean = false
        set(value) {
            field = value
            if (reTryMaxNum > 0 && field) {
                reTryNum = 0
                loadingTime = System.currentTimeMillis()
            }
        }

    var data: Any? = null
        get() {
            if (!isAvaiable()) {
                field = null
            }
            return field
        }
        set(value) {
            field = value
            if (field != null) {
                saveDataTime = System.currentTimeMillis()
                loadingTime = 0L
            }
        }

    fun removeData(): Any? {
        val d = data
        data = null
        return d
    }

    private var saveDataTime = -1L

    private fun isAvaiable(): Boolean {
        if (System.currentTimeMillis() - saveDataTime < 1000 * 60 * 60) {
            return true
        }
        return false
    }

    fun getSpaceConfigIterator(): Iterator<CP1ConBean> {
        return AdConfigureCache.getAdCBySpace(space).iterator()
    }

    fun isNeedRetryLoadAd(): Boolean {
        if (reTryMaxNum > 0
            && System.currentTimeMillis() - loadingTime < reTryTimeOut
            && reTryNum < reTryMaxNum
        ) {
            reTryNum++
            return true
        }
        return false
    }
}