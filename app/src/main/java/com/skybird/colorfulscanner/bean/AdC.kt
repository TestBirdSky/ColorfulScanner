package com.skybird.colorfulscanner.bean

/**
 * Date：2022/7/12
 * Describe:
 */
data class CP1AdConListBean(val cp1_sp: ArrayList<CP1ConBean>,val cp1_a_f_i: ArrayList<CP1ConBean>,val cp1_filter_i: ArrayList<CP1ConBean>)
data class CP1ConBean(
    val cp1_t: String,
    val cp1_pro: Int,
    val cp1_p: String,
    val cp1_id: String,
)