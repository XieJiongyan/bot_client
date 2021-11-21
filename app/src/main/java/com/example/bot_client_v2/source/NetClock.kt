package com.example.bot_client_v2.source

import com.example.bot_client_v2.ui.log.placeholder.LogContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NetClock {
    private val mySocket: MySocket = MySocket(8100)
    private val TAG: String = "Jog.NetClock"

    private fun establishConnect() {
        mySocket.establish_connection()
    }

    private fun listen() = CoroutineScope(Dispatchers.IO).launch {
        while(true) {
            val inputString: String = mySocket.read()
            LogContent.addLog(TAG, "Receive: " + inputString)
        }
    }

    fun autoLogin() = CoroutineScope(Dispatchers.IO).launch{
        establishConnect()
        listen()
    }
}