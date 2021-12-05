package com.example.bot_client_v2.ui.log

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.bot_client_v2.databinding.FragmentLoggBinding

import com.example.bot_client_v2.ui.log.placeholder.LogContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private var values: MutableList<PlaceholderItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentLoggBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentLoggBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.logItemNumber
        val contentView: TextView = binding.logItemContent

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    public fun addItem(item: PlaceholderItem) {
        values.add(item)
        notifyItemInserted(itemCount)
        notifyItemRangeInserted(itemCount, itemCount)
    }
}