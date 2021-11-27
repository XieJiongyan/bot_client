package com.example.bot_client_v2.source

import android.util.Log
import com.example.bot_client_v2.MainActivity
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import kotlinx.coroutines.*
import java.lang.Exception
import java.time.LocalDateTime

object NetClock {
    private val mySocket: MySocket = MySocket(8100)
    private val TAG: String = "Jog.NetClock"
    private var mainActivity: MainActivity? = null
    private var lastReceiveNetTime: LocalDateTime = LocalDateTime.now()

    private fun establishConnect() {
        mySocket.establish_connection()
    }
    private suspend fun listen() {
        while(true) {
            val inputString: String? = mySocket.read()
            if (inputString == null) {
                delay(500L)
                continue
            }
            LogContent.addLog(TAG, "Receive: " + inputString)
            lastReceiveNetTime = LocalDateTime.now()
            withContext(Dispatchers.Main) {
                mainActivity?.changeLoginText(true)
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
        CoroutineScope(Dispatchers.IO).launch {
            listen()
        }
        login()
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
}