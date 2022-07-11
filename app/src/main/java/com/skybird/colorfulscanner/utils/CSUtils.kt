package com.skybird.colorfulscanner.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.DataStore
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.bean.SerBean
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder


/**
 * Date：2022/7/1
 * Describe:
 */
object CSUtils {

    fun showSoftInput(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun getPackInfo(): PackageInfo {
        val pm = CSApp.mApp.packageManager
        return pm.getPackageInfo(CSApp.mApp.packageName, PackageManager.GET_ACTIVITIES)
    }

    fun getCircleNIcon(nati: String): Int {
        return R.drawable.ic_default_n
    }

    fun setProfile(server: SerBean) {
        var profile = ProfileManager.getProfile(DataStore.profileId)
        if (profile == null) {
            profile = ProfileManager.createProfile(Profile())
            profile.name = CSApp.mApp.getString(R.string.app_name)
            DataStore.profileId = profile.id
        }
        LogCSI("switchProfile: $server  --$profile  --${DataStore.profileId}")
        profile.host = server.cp_t_ip
        profile.remotePort = server.cp_t_pt
        profile.password = server.cp_t_pwd
        profile.method = server.cp_t_m
        ProfileManager.updateProfile(profile)
    }


    fun intTimeToStr(time: Long): String {
        return "${String.format("%02d", time / 3600)}:${
            String.format(
                "%02d",
                time % 3600 / 60
            )
        }:${String.format("%02d", time % 60)}"
    }

    fun assetGsonFileStr(context: Context, fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            //获取assets资源管理器
            val assetManager: AssetManager = context.getAssets()
            //通过管理器打开文件并读取
            val bf = BufferedReader(
                InputStreamReader(
                    assetManager.open(fileName)
                )
            )
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

}