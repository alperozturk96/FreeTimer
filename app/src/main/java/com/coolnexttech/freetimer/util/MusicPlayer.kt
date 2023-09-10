package com.coolnexttech.freetimer.util

import android.content.Context
import android.media.MediaPlayer

class MusicPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    var canPlay = true

    fun playAudio(resourceId: Int) {
        if (!canPlay) {
            return
        }

        stopAudio()
        mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer?.start()
    }

    fun stopAudio() {
        if (!canPlay) {
            return
        }

        mediaPlayer?.release()
        mediaPlayer = null
    }
}