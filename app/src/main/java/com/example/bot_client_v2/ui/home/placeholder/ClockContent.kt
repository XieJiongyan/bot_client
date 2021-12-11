package com.example.bot_client_v2.ui.home.placeholder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.ArrayList

object ClockContent {

    /**
     * An array of sample (placeholder) items.
     */
    val SHOW_ITEMS: MutableList<ClockShowItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createPlaceholderItem())
        }
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
    data class ClientClocks(
        @SerialName("client_id") val clientId: Int,
        var clocks: Array<ClockInputItem>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ClientClocks

            if (!clocks.contentEquals(other.clocks)) return false

            return true
        }

        override fun hashCode(): Int {
            return clocks.contentHashCode()
        }

        override fun toString(): String {
            var rev: String = "Client: $clientId"
            for (s in clocks) {
                rev += " $s"
            }
            return rev
        }
    }
}