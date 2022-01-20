package com.example.bot_client_v2.ui.home

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import com.example.bot_client_v2.R
import com.example.bot_client_v2.databinding.ActivityClockDetailBinding
import com.example.bot_client_v2.source.ClockItemData
import com.example.bot_client_v2.ui.home.placeholder.ClockContent
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import com.soywiz.klock.DateTimeTz
import dev.inmo.krontab.KronSchedulerTz
import dev.inmo.krontab.createSimpleScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


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
    private var nextAlarmTextView: TextView? = null

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
        nextAlarmTextView = findViewById(R.id.clock_detail_next_alarm)

        cronTextView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                LogContent.addLog(tag, "afterTextChanged")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                LogContent.addLog(tag, "beforeTextChanged")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    changeCronText("$s")
                    val animation: AlphaAnimation = AlphaAnimation(0.1f, 1.0f)
                    animation.duration = 500
                    animation.repeatCount = 0
                    animation.repeatMode = Animation.RESTART
                    nextAlarmTextView?.startAnimation(animation)
                } catch (e: Exception) {
                    Log.e(tag, "o: $e")
                }
            }
        })
        changeCronText(cronTextView.text.toString())

        val cancelButton: Button = findViewById(R.id.clock_detail_cancel_button)
        val confirmButton: Button = findViewById(R.id.clock_detail_confirm_button)
        val deleteButton: Button = findViewById(R.id.clock_detail_delete_button)

        cancelButton.setOnClickListener {
            finish()
        }

        confirmButton.setOnClickListener {
            if (nextAlarmTextView?.text.toString() == getString(R.string.illegal_cron)) {
                Toast.makeText(this, "illegal cron text", Toast.LENGTH_SHORT).show()
                val animation: AlphaAnimation = AlphaAnimation(0.1f, 1.0f)
                animation.duration = 500
                animation.repeatCount = 0
                animation.repeatMode = Animation.RESTART
                nextAlarmTextView?.startAnimation(animation)
                return@setOnClickListener
            }
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

    private fun changeCronText(cron: String) {
        var nextSpan: String = ""
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val s: KronSchedulerTz = createSimpleScheduler(cron, 8 * 60)

                val nextTimeTz: DateTimeTz = s.next(DateTimeTz.nowLocal())!!
                val ts: Long = nextTimeTz.local.unixMillisLong

                val halfTime: String = nextTimeTz.format("hh:mm")
                val morningOrAfternoon: String = if (nextTimeTz.hours >= 12) {
                    "下午"
                } else {
                    "上午"
                }

                val nextSpanSeconds: Int = (nextTimeTz - DateTimeTz.nowLocal()).seconds.toInt()
                val nextSpanPart: String = when {
                    nextSpanSeconds < 60 -> {
                        "不到 1 分钟后"
                    }
                    nextSpanSeconds < 3600 -> {
                        "${nextSpanSeconds / 60} 分钟后"
                    }
                    nextSpanSeconds < 24 * 3600 -> {
                        "${nextSpanSeconds / 3600} 小时 ${(nextSpanSeconds % 3600) / 60} 分钟后"
                    }
                    else -> {
                        val hour: Int = (nextSpanSeconds % (3600 * 24)) / 3600
                        "${nextSpanSeconds / 24 / 3600} 天 $hour 小时后"
                    }
                }
                nextSpan = "闹钟将于 ${nextSpanPart}响铃"
            } catch (e: Exception) {
                Log.e(tag, "wrong cron: $e")
            } finally {
                Log.i(tag, "ready to change")
                if (nextSpan == "") {
                    try {
                        nextAlarmTextView?.text = getString(R.string.illegal_cron)
                        nextAlarmTextView?.setTextColor(Color.parseColor("#ff0000"))
                    } catch (e: Exception) {
                        Log.i(tag, "so why: $e")
                    }
                    Log.i(tag, "oops")
                } else {
                    nextAlarmTextView?.text = nextSpan
                    nextAlarmTextView?.setTextColor(Color.parseColor("#000000"))
                }
            }
        }
    }
}

