package com.example.bot_client_v2.ui.home.placeholder

import android.annotation.SuppressLint
import android.util.Log
import com.example.bot_client_v2.source.ClientClockData
import com.example.bot_client_v2.source.ClockItemData
import com.example.bot_client_v2.source.DeviceData
import com.example.bot_client_v2.source.MySocket
import com.example.bot_client_v2.ui.home.ClockRecyclerViewAdapter
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import com.soywiz.klock.DateTimeTz
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception
import dev.inmo.krontab.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.encodeToString
import java.time.LocalDateTime
import java.util.*

@SuppressLint("NotifyDataSetChanged")
object ClockContent {
    private const val TAG = "Jog.ClockContent"
    /**
     * An array of sample (placeholder) items.
     */
    val SHOW_ITEMS: MutableList<ClockShowItem> = ArrayList()
    var data: ClientClockData? = null
    var mySocket: MySocket? = null

    private var clockRecyclerViewAdapter: ClockRecyclerViewAdapter? = null
    fun setRecyclerViewAdapter(_clockRecyclerViewAdapter: ClockRecyclerViewAdapter) {
        clockRecyclerViewAdapter = _clockRecyclerViewAdapter
    }

    data class ClockShowItem(
            val halfTime: String,
            val morningOrAfternoon: String,
            val comment: String,
            var isActive: Boolean,
            var ts: Long,
            var deviceId: String,
            var clockIndex: Int) {
        override fun toString(): String = halfTime + " " + morningOrAfternoon + " " +
                comment + " " + isActive.toString()
    }
    fun updateClockInfo(inputStruct: MySocket.NetStruct) {
        try {
            if (inputStruct.options?.get(0) == "total") {
                val clientData: ClientClockData =
                    inputStruct.extras?.let { Json.decodeFromString<ClientClockData>(it) }!!
                data = clientData
                LogContent.addLog(TAG, "get clientClocks: $clientData")
                CoroutineScope(Dispatchers.Default).launch {
                    refresh(clientData)
                }
            }
        } catch (e: Exception) {
            Log.i(TAG, "Error updateClockInfo: $e")
        }
    }

    var refreshChan = Channel<Boolean>(1)
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun refresh(clientData: ClientClockData) {
        refreshChan.send(true)
        SHOW_ITEMS.clear()
        for (device in clientData.devices) {
            val deviceData: DeviceData = device.value
            for (clockPlace in deviceData.clocks.indices) {
                val clock: ClockItemData = deviceData.clocks[clockPlace]
                val s: KronSchedulerTz = createSimpleScheduler(clock.cron, 8 * 60)

                val nextTimeTz: DateTimeTz = s.next(DateTimeTz.nowLocal())!!
                val ts: Long = nextTimeTz.local.unixMillisLong

                val halfTime: String = nextTimeTz.format("hh:mm")
                val morningOrAfternoon: String = if (nextTimeTz.hours >= 12) {
                    "下午"
                } else {
                    "上午"
                }

                val nextSpanSeconds: Int = (nextTimeTz - DateTimeTz.nowLocal()).seconds.toInt()
                val nextSpan: String = when {
                    nextSpanSeconds < 60 -> {
                        "不到 1 分钟后"
                    }
                    nextSpanSeconds < 3600 -> {
                        "${nextSpanSeconds / 60} 分钟后"
                    }
                    nextSpanSeconds < 24 * 3600 -> {
                        "${nextSpanSeconds / 3600} 小时 ${(nextSpanSeconds % 3600) / 60} 分钟后"
                    }
                    else -> {
                        val hour: Int = (nextSpanSeconds % (3600 * 24)) / 3600
                        "${nextSpanSeconds / 24 / 3600} 天 $hour 小时后"
                    }
                }
                val comment = "${deviceData.name} | $nextSpan"

                val clockShowInputItem =
                    ClockShowItem(halfTime, morningOrAfternoon, comment, clock.isActive
                        , ts, device.key, clockPlace)

                //TODO: 这里需要加一个排序
                SHOW_ITEMS.add(clockShowInputItem)
            }
        }
        SHOW_ITEMS.sortBy { it.ts }
        CoroutineScope(Dispatchers.Main).launch {
            clockRecyclerViewAdapter?.notifyDataSetChanged()
        }
        refreshChan.receive()
    }

    init {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(60_000L - LocalDateTime.now().second.toLong() * 1000)
                data?.let {
                    refresh(it)
                }
            }
        }
    }

    fun switchActive(position: Int) {
        val deviceId: String = SHOW_ITEMS[position].deviceId
        val clockPlace: Int = SHOW_ITEMS[position].clockIndex

        val clockItemData: ClockItemData? = data?.get(deviceId, clockPlace)
        clockItemData?.let {
            it.isActive = !it.isActive
            data?.setItem(deviceId, clockPlace, it)
        }
        refreshAndSync(data)
    }

    private fun syncToServer(clientData: ClientClockData) {
        val str = Json.encodeToString(clientData)
        val outputStruct = MySocket.NetStruct(
            command = "clock",
            options = arrayOf("post", "all"),
            extras = str
        )
        mySocket?.writeStruct(outputStruct)
    }
    private fun refreshAndSync(clientData: ClientClockData?) {
        clientData?.let {
            CoroutineScope(Dispatchers.Main).launch { refresh(it) }
            CoroutineScope(Dispatchers.IO).launch { syncToServer(it) }
        }
    }
}