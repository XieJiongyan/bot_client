package com.example.bot_client_v2.ui.home.placeholder

import android.annotation.SuppressLint
import android.util.Log
import com.example.bot_client_v2.source.MySocket
import com.example.bot_client_v2.ui.home.ClockRecyclerViewAdapter
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import com.soywiz.klock.DateTimeTz
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception
import java.util.ArrayList
import dev.inmo.krontab.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ClockContent {
    private const val TAG = "Jog.ClockContent"
    /**
     * An array of sample (placeholder) items.
     */
    val SHOW_ITEMS: MutableList<ClockShowItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    private val COUNT = 0

    init {
        // Add some sample items.
//        for (i in 1..COUNT) {
//            addItem(createPlaceholderItem())
//        }
    }

    private var clockRecyclerViewAdapter: ClockRecyclerViewAdapter? = null
    fun setRecyclerViewAdapter(_clockRecyclerViewAdapter: ClockRecyclerViewAdapter) {
        clockRecyclerViewAdapter = _clockRecyclerViewAdapter
    }

    private fun addItem(showItem: ClockShowItem) {
        SHOW_ITEMS.add(showItem)
    }

    private fun createPlaceholderItem(): ClockShowItem {
        return ClockShowItem("07:30", "上午", "Bot1 | \" 30 7 * * *\"", false)
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class ClockShowItem(val halfTime: String, val morningOrAfternoon: String, val comment: String, var isActive: Boolean) {
        override fun toString(): String = halfTime + " " + morningOrAfternoon + " " +
                comment + " " + isActive.toString()
    }

    @Serializable
    data class ClockInputItem(
        val cron: String,
        val text: String,
        @SerialName("is_active") val isActive: Boolean
    ) {
        override fun toString(): String {
            return "($cron, $text, $isActive)"
        }
    }
    @Serializable
    data class DeviceData(
        val name: String,
        var clocks: Array<ClockInputItem>
    )
    {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DeviceData

            if (!clocks.contentEquals(other.clocks)) return false

            return true
        }

        override fun hashCode(): Int {
            return clocks.contentHashCode()
        }

        override fun toString(): String {
            var rev: String = "Device: $name"
            for (s in clocks) {
                rev += " $s"
            }
            return rev
        }
    }

    fun updateClockInfo(inputStruct: MySocket.NetStruct) {
     try {
         if (inputStruct.options?.get(0) == "total") {
             val clientData: Map<String, DeviceData> =
                 inputStruct.extras?.let { Json.decodeFromString<Map<String, DeviceData>>(it) }!!
             LogContent.addLog(TAG, "get clientClocks: $clientData")
             CoroutineScope(Dispatchers.Default).launch {
                 setItems(clientData)
             }
         }
     } catch (e: Exception) {
         Log.i(TAG, "Error updateClockInfo: $e")
     }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun setItems(clientData: Map<String, DeviceData>) {
        SHOW_ITEMS.clear()
        for (device in clientData) {
            val deviceData: DeviceData = device.value
            for (clock in deviceData.clocks) {
                val s: KronSchedulerTz = createSimpleScheduler(clock.cron, 8 * 60)

                val nextTimeTz: DateTimeTz = s.next(DateTimeTz.nowLocal())!!

                val halfTime: String = nextTimeTz.format("hh:mm")
                val morningOrAfternoon: String = if (nextTimeTz.hours >= 12) {
                    "下午"
                } else {
                    "上午"
                }

                val nextSpanSeconds: Int = (nextTimeTz - DateTimeTz.nowLocal()).seconds.toInt()
                val nextSpan: String = if (nextSpanSeconds < 3600) {
                    "${nextSpanSeconds / 60} 分钟后"
                } else if (nextSpanSeconds < 24 * 3600) {
                    "${nextSpanSeconds / 3600} 小时 ${(nextSpanSeconds % 3600) / 60} 分钟后"
                } else {
                    "${nextSpanSeconds / 24 / 3600} 天 ${(nextSpanSeconds % (3600 * 24)) / 3600} 小时后"
                }
                val comment = "${deviceData.name} | $nextSpan"

                val clockShowInputItem =
                    ClockShowItem(halfTime, morningOrAfternoon, comment, clock.isActive)

                //TODO: 这里需要加一个排序
                SHOW_ITEMS.add(clockShowInputItem)
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            clockRecyclerViewAdapter?.notifyDataSetChanged()
        }
    }
}