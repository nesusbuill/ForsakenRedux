package com.studios.forsaken2d

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

object NetworkManager {
    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    var isHost = false; var isConnected = false
    var onEnemyPosReceived: ((Float, Float) -> Unit)? = null
    var onConnected: (() -> Unit)? = null

    fun startHost(port: Int = 8888) {
        isHost = true
        Thread {
            try {
                serverSocket = ServerSocket(port)
                clientSocket = serverSocket!!.accept()
                setupStreams(); isConnected = true; onConnected?.invoke()
            } catch (e: Exception) { Log.e("NETWORK", "Lỗi Host: ${e.message}") }
        }.start()
    }

    fun joinGame(ip: String, port: Int = 8888) {
        isHost = false
        Thread {
            try {
                clientSocket = Socket(ip, port)
                setupStreams(); isConnected = true; onConnected?.invoke()
            } catch (e: Exception) { Log.e("NETWORK", "Lỗi Join: ${e.message}") }
        }.start()
    }

    private fun setupStreams() {
        writer = PrintWriter(clientSocket!!.getOutputStream(), true)
        reader = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
        Thread {
            try {
                var line: String?
                while (reader?.readLine().also { line = it } != null) {
                    val parts = line!!.split(",")
                    if (parts.size == 2) {
                        onEnemyPosReceived?.invoke(parts[0].toFloatOrNull() ?: 0f, parts[1].toFloatOrNull() ?: 0f)
                    }
                }
            } catch (e: Exception) { isConnected = false }
        }.start()
    }

    fun sendPos(x: Float, y: Float) { if (isConnected) try { writer?.println("$x,$y") } catch (_: Exception) {} }
    fun stop() { 
        try { serverSocket?.close() } catch (_: Exception) {}; 
        try { clientSocket?.close() } catch (_: Exception) {}; 
        isConnected = false 
    }
}
