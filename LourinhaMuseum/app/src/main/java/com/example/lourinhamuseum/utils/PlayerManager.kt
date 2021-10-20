package com.example.lourinhamuseum.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber


class PlayerManager(private val context: Context, private val audio:Uri)
    :LifecycleObserver{

    enum class AudioPlayerState {
        UNKNOWN_STATE, INITIALIZED, PLAYING, PAUSED, STOPPED, CONCLUDED
    }

    private var mMediaPlayer: MediaPlayer? =null
    private var mPlayerState = AudioPlayerState.UNKNOWN_STATE


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialisePlayer() {
        if(mPlayerState== AudioPlayerState.UNKNOWN_STATE){
            Timber.d("Player initialised")
            mMediaPlayer = MediaPlayer().apply {
                setDataSource(context, audio)
                prepare()
            }
            mPlayerState = AudioPlayerState.INITIALIZED
        }
    }

    fun getDuration(): Int {
        return mMediaPlayer?.duration ?: 0
    }

    fun setDisplay(display : SurfaceHolder){
        mMediaPlayer?.setDisplay(display)
    }


    fun getCurrentTime(): Int {
        return mMediaPlayer?.currentPosition ?: 0
    }

    fun seekTo(newPosition: Int) {
        if (mPlayerState != AudioPlayerState.UNKNOWN_STATE) {
            mMediaPlayer!!.seekTo(newPosition)
        }
    }

    fun pause() :Boolean{
        Timber.d("Player on Pause")
        var valid = false
        if (mPlayerState == AudioPlayerState.PLAYING) {
            mMediaPlayer!!.pause()
            mPlayerState = AudioPlayerState.PAUSED
            valid = true
        }
        return valid
    }

    fun play() : Boolean {
        var valid = false
        if (mPlayerState != AudioPlayerState.PLAYING
            && mPlayerState != AudioPlayerState.UNKNOWN_STATE
        ) {
                Timber.d("Audio playing")
            mMediaPlayer!!.start()
            mPlayerState = AudioPlayerState.PLAYING
            valid = true
        }
        return valid
    }

    fun forward(skip:Int){
        if (mPlayerState != AudioPlayerState.STOPPED
            && mPlayerState != AudioPlayerState.UNKNOWN_STATE
        ) {
            val pos = mMediaPlayer!!.currentPosition+skip
            val end = mMediaPlayer!!.duration
            val newPosition = if(pos>end) end else pos
            mMediaPlayer!!.seekTo(newPosition)
        }
    }

    fun rewind(skip:Int){
        if (mPlayerState != AudioPlayerState.STOPPED
            && mPlayerState != AudioPlayerState.UNKNOWN_STATE
        ) {
            val pos = mMediaPlayer!!.currentPosition-skip
            val newPosition = if(pos<0) 0 else pos
            mMediaPlayer!!.seekTo(newPosition)
            Toast.makeText(context, "Rewind ${skip/1000} seconds", Toast.LENGTH_SHORT).show()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        Timber.d("Player released")
        if(mMediaPlayer!=null) {
            mMediaPlayer!!.release()
            mPlayerState = AudioPlayerState.UNKNOWN_STATE
            mMediaPlayer = null

        }
    }

    fun setOnCompleteListener(listener: MediaPlayer.OnCompletionListener) {
        mMediaPlayer?.setOnCompletionListener(listener)
    }
}