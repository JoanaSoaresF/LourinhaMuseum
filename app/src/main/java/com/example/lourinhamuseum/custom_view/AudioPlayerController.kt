package com.example.lourinhamuseum.custom_view

import androidx.lifecycle.LiveData
import com.example.lourinhamuseum.utils.PlayerManager
import com.example.lourinhamuseum.data.domain.Point

interface AudioPlayerController {

    fun onPlayButtonClicked()

    val point: Point

    val isVideo: Boolean

    val player : PlayerManager

    fun onAudioComplete()

    val reproductionListener : LiveData<Boolean>
}