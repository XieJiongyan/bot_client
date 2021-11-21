package com.example.bot_client_v2.source

import android.content.Context
import android.util.Log
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.lang.Exception

object NetClock {
    private val mySocket: MySocket = MySocket(8100)
    private val TAG: String = "Jog.NetClock"

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
    fun autoLogin() = CoroutineScope(Dispatchers.IO).launch{
        establishConnect()
        CoroutineScope(Dispatchers.IO).launch {
            listen()
        }
        login()
    }
}