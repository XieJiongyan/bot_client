package com.example.bot_client_v2.source

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bot_client_v2.MainActivity
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select
import java.io.File
import java.io.InputStream
import java.lang.Exception

object NetClock {
    private val mySocket: MySocket = MySocket(8100)
    private val TAG: String = "Jog.NetClock"
    private var mainActivity: MainActivity? = null

    private fun establishConnect() {
        mySocket.establish_connection()
    }
    private fun listen() {
        while(true) {
            val inputString: String = mySocket.read()
            LogContent.addLog(TAG, "Receive: " + inputString)
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
        delay(5000)
        withContext(Dispatchers.Main) {
            mainActivity?.changeLoginText(mySocket.isConnected())
        }
    }
}