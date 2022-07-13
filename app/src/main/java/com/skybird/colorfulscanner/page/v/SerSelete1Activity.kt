package com.skybird.colorfulscanner.page.v

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.shadowsocks.bg.BaseService
import com.google.gson.Gson
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.bean.SListBean
import com.skybird.colorfulscanner.cpad.CPAdUtils
import com.skybird.colorfulscanner.databinding.ActivitySerS1Binding
import com.skybird.colorfulscanner.dialog.DisconnectedTipsDialog
import com.skybird.colorfulscanner.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Date：2022/7/8
 * Describe:
 */
class SerSelete1Activity : BaseDataBindingAc<ActivitySerS1Binding>() {
    private val adapter by lazy {
        SerAdapter {
            if (CSApp.mApp.isConnectedV) {
                if (it.name != getCurSelectedName()) {
                    DisconnectedTipsDialog({
                        setResult(it)
                    }, {
                        reset()
                    }).show(supportFragmentManager, "---")
                }
            } else {
                if (it.name != getCurSelectedName()) {
                    setResult(it)
                }
            }
        }
    }

    private fun setResult(bean: SerUiBean) {
        setResult(RESULT_OK, Intent().apply {
            putExtra("selectedName", bean.name)
            putExtra("selectedNative", CSUtils.getCircleNIcon(bean.native))
        })
        finish()
    }

    private fun reset() {
        adapter.reset()
    }

    override fun layoutId() = R.layout.activity_ser_s1

    override fun initUI() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            rv.layoutManager = LinearLayoutManager(this@SerSelete1Activity)
            rv.adapter = adapter
        }
    }

    override fun initData() {
        lifecycleScope.launch {
            try {
                val serList = withContext(Dispatchers.IO) {
                    (Gson().fromJson(
                        ConfigureManager.getSer1ListStr(),
                        SListBean::class.java
                    ) as SListBean).cp_t_s
                }
                adapter.currentList = (DataConversionUtils.serBeanListToSerUiBean(serList))
            } catch (e: Exception) {
                adapter.currentList =
                    (arrayListOf(SerUiBean(DEFAULT_SERVER_NAME, DEFAULT_SERVER_NAME)))
            }
        }
        CPAdUtils.loadBackAd()
    }

    override fun onBackPressed() {
        if (!CPAdUtils.showBackAd(this) {
                finish()
            }) {
            super.onBackPressed()
        }
    }
}