package com.example.bot_client_v2.source

import android.util.Log
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket

class MySocket(val port: Int) {
    private var sc: Socket? = null
    private val TAG: String = "Jog.MySocket"

    private var bufferedReader: BufferedReader? = null

    fun establish_connection() {
        Log.i(TAG, "start connect")
        try {
            sc = Socket("122.9.138.33", port)
            LogContent.addLog(TAG, "connected for port " + port.toString())
            val inputStream: InputStream = sc!!.getInputStream()
            val inputStreamReader = InputStreamReader(inputStream)
            bufferedReader = BufferedReader(inputStreamReader)
        } catch (e: Exception) {
            LogContent.addLog(TAG, "cannot establish connect with" + port.toString())
            Log.i(TAG, "Error is:" + e.toString())
        }
    }

    fun read(): String{
        try {
            return bufferedReader!!.readLine()
        } catch (e: Exception) {
            Log.i(TAG, "Error is: " + e.toString())
        }
        return ""
    }

    fun write(str: String) {

    }
}