package com.skybird.colorfulscanner

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.skybird.colorfulscanner.utils.LogCSI

/**
 * Date：2022/6/30
 * Describe:
 */
class CSACL : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        LogCSI("onActivityCreated activity==$activity")
    }

    override fun onActivityStarted(activity: Activity) {
        LogCSI("onActivityStarted activity==$activity")
    }

    override fun onActivityResumed(activity: Activity) {
        LogCSI("onActivityResumed activity==$activity")
    }

    override fun onActivityPaused(activity: Activity) {
        LogCSI("onActivityPaused activity==$activity")
    }

    override fun onActivityStopped(activity: Activity) {
        LogCSI("onActivityStopped activity==$activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        LogCSI("onActivitySaveInstanceState activity==$activity")
    }

    override fun onActivityDestroyed(activity: Activity) {
        LogCSI("onActivityDestroyed activity==$activity")
    }
}