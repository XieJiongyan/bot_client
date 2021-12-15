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
    fun deleteItem(deviceId: String, clockPlace: Int) {
        devices[deviceId]?.clocks?.removeAt(clockPlace)
    }

    fun addItem(deviceId: String, clockItemData: ClockItemData) {
        devices[deviceId]?.clocks?.add(clockItemData)
    }

    fun setItem(deviceId: String, clockPlace: Int, clockItemData: ClockItemData) {
        devices[deviceId]?.clocks?.set(clockPlace, clockItemData)
    }

    fun get(deviceId: String, clockPlace: Int): ClockItemData?{
        return devices[deviceId]?.clocks?.get(clockPlace)
    }
}