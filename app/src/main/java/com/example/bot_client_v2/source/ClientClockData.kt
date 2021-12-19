package com.example.bot_client_v2.source

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClockItemData(
    var cron: String,
    var text: String,
    @SerialName("is_active") var isActive: Boolean
)
@Serializable
data class DeviceData(
    var name: String,
    var clocks: MutableList<ClockItemData>
)

@Serializable
data class ClientClockData(
    val devices: Map<String, DeviceData>
) {
    fun deleteItem(deviceId: String, clockIndex: Int) {
        devices[deviceId]?.clocks?.removeAt(clockIndex)
    }

    fun addItem(deviceId: String, clockItemData: ClockItemData) {
        devices[deviceId]?.clocks?.add(clockItemData)
    }

    fun setItem(deviceId: String, clockIndex: Int, clockItemData: ClockItemData) {
        devices[deviceId]?.clocks?.set(clockIndex, clockItemData)
    }

    fun get(deviceId: String, clockIndex: Int): ClockItemData?{
        return devices[deviceId]?.clocks?.get(clockIndex)
    }

    fun getBots(): Array<String> {
        val rev: Array<String> = Array<String>(devices.size) {""}
        var i: Int = 0
        devices.forEach {
            rev[i] = it.value.name
            i++
        }
        return rev
    }

}