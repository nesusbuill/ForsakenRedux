package com.studios.forsaken2d

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

object SoundManager {
    private var bgmPlayer: MediaPlayer? = null

    fun playLMSMusic(context: Context, musicName: String) {
        stopMusic()
        if (musicName.isEmpty()) return
        
        val resId = context.resources.getIdentifier(musicName, "raw", context.packageName)
        if (resId != 0) {
            try {
                bgmPlayer = MediaPlayer.create(context, resId).apply {
                    isLooping = true
                    setVolume(0.8f, 0.8f)
                    start()
                }
            } catch (e: Exception) {
                Log.e("SOUND", "Lỗi phát nhạc: ${e.message}")
            }
        }
    }

    fun stopMusic() {
        bgmPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        bgmPlayer = null
    }
}
