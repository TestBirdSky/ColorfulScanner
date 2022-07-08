package com.skybird.colorfulscanner

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

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
        Firebase.initialize(this)
        registerActivityLifecycleCallbacks(CSACL())
    }
}