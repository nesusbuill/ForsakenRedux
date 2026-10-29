package com.studios.forsaken2d

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.hypot

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    private var gameThread: Thread? = null
    @Volatile private var isPlaying = false
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 0 = Lobby
    private var state = 0 
    
    private var kIndex = 0
    private var sIndex = 0

    private lateinit var map: GameMap
    private var pChar: Char? = null
    private var pX = 0f; private var pY = 0f
    private var pHp = 100f

    init { 
        holder.addCallback(this) 
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isPlaying = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isPlaying = false
        try { gameThread?.join() } catch (_: Exception) {}
    }

    override fun run() {
        // Đợi 1 nhịp để SurfaceView sẵn sàng
        try { Thread.sleep(200) } catch (_: Exception) {}

        while (isPlaying) {
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    // Nếu có lỗi khi vẽ, catch lại để không văng app
                    try {
                        drawGame(canvas)
                    } catch (e: Exception) {
                        Log.e("GAME_CRASH", "Lỗi vẽ: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("GAME_CRASH", "Lỗi Canvas: ${e.message}")
            } finally {
                if (canvas != null) {
                    try { holder.unlockCanvasAndPost(canvas) } catch (_: Exception) {}
                }
            }
            try { Thread.sleep(30) } catch (_: Exception) {}
        }
    }

    private fun drawGame(c: Canvas) {
        c.drawColor(Color.BLACK)
        val w = width.toFloat()
        val h = height.toFloat()

        paint.color = Color.parseColor("#D32F2F")
        paint.textSize = 80f
        paint.textAlign = Paint.Align.CENTER
        c.drawText("FORSAKEN: REDUX", w / 2f, h / 3f, paint)

        // Vẽ nút bấm giả lập
        paint.color = Color.parseColor("#7CFFB2")
        c.drawRect(w / 2f - 200f, h / 2f, w / 2f + 200f, h / 2f + 100f, paint)
        
        paint.color = Color.BLACK
        paint.textSize = 40f
        c.drawText("BẮT ĐẦU GAME", w / 2f, h / 2f + 65f, paint)

        // Vẽ thông báo nhỏ góc màn hình
        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.textAlign = Paint.Align.LEFT
        c.drawText("App đã chạy thành công! Bấm nút để test.", 50f, 50f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Bấm vào đâu cũng hiện thông báo để test
            android.widget.Toast.makeText(context, "Đã bấm! App không văng!", android.widget.Toast.LENGTH_SHORT).show()
        }
        return true
    }
}
