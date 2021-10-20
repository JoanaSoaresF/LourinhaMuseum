package com.example.lourinhamuseum.screens.allCards

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lourinhamuseum.data.domain.Museum
import com.example.lourinhamuseum.data.domain.Point
import com.example.lourinhamuseum.data.repository.MuseumRepository
import com.example.lourinhamuseum.utils.ApplicationSoundsManager

class AllCardsViewModel(application: Application) : AndroidViewModel(application) {

    var permissionRequested = false

    enum class State {
        UNKNOWN, REQUEST_PERMISSIONS, PERMISSIONS_GRANTED, DETECTION, INFO
    }

    val soundPlayer by lazy { ApplicationSoundsManager.getSoundManager() }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    /**
     * Objecto para sinalizar que se deve passar para o fragmento que mostra as
     * informações de uma imagem
     */
    var pointToDetect: Int? = null
    private var point: Point? = null

    /**
     * Coleção de todos os cromos a serem encontrados
     */
    private val _allCards = MutableLiveData<List<DataItem>>()
    val allCards: LiveData<List<DataItem>>
        get() = _allCards

    /*Repository were the data is stored*/
    private val repository = MuseumRepository.getMuseumRepository(application)

    /*Object representing the museum*/
    val museum: Museum = repository.museum!!

    init {
        _state.value = State.UNKNOWN
        _allCards.value = museum.recyclerViewPoints

    }


    /**
     * Sinalizes that the navigation to another fragment is complete
     */
    fun navigationDone() {
        _state.value = State.UNKNOWN
    }

    fun confirmPermissionsDenied(): Boolean {
        return !permissionRequested

    }

    fun imageClicked(point: Point) {
        pointToDetect = point.id
        this.point = point
        when (_state.value) {
            State.UNKNOWN -> handlePermissions(point)
            State.REQUEST_PERMISSIONS -> _state.value = State.REQUEST_PERMISSIONS
            State.PERMISSIONS_GRANTED -> navigateToCorrectFragment(point)
        }
    }

    private fun handlePermissions(point: Point) {
        if (cameraPermissionsGranted()) {
            navigateToCorrectFragment(point)
        } else {
            _state.value = State.REQUEST_PERMISSIONS
        }

    }

    private fun navigateToCorrectFragment(point: Point) {
        soundPlayer.onClickSound(getApplication())
        if (point.isFound) {
            _state.value = State.INFO
        } else {
            _state.value = State.DETECTION
        }
    }

    private fun cameraPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    }

    fun permissionGranted() {
        _state.value = State.PERMISSIONS_GRANTED
    }

    fun playLoop() {
        soundPlayer.onLoopSound(getApplication())
    }

}