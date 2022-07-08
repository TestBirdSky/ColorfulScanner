package com.skybird.colorfulscanner

import android.app.ActivityManager
import android.app.Application
import android.os.Process
import com.github.shadowsocks.Core
import com.skybird.colorfulscanner.page.StartUpActivity

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
}