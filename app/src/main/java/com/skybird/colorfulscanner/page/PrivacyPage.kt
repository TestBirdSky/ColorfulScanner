package com.skybird.colorfulscanner.page

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.*
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.AcPrivacyBinding
import java.lang.Exception

/**
 * Date：2022/7/4
 * Describe:
 */
class PrivacyPage : BaseDataBindingAc<AcPrivacyBinding>() {

    override fun layoutId() = R.layout.ac_privacy

    override fun initUI() {
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    override fun initData() {
        val url = getString(R.string.privacy_url)
        binding.run {
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    try {
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                            return true
                        }
                    } catch (e: Exception) {
                        return false
                    }
                    webView.loadUrl(url)
                    return super.shouldOverrideUrlLoading(webView, url)
                }
            }
            webView.loadUrl(url)
        }
    }

    override fun onDestroy() {
        binding.run {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView.clearHistory()
            (webView.parent as ViewGroup).removeView(webView)
            webView.destroy()
        }
        super.onDestroy()
    }
}