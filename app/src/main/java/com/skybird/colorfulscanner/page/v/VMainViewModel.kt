package com.skybird.colorfulscanner.page.v

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.shadowsocks.Core
import com.google.gson.Gson
import com.skybird.colorfulscanner.CSApp
import com.skybird.colorfulscanner.bean.FastBean
import com.skybird.colorfulscanner.bean.SListBean
import com.skybird.colorfulscanner.bean.SerBean
import com.skybird.colorfulscanner.utils.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.util.*
import kotlin.math.roundToInt

/**
 * Date：2022/7/8
 * Describe:
 */
class VMainViewModel : ViewModel() {
    private val mApp = CSApp.mApp
    private val gson by lazy { Gson() }
    private val fastCList by lazy {
        (gson.fromJson(
            ConfigureManager.getFastCity1Str(),
            FastBean::class.java
        )).cpf
    }
    private val serList by lazy {
        (gson.fromJson(
            ConfigureManager.getSer1ListStr(),
            SListBean::class.java
        ) as SListBean).cp_t_s
    }
    var curSerName = MutableLiveData(getCurSelectedName())
    var curCircleNativeIcon = MutableLiveData(getCurSelectedIcon())
    var curConnectedTimeStr = MutableLiveData("00:00:00")
    private var isChoiceFinish = false

    fun choiceSer() {
        isChoiceFinish = false
        val name = curSerName.value
        if (name == DEFAULT_SERVER_NAME) {
            selFastSer()
        } else {
            for (bean in serList) {
                if (name == bean.getName()) {
                    CSUtils.setProfile(bean)
                    isChoiceFinish = true
                    break
                }
            }
        }
    }

    private fun selFastSer() {
        viewModelScope.launch {
            val list = arrayListOf<SerBean>()
            for (fc in fastCList) {
                for (serBean in serList) {
                    if (fc == serBean.cp_t_ci) {
                        list.add(serBean)
                        break
                    }
                }
            }
            val size = list.size
            if (size == 0) {
                return@launch
            }
            val beans = arrayListOf<SerBean>()
            val time = System.currentTimeMillis()
            val jobs = arrayListOf<Job>()
            for (i: Int in 0 until list.size) {
                jobs.add(viewModelScope.launch(Dispatchers.IO) {
                    val delay = delayTest(list[i].cp_t_ip)
                    LogCSI("${list[i].cp_t_ip}----delay=$delay")
                    val serBean: SerBean = list[i]
                    serBean.delay = delay
                    beans.add(serBean)
                })
            }
            withContext(Dispatchers.Default) {
                while (beans.size < list.size && System.currentTimeMillis() - time < 2000) {
                    delay(200)
                }
            }
            jobs.forEach { it.cancel() }
            LogCSI("size->${beans.size}")
            beans.sortBy { it.delay }
            val fastBeans = arrayListOf<SerBean>()
            for (i: Int in 0 until beans.size) {
                if (i < 3) {
                    fastBeans.add(beans[i])
                } else {
                    break
                }
            }
            val sizeFb = fastBeans.size
            var randomIndex = 0
            if (sizeFb > 1) {
                randomIndex = Random().nextInt(sizeFb)
            }
            val fastBean = fastBeans[randomIndex]
            CSUtils.setProfile(fastBean)
            isChoiceFinish = true
        }
    }

    private suspend fun delayTest(ip: String): Int {
        var delay = Int.MAX_VALUE
        val count = 1
        val timeout = 1
        val cmd = "/system/bin/ping -c $count -w $timeout $ip"
        return withContext(Dispatchers.IO) {
            val r = ping(cmd)
            if (r != null) {
                try {
                    val index: Int = r.indexOf("min/avg/max/mdev")
                    if (index != -1) {
                        val tempInfo: String = r.substring(index + 19)
                        val temps = tempInfo.split("/".toRegex()).toTypedArray()
                        delay = temps[0].toFloat().roundToInt()//min
                    }
                } catch (e: Exception) {

                }
            }
            delay
        }
    }

    private fun ping(cmd: String): String? {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(cmd) //执行ping指令
            val inputStream = process!!.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String?
            while (null != reader.readLine().also { line = it }) {
                sb.append(line)
                sb.append("\n")
            }
            reader.close()
            inputStream.close()
            return sb.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            process?.destroy()
        }
        return null
    }

    private var count = 0L
    private var isStopped = true
    fun connected() {
        if (isStopped) {
            isStopped = false
            mApp.isConnectedV = true
            mApp.connectedName = curSerName.value ?: DEFAULT_SERVER_NAME
            curCircleNativeIcon.value?.let {
                mApp.connectedIcon = it
            }
            if (mApp.connectedTime == -1L) {
                mApp.connectedTime = System.currentTimeMillis()
            }
            count = ((System.currentTimeMillis() - mApp.connectedTime) / 1000)
            viewModelScope.launch {
                putCurSelectedName(mApp.connectedName)
                putCurSelectedIcon(mApp.connectedIcon)
                while (!isStopped) {
                    curConnectedTimeStr.value = CSUtils.intTimeToStr(count)
                    delay(1000)
                    count++
                }
            }
        }
    }

    fun stopped() {
        count = 0
        isStopped = true
        mApp.isConnectedV = false
        curConnectedTimeStr.value = CSUtils.intTimeToStr(count)
        mApp.connectedTime = -1L
    }

    fun reset() {
        curSerName.value = getCurSelectedName()
        curCircleNativeIcon.value = getCurSelectedIcon()

    }

    fun toggle(isConnection: Boolean) {
        if (isConnection) {
            viewModelScope.launch {
                while (!isChoiceFinish) {
                    delay(200)
                }
                Core.startService()
            }
        } else {
            Core.stopService()
        }
    }
}