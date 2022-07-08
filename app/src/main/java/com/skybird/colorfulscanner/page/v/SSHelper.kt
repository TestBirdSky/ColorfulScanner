package com.skybird.colorfulscanner.page.v

import android.content.Context
import android.os.RemoteException
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.aidl.TrafficStats
import com.github.shadowsocks.bg.BaseService

/**
 * Date：2022/7/8
 * Describe:
 */
class SSHelper(val stateChange: (state: BaseService.State) -> Unit) :
    ShadowsocksConnection.Callback {
    private val connection = ShadowsocksConnection(true)
    private lateinit var context: Context

    fun connect(context: Context) {
        this.context = context
        connection.connect(context, this)
    }

    fun onStart() {
        connection.bandwidthTimeout = 500
    }

    fun onStop() {
        connection.bandwidthTimeout = 0
    }

    fun disconnect() {
        connection.disconnect(context)
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        stateChange.invoke(state)
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        try {
            stateChange.invoke(BaseService.State.values()[service.state])
        } catch (_: RemoteException) {
            stateChange.invoke(BaseService.State.Idle)
        }
    }

    override fun onBinderDied() {
        connection.disconnect(context)
        connection.connect(context, this)
    }

    override fun onServiceDisconnected() = stateChange.invoke(BaseService.State.Idle)

    var trafficInfo: (stats: TrafficStats) -> Unit = {}

    override fun trafficUpdated(profileId: Long, stats: TrafficStats) {
        super.trafficUpdated(profileId, stats)
        trafficInfo.invoke(stats)
    }
}