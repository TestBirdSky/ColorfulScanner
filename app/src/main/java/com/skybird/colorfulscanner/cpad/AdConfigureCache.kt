package com.skybird.colorfulscanner.cpad

import com.google.gson.Gson
import com.skybird.colorfulscanner.bean.CP1AdConListBean
import com.skybird.colorfulscanner.bean.CP1ConBean
import com.skybird.colorfulscanner.bean.CP1VAdConListBean
import com.skybird.colorfulscanner.utils.ConfigureManager

/**
 * Date：2022/7/12
 * Describe:
 */
object AdConfigureCache {
    private var adCListBean: CP1AdConListBean? = null
    private var adCVListBean: CP1VAdConListBean? = null

    fun loadAdCList() {
        adCListBean =
            Gson().fromJson(ConfigureManager.getAdc1ListStr(), CP1AdConListBean::class.java)

    }

    fun loadAdCVListBean() {
        adCVListBean =
            Gson().fromJson(ConfigureManager.getAdc1VListStr(), CP1VAdConListBean::class.java)
    }

    private fun getAdCVListBean(): CP1VAdConListBean? {
        if (adCVListBean == null) {
            loadAdCVListBean()
        }
        return adCVListBean
    }

    fun getAdCBySpace(space: Space): ArrayList<CP1ConBean> {
        if (adCListBean == null) {
            loadAdCList()
        }
        val list = when (space) {
            Space.OPEN -> adCListBean?.cp1_sp ?: arrayListOf()
            Space.FILE -> adCListBean?.cp1_a_f_i ?: arrayListOf()
            Space.FILTER -> adCListBean?.cp1_filter_i ?: arrayListOf()
            Space.HOME -> getAdCVListBean()?.cp1_h_n ?: arrayListOf()
            Space.RESULT -> getAdCVListBean()?.cp1_r_n ?: arrayListOf()
            Space.CONNECT -> getAdCVListBean()?.cp1_ction_i ?: arrayListOf()
            Space.BACK -> getAdCVListBean()?.cp1_b_i ?: arrayListOf()
        }
        list.sortWith { o1, o2 -> o2.cp1_pro - o1.cp1_pro }
        return list
    }
}