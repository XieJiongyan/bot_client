package com.example.bot_client_v2.ui.home

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.bot_client_v2.databinding.FragmentClockBinding
import com.example.bot_client_v2.ui.home.placeholder.ClockContent

import com.example.bot_client_v2.ui.home.placeholder.ClockContent.ClockShowItem
import com.example.bot_client_v2.ui.log.placeholder.LogContent

/**
 * [RecyclerView.Adapter] that can display a [ClockShowItem].
 * TODO: Replace the implementation with code for your data type.
 */
class ClockRecyclerViewAdapter(
    private val values: List<ClockShowItem>,
    private val buttonOnBackGround: Drawable,
    private val buttonOffBackGround: Drawable
) : RecyclerView.Adapter<ClockRecyclerViewAdapter.ViewHolder>() {

    private val tag: String = "Jog.ClockRecyclerViewAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentClockBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.halftimeView.text = item.halfTime
        holder.morningView.text = item.morningOrAfternoon
        holder.commentView.text = item.comment
        if (item.isActive) {
            holder.buttonView.background = buttonOnBackGround
        } else {
            holder.buttonView.background = buttonOffBackGround
        }
        holder.buttonView.setOnClickListener {
            ClockContent.switchActive(position)
        }
        holder.clockItem.setOnClickListener {
            LogContent.addLog(tag, "click $position clock, ready to start intent")
            val intent = Intent(holder.clockItem.context, ClockDetailActivity()::class.java).also {
                it.putExtra("isNew", false)
                it.putExtra("clockIndex", position)
            }
            holder.clockItem.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentClockBinding) : RecyclerView.ViewHolder(binding.root) {
        val halftimeView: TextView = binding.clockItemHalftime
        val morningView: TextView = binding.clockItemMorning
        val commentView: TextView = binding.clockItemComment
        val buttonView: ImageButton = binding.clockItemButton
        val clockItem: View = binding.clockItem
        override fun toString(): String {
            return super.toString() + " '" + morningView.text + "'"
        }
    }

}