package com.example.bot_client_v2.source

import android.util.Log
import com.example.bot_client_v2.MainActivity
import com.example.bot_client_v2.ui.home.placeholder.ClockContent
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception
import java.time.LocalDateTime

object NetClock {
    private val mySocket: MySocket = MySocket(8100)
    private const val TAG: String = "Jog.NetClock"
    private var mainActivity: MainActivity? = null
    private var lastReceiveNetTime: LocalDateTime = LocalDateTime.now()

    private fun establishConnect() {
        mySocket.setLoginFun { login() }
        mySocket.establish_connection()
    }
    private suspend fun listen() {
        while(true) {
            val inputStruct: MySocket.NetStruct? = mySocket.read()
            if (inputStruct == null) {
                delay(5000L)
                continue
            }
            LogContent.addLog(TAG, "Receive: $inputStruct")
            lastReceiveNetTime = LocalDateTime.now()
            withContext(Dispatchers.Main) {
                mainActivity?.changeLoginText(true)
            }
            withContext(Dispatchers.Default) {
                when(inputStruct.command) {
                    "clock" -> ClockContent.updateClockInfo(inputStruct)
                }
            }
        }
    }
    private fun login() {
        try {
            val strLogin: String = """
                {
                    "content": [
                        "login", 
                        "client",
                        "1",
                        "111111"
                    ]
                }
            """.trimIndent()
            val bytesLogin: ByteArray = strLogin.encodeToByteArray()
            LogContent.addLog(TAG, strLogin)
            mySocket.write(bytesLogin)
        } catch (e: Exception) {
            Log.i(TAG, "Error load file: " + e.toString())
        }
    }
    fun autoLogin(__mainActivity: MainActivity) = CoroutineScope(Dispatchers.IO).launch{
        mainActivity = __mainActivity

        establishConnect()
        mySocket.ww()

        CoroutineScope(Dispatchers.IO).launch {
            listen()
        }
        CoroutineScope(Dispatchers.IO).launch { showIsConnected() }
    }


    private suspend fun showIsConnected() {
        while (true) {
            delay(5000L)
            if (LocalDateTime.now().minusSeconds(5L).isAfter(lastReceiveNetTime)) {
                withContext(Dispatchers.Main) {
                    mainActivity?.changeLoginText(false)
                }
            }
        }
    }

    init {
        ClockContent.mySocket = mySocket
    }
}