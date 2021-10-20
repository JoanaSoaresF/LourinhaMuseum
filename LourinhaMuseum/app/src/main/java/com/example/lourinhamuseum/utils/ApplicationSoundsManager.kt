package com.example.lourinhamuseum.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.lourinhamuseum.R

class ApplicationSoundsManager:
    LifecycleObserver, MediaPlayer.OnCompletionListener {

    private var soundPlayer: MediaPlayer? = null

    companion object {
        private var INSTANCE: ApplicationSoundsManager? = null

        fun getSoundManager(): ApplicationSoundsManager {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = ApplicationSoundsManager()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }


    fun onClickSound(context: Context) {
        playOnceSound(context, R.raw.clicar)
    }

    fun onLoopSound(context: Context) {
        releasePlayers()
        soundPlayer = MediaPlayer.create(context, R.raw.loop)
        soundPlayer?.isLooping = true
        soundPlayer?.start()
    }

    fun onDetectSound(context: Context) {
        playOnceSound(context, R.raw.detecao)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pauseAppSound() {
        soundPlayer?.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playAppSound() {
        soundPlayer?.start()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun releasePlayers() {
        soundPlayer?.release()
        soundPlayer = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        releasePlayers()
    }

    fun onDownloadClick(context: Context) {
        playOnceSound(context,  R.raw.sword)
    }

    private fun playOnceSound(context: Context, sound: Int) {
        releasePlayers()
        soundPlayer = MediaPlayer.create(context, sound)
        soundPlayer?.start()
        soundPlayer?.setOnCompletionListener(this)
    }


}