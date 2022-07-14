package com.skybird.colorfulscanner

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.ads.AdActivity
import com.skybird.colorfulscanner.CSApp.Companion.isAppResume
import com.skybird.colorfulscanner.page.StartUpActivity
import com.skybird.colorfulscanner.utils.LogCSI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2022/6/30
 * Describe:
 */
class CSACL : Application.ActivityLifecycleCallbacks {
    var num = 0
    var leaveAppTime = -1L
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        LogCSI("onActivityCreated activity==$activity")
    }

    override fun onActivityStarted(activity: Activity) {
        LogCSI("onActivityStarted activity==$activity")
        if (leaveAppTime != -1L && System.currentTimeMillis() - leaveAppTime >= 3000L) {
            leaveAppTime = -1L
            CSApp.isNeedRefreshHomeNAd = true
            if (activity is StartUpActivity) {

            } else {
                activity.toNexAct(StartUpActivity::class.java, Bundle().apply {
                    putBoolean("isHotReboot", true)
                })
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        LogCSI("onActivityResumed activity==$activity")
        isAppResume = true
        leaveAppTime = -1L
        num++
    }

    override fun onActivityPaused(activity: Activity) {
        LogCSI("onActivityPaused activity==$activity")
        num--
        if (num <= 0) {
            isAppResume = false
            leaveAppTime = System.currentTimeMillis()
        }
    }

    override fun onActivityStopped(activity: Activity) {
        LogCSI("onActivityStopped activity==$activity")
        if (activity is AdActivity) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2500L)
                if (leaveAppTime != -1L && (!isAppResume) && !activity.isFinishing) {
                    LogCSI("onActivityStopped finish-->$activity")
                    activity.finish()
                }
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        LogCSI("onActivitySaveInstanceState activity==$activity")
    }

    override fun onActivityDestroyed(activity: Activity) {
        LogCSI("onActivityDestroyed activity==$activity")
    }
}