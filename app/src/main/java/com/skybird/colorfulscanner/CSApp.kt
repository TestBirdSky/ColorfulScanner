package com.skybird.colorfulscanner

import android.app.Application

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
        mApp = this
        registerActivityLifecycleCallbacks(CSACL())
    }
}