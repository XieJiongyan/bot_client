package com.example.bot_client_v2.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.bot_client_v2.R
import com.example.bot_client_v2.ui.home.placeholder.ClockContent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception

/**
 * A fragment representing a list of Items.
 */
class ClockFragment : Fragment() {

    private var columnCount = 1
    private val TAG = "Jog.ClockFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clock_list, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.list)
        // Set the adapter
        with(recyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            val buttonOnDrawable = ContextCompat.getDrawable(context, R.drawable.ic_clock_button_on)
            val buttonOffDrawable = ContextCompat.getDrawable(context, R.drawable.ic_clock_button_off)
            try {
                val cAdapter = ClockRecyclerViewAdapter(
                    ClockContent.SHOW_ITEMS,
                    buttonOnDrawable!!,
                    buttonOffDrawable!!
                )
                adapter = cAdapter
                ClockContent.setRecyclerViewAdapter(cAdapter)
            } catch (e: Exception) {
                Log.i(TAG, "error find drawable $e")
            }
        }

        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        try {
            fab.setOnClickListener {
                val intent = Intent(context, ClockDetailActivity()::class.java).also {
                    it.putExtra("isNew", true)
                    it.putExtra("clockIndex", 0)
                }
                context?.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.i(TAG, "error set listener: $e")
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        ClockContent.data?.let { ClockContent.refreshAndSync(it) }
    }
    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ClockFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}