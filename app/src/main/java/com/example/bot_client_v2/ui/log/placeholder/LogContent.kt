package com.example.bot_client_v2.ui.log.placeholder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.bot_client_v2.ui.log.MyItemRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
object LogContent {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<PlaceholderItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, PlaceholderItem> = HashMap()

    private val TAG: String = "Jog.PlaceHolderContent"
    private var myItemRecyclerViewAdapter: MyItemRecyclerViewAdapter? = null
    private var mRecyclerView: RecyclerView? = null

    fun addLog(tag:String, text: String) {
        //TODO: 需增加 notifyItemChange
        addItem(PlaceholderItem(ITEMS.size.toString(), text, "so, what is detail"))
        Log.i(tag, text)
    }

    private fun addItem(item: PlaceholderItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
        try {
            mRecyclerView?.post {
                myItemRecyclerViewAdapter?.notifyItemInserted(ITEMS.size)
                mRecyclerView?.scrollToPosition(ITEMS.size - 1)
            }
        } catch (e: Exception) {
            Log.i(TAG, e.toString())
        }
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class PlaceholderItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }

    fun setRecyclerView(recyclerView: RecyclerView) {
        val adapter = MyItemRecyclerViewAdapter(ITEMS)
        myItemRecyclerViewAdapter = adapter
        mRecyclerView = recyclerView
        recyclerView.adapter = adapter
    }
}