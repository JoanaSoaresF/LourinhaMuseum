package com.example.lourinhamuseum.screens.vuforia


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.custom_view.PopupInfoController
import com.example.lourinhamuseum.data.domain.Point
import com.example.lourinhamuseum.data.repository.MuseumRepository
import timber.log.Timber


class VuforiaViewModel(application: Application) : AndroidViewModel(application),
    PopupInfoController {

    enum class DetectionState {
        FOUND, NOT_FOUND, HELP_PRESSED, CAPTURE_PRESSED
    }

    companion object {
        const val DETECTION_COMPLETE = 50
    }


    /**
     * Objecto para sinalizar que se deve passar para o fragmento que mostra as
     * informações de uma imagem, ou alterações nos botões apresentados
     */
    //ATTENTION mudar estado inicial para NOT_FOUND
    private val _detectionState = MutableLiveData(DetectionState.FOUND)
    val detectionState: LiveData<DetectionState>
        get() = _detectionState

    /**
     * Detection progress of the selected point
     */
    private var detectionProgress = 0
    private var lastImageDetected: Int? = null

    /**
     * Number of times the image as detected in a row
     */

    /**
     * Repository to fetch the data
     */
    private val repository = MuseumRepository.getMuseumRepository(application)

    /**
     * Point to detect
     */
    lateinit var point: Point
    var pointId: Int? = null


    /**
     * Monitoriza o progresso da deteção de imagem e sinaliza se a imagem foi detetada
     */
    fun monitorizeDetectionProgress(image: Int?) {

        if (image != lastImageDetected) {
            detectionProgress = 0
            lastImageDetected = image
        } else if (image != null) {
            detectionProgress++
        }

        if (detectionProgress == DETECTION_COMPLETE && lastImageDetected == pointId) {
            _detectionState.value = DetectionState.FOUND
        }
    }

    /**
     * Sinalizes that the navigation is complete, restoring the state
     */
    fun navigationDone() {
        _detectionState.value = DetectionState.NOT_FOUND
        detectionProgress = 0
    }

    /**
     * Sets the point to detect in the session
     */
    fun setPointToDetect(id: Int) {
        //ATTENTION mudar estado inicial para NOT_FOUND
        _detectionState.value = DetectionState.FOUND
        detectionProgress = 0
        pointId = id
        repository.museum?.let {
            point = it.getPoint(id)!!
        }

    }

    fun onHelpPressed() {
        if (_detectionState.value != DetectionState.HELP_PRESSED) {
            _detectionState.value = DetectionState.HELP_PRESSED
        }
    }

    fun onCapturePressed() {
        Timber.i("Capture pressed")
        if (_detectionState.value == DetectionState.FOUND) {
            _detectionState.value = DetectionState.CAPTURE_PRESSED
        }
        //_detectionState.value = DetectionState.CAPTURE_PRESSED
    }


    override fun onClosePopup() {
        navigationDone()
    }

    override val isPopupVisible: LiveData<Boolean> =
        Transformations.map(_detectionState) {
            it == DetectionState.HELP_PRESSED
        }
    override val popupText: String
        get() = point.helpTip

    override val popupTitle: String
        get() = getApplication<Application>().getString(R.string.ajuda_text)

}