package com.example.bot_client_v2.source

import android.util.Log
import com.example.bot_client_v2.ui.log.placeholder.LogContent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.lang.Exception
import java.net.Socket
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class MySocket(val port: Int) {
    private val TAG: String = "Jog.MySocket"

    private var sc: Socket? = null
    private var bufferedReader: BufferedReader? = null
    private var outputStream: OutputStream? = null
    private var isConnecting: Boolean = false

    private var lock: Lock = ReentrantLock()
    private var isLocked: Boolean = false

    private var loginFun: (() -> Unit)? = null
    fun setLoginFun(func: () -> Unit) {
        loginFun = func
    }
    fun establish_connection() {
        Log.i(TAG, "start connect")
        if (isLocked) return
        isLocked = true
        lock.lock()
        try {
            sc = Socket("122.9.138.33", port)
            LogContent.addLog(TAG, "connected for port " + port.toString())
            val inputStream: InputStream = sc!!.getInputStream()
            val inputStreamReader = InputStreamReader(inputStream)
            bufferedReader = BufferedReader(inputStreamReader)
            outputStream = sc!!.getOutputStream()

            loginFun?.invoke()
        } catch (e: Exception) {
            LogContent.addLog(TAG, "cannot establish connect with " + port.toString())
            Log.i(TAG, "Error is:" + e.toString())
            CoroutineScope(Dispatchers.IO).launch {
                reConnect()
            }
        } finally {
            isLocked = false
            lock.unlock()
        }
    }

    suspend fun read(): NetStruct?{
        val json = Json { coerceInputValues = true }
        try {
            val input = bufferedReader!!.readLine()
            return Json.decodeFromString<NetStruct>(input)
        } catch (e: Exception) {
            LogContent.addLog(TAG, "Error read: " + e.toString())
            reConnect()
            return read()
        }
    }

    fun write(bytes: ByteArray) {
        try {
            outputStream!!.write(bytes)
            outputStream!!.flush()
        } catch (e: Exception) {
            LogContent.addLog(TAG, "Error write: $e")
            CoroutineScope(Dispatchers.IO).launch {
                reConnect()
                write(bytes)
            }
        }
    }

    private val reConnectChan = Channel<Boolean>(1)
    private val waitingChan = Channel<Boolean>()
    @ExperimentalCoroutinesApi
    private suspend fun reConnect() {
        try {
            val res = reConnectChan.trySend(true)
            if (res.isSuccess) {
                sc?.close()
                sc = null
                bufferedReader?.close()
                bufferedReader = null
                outputStream?.close()
                outputStream = null
                establish_connection()
                delay(500L)
                while (!waitingChan.isEmpty) waitingChan.receive()
                reConnectChan.receive()
            } else if(res.isFailure){
                waitingChan.send(true)
            }
        } catch(e: Exception) {
            Log.i(TAG, "Error reConnect: $e")
        }
    }

    @Serializable
    data class NetStruct(
        val command: String,
        val options: Array<String>?,
        val extras: String?
    ) {
        override fun toString(): String {
            var rev = "$command: "
            if (options != null) {
                for (s in options ) {
                    rev += "$s, "
                }
            }
            rev += extras?:""
            return rev
        }
    }

    fun ww() {
        val inputStruct = NetStruct("clock", arrayOf("get"), "")
        writeStruct(inputStruct)
    }
    fun writeStruct(inputStruct: NetStruct) {
        try {
            val str: String = Json.encodeToString(inputStruct)
            LogContent.addLog(TAG, "Write to Socket: $str")
            write(str.toByteArray())
        } catch (e: Exception) {
            Log.i(TAG, e.toString())
        }

    }
}