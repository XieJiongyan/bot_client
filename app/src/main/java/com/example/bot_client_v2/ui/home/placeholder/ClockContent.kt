package com.example.bot_client_v2.ui.home.placeholder

import java.util.ArrayList

object ClockContent {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<ClockItem> = ArrayList()

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

    private fun addItem(item: ClockItem) {
        ITEMS.add(item)
    }

    private fun createPlaceholderItem(): ClockItem {
        return ClockItem("07:30", "上午", "Bot1 | \" 30 7 * * *\"", false)
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class ClockItem(val halfTime: String, val morningOrAfternoon: String, val comment: String, var isActive: Boolean) {
        override fun toString(): String = halfTime + " " + morningOrAfternoon + " " +
                comment + " " + isActive.toString()
    }
}