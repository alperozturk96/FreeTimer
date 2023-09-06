package com.coolnexttech.freetimer.util

import android.content.Context
import android.media.MediaPlayer

class MusicPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(resourceId: Int) {
        stopAudio()
        mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer?.start()
    }

    fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}