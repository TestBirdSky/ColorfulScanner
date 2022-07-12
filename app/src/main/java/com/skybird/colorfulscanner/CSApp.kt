package com.skybird.colorfulscanner

import android.app.ActivityManager
import android.app.Application
import android.os.Process
import com.github.shadowsocks.Core
import com.google.android.gms.ads.MobileAds
import com.skybird.colorfulscanner.page.StartUpActivity
import com.skybird.colorfulscanner.utils.ConfigureManager
import com.skybird.colorfulscanner.utils.DEFAULT_SERVER_NAME

/**
 * Date：2022/6/29
 * Describe:
 */
class CSApp : Application() {
    companion object {
        lateinit var mApp: CSApp
        var isAppResume = false
    }

    override fun onCreate() {
        super.onCreate()
        Core.init(this, StartUpActivity::class)
        if (!isBg()) {
            mApp = this
            registerActivityLifecycleCallbacks(CSACL())
            ConfigureManager.loadRemoteConfigure()
            MobileAds.initialize(this)
        }
    }

    private fun isBg(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == Process.myPid()) {
                return processInfo.processName.contains(":bg")
            }
        }
        return false
    }

    var connectedName: String = DEFAULT_SERVER_NAME
    var connectedTime: Long = -1L
    var connectedIcon: Int = R.drawable.ic_default_n
    var isConnectedV = false
    var originScaledDensity = -1f
}