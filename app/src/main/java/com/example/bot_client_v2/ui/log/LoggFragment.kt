package com.example.bot_client_v2.ui.log

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bot_client_v2.R
import com.example.bot_client_v2.ui.log.placeholder.LogContent

/**
 * A fragment representing a list of Items.
 */
class LoggFragment : Fragment() {

    private val TAG: String = "Jog.LoggFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_logg_list, container, false) as RecyclerView

        // Set the adapter
        view.let {
            it.layoutManager = LinearLayoutManager(context)
            LogContent.addLog(TAG, "init logFragment.onCreateView")
            LogContent.setRecyclerView(it)
        }
        return view
    }

}