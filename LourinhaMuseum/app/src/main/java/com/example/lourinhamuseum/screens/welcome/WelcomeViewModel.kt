package com.example.lourinhamuseum.screens.welcome

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.data.SharedPreferencesManager
import com.example.lourinhamuseum.data.domain.Museum
import com.example.lourinhamuseum.data.repository.MuseumRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WelcomeViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Possible states for the button in the welcome screen
     */
    enum class State {
        LOADING, READY, DOWNLOADING, PLAY, ERROR
    }

    /**
     * Sinalizes the changes in the state of the button
     */
    private val _buttonState = MutableLiveData(State.LOADING)
    val buttonState: LiveData<State>
        get() = _buttonState

    /**
     * Triggers the event of navigation to the game fragment
     */
    private val _navigateToGame = MutableLiveData(false)
    val navigateToGame: LiveData<Boolean>
        get() = _navigateToGame


    private val museumRepository = MuseumRepository.getMuseumRepository(application)
    var museum: Museum? = null

    val ranking = museumRepository.ranking

    val numFilesTransferred: LiveData<Int> = museumRepository.getNumberFilesTransferred()
    private var downloadFilesJob: Job? = null

    /**
     * Variables that sinalizes changes the state of the definition of the user name
     */
    private val _isUserDefined = MutableLiveData(museumRepository.isUserDefined)
    val isUserDefined: LiveData<Boolean>
        get() = _isUserDefined

    init {

        viewModelScope.launch {
            museumRepository.refreshRanking()
        }

        loadMuseum()
    }

    private fun loadMuseum() {
        _buttonState.postValue(State.LOADING)
        viewModelScope.launch {
            museum = museumRepository.getMuseum()
            //If the museum isn't loaded an error occurred, possible no internet
            // connection
            if (museum == null) {
                _buttonState.postValue(State.ERROR)
            } else {
                //The state depends if the files are already download or not
                if (SharedPreferencesManager.isDownloadDone(getApplication())) {
                    _buttonState.postValue(State.PLAY)
                } else {
                    _buttonState.postValue(State.READY)
                }
            }
        }
    }

    /**
     * Performs the download of all files. When starting the state of the button will
     * the "CANCEL", when the download is done with success the state will change to "PLAY"
     * If an error occurs during the download a toast warning will appear
     */
    private fun downloadFiles() {
        _buttonState.postValue(State.DOWNLOADING)
        downloadFilesJob?.cancel()
        downloadFilesJob = viewModelScope.launch {
            val success = museumRepository.downloadALlFiles()
            if (success) {
                _buttonState.postValue(State.PLAY)
                SharedPreferencesManager.setFilesDownloadDone(getApplication())
            } else {
                _buttonState.postValue(State.ERROR)
                val app = getApplication<Application>()
                Toast.makeText(
                    app,
                    app.getString(R.string.download_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Cancels the Job of download the files.
     * Changes the state back to Download
     */
    private fun cancelDownload() {
        downloadFilesJob?.cancel()
        _buttonState.postValue(State.READY)
    }

    /**
     * Performs the actions necessary according to the button's state
     * If the state is download it will perform the download off all the files
     * If the state is Cancel it will cancel the download of the files
     * If the state is play it will sinalize the event for navigation for the next fragment
     * When it is loading doesn't do anything
     *
     */
    fun buttonClicked() {
        when (_buttonState.value) {
            State.READY -> downloadFiles()
            State.DOWNLOADING -> cancelDownload()
            State.PLAY -> play()
            State.ERROR -> loadMuseum()
        }
    }

    /**
     * Sinalizes that the navigation is complete
     */
    fun navigationDone() {
        _navigateToGame.value = false
    }

    /**
     * Sinalizes the event to trigger navigation
     */
    private fun play() {
        if (museumRepository.isUserDefined) {
            _navigateToGame.value = true
        } else {
            Toast.makeText(
                getApplication(),
                getApplication<Application>().getString(R.string.username_error),
                Toast.LENGTH_LONG
            ).show()
            _buttonState.value = State.DOWNLOADING
        }

    }

    fun score(): Int {
        return museum?.score ?: 0
    }

    fun chooseUsername(username: String): Boolean {
        var success = false
        if (username != "") {
            success = true
            museumRepository.saveUsername(getApplication(), username)
            _isUserDefined.value = museumRepository.isUserDefined
            if(SharedPreferencesManager.isDownloadDone(getApplication())){
                _buttonState.value = State.PLAY
            }
        }
        return success
    }

    fun getUsername(): String {
        return museumRepository.username ?: ""
    }

}