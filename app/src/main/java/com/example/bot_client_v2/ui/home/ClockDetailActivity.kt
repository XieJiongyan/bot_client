package com.example.bot_client_v2.ui.home

import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.example.bot_client_v2.R
import com.example.bot_client_v2.databinding.ActivityClockDetailBinding
import com.example.bot_client_v2.source.ClockItemData
import com.example.bot_client_v2.ui.home.placeholder.ClockContent
import com.example.bot_client_v2.ui.log.placeholder.LogContent


class ClockDetailActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var text: String = ""
    private var cron: String = ""
    private var isActive: Boolean = true
    private var bots: MutableList<String> = mutableListOf("")
    private var isNew: Boolean = false
    private var clockIndex: Int = 0
    private var arrayAdapter: ArrayAdapter<String>? = null
    private val tag = "Jog.ClockDetailActivity"

    private lateinit var binding: ActivityClockDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_detail)
        isNew = intent.getBooleanExtra("isNew", false)
        clockIndex = intent.getIntExtra("clockIndex", 0)
        obtainProperties()
        setUi()
    }
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }

    private fun obtainProperties() {
        if (!isNew) {
            val deviceId: String = ClockContent.SHOW_ITEMS[clockIndex].deviceId
            val clockIndex: Int = ClockContent.SHOW_ITEMS[clockIndex].clockIndex
            val clockItemData :ClockItemData = ClockContent.data!!.get(deviceId, clockIndex)!!

            text = clockItemData.text
            cron = clockItemData.cron
            isActive = clockItemData.isActive
        }
        bots = ClockContent.data!!.getBots().toMutableList()
    }

    private fun setUi() {
        if (!isNew) {
            val cronView: TextView = findViewById(R.id.clock_detail_cron)
            val textView: TextView = findViewById(R.id.clock_detail_text)

            cronView.text = cron
            textView.text = text
        }
        arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bots)
        arrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner: Spinner = findViewById(R.id.clock_detail_spinner)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        LogContent.addLog(tag, "choose id: $id")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        LogContent.addLog(tag, "nothing selected")
    }

}

