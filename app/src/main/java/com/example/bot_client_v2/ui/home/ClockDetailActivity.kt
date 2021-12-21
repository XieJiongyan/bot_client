package com.example.bot_client_v2.ui.home

import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextWatcher
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.example.bot_client_v2.R
import com.example.bot_client_v2.databinding.ActivityClockDetailBinding
import com.example.bot_client_v2.source.ClockItemData
import com.example.bot_client_v2.ui.home.placeholder.ClockContent
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ClockDetailActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var text: String = ""
    private var cron: String = ""
    private var isActive: Boolean = true
    private var bot: String = ""
    private var bots: MutableList<String> = mutableListOf("")
    private var isNew: Boolean = false
    private var clockIndex: Int = 0
    private var arrayAdapter: ArrayAdapter<String>? = null
    private val tag = "Jog.ClockDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_detail)
        isNew = intent.getBooleanExtra("isNew", false)
        clockIndex = intent.getIntExtra("clockIndex", 0)
        obtainProperties()
        setUi()
        setListener()
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
        bot = bots[0]
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

    private fun setListener() {
        val cronTextView: EditText = findViewById(R.id.clock_detail_cron)
        val textTextView: EditText = findViewById(R.id.clock_detail_text)

        val cancelButton: Button = findViewById(R.id.clock_detail_cancel_button)
        val confirmButton: Button = findViewById(R.id.clock_detail_confirm_button)
        val deleteButton: Button = findViewById(R.id.clock_detail_delete_button)

        cancelButton.setOnClickListener {
            finish()
        }

        confirmButton.setOnClickListener {
            LogContent.addLog(tag, "Click Confirm Button")
            obtainNewProperties()
            if (isNew) {
                addClockItem()
            } else {
                setClockItem()
            }
            finish()
        }

        deleteButton.setOnClickListener {
            deleteClockItem()
            finish()
        }
    }

    private fun setClockItem() {
        val clockItemData = ClockItemData(cron, text, isActive)
        val deviceId: String = ClockContent.SHOW_ITEMS[clockIndex].deviceId
        val clockIndex: Int = ClockContent.SHOW_ITEMS[clockIndex].clockIndex

        ClockContent.data?.setItem(deviceId, clockIndex, clockItemData)
    }

    private fun addClockItem() {
        val clockItemData = ClockItemData(cron, text, isActive)
        val deviceId: String = ClockContent.SHOW_ITEMS[clockIndex].deviceId

        ClockContent.data?.addItem(deviceId, clockItemData)
    }

    private fun deleteClockItem() {
        val deviceId: String = ClockContent.SHOW_ITEMS[clockIndex].deviceId
        val clockIndex: Int = ClockContent.SHOW_ITEMS[clockIndex].clockIndex

        ClockContent.data?.deleteItem(deviceId, clockIndex)
    }

    private fun obtainNewProperties() {
        val cronTextView: EditText = findViewById(R.id.clock_detail_cron)
        val textTextView: EditText = findViewById(R.id.clock_detail_text)
        cron = cronTextView.text.toString()
        text = textTextView.text.toString()
    }

}

