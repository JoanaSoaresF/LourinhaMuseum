package com.example.lourinhamuseum.screens.information


import android.app.Application
import android.os.Environment
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.example.lourinhamuseum.utils.PlayerManager
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.custom_view.AudioPlayerController
import com.example.lourinhamuseum.custom_view.PopupInfoController
import com.example.lourinhamuseum.custom_view.QuizController
import com.example.lourinhamuseum.data.domain.Point
import com.example.lourinhamuseum.data.domain.Question
import com.example.lourinhamuseum.data.repository.MuseumRepository
import kotlinx.coroutines.launch
import java.io.File

class InformationViewModel(val pointId: Int, application: Application) :
    AndroidViewModel(application), QuizController, PopupInfoController,
    AudioPlayerController {

    /**
     * Possible states and events of the Information Fragment
     * INFO fot when the main screen is showing
     * QUIZ when the quiz is displayed
     * TEXT when the text id displayed
     * PLAYING when the audio is playing
     */
    enum class State {
        INFO, QUIZ, TEXT, PLAYING
    }

    /**
     * Current stat of the InformationFragment
     */
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state


    /**
     * Player manager to mage the audio in the fragment
     */
    override val player: PlayerManager

    /**
     * Repository to collect the data
     */
    private val repository = MuseumRepository.getMuseumRepository(application)
    private val museum = repository.museum!!

    /**
     * Point to show the information
     */
    override val point = museum.getPoint(pointId)!!
    private val _pointObservable: MutableLiveData<Point> = MutableLiveData(point)
    val pointObservable: LiveData<Point>
        get() = _pointObservable

    /**
     * Variable to indicate if in the point information is to display a video or a
     * slideshow
     */
    //ATTENTION remove && point.slideshow.isEmpty()
    override val isVideo: Boolean = point.video != "" //&& point.slideshow.isEmpty()

    /**
     * Current question to show in the quiz
     */
    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question
    private var numQuestion = 0

    init {
        _state.value = State.INFO

        if (!point.isFound) {
            point.isFound = true
            museum.numPointsToFind--
        }

        if (point.questions.isNotEmpty()) {
            val q = point.questions[numQuestion]
            q.order = numQuestion + 1
            _question.value = q
        }

        val file = if (isVideo) point.video else point.media
        val audio = File(
            application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            file
        ).toUri()
        player = PlayerManager(getApplication(), audio)
        player.initialisePlayer()
        viewModelScope.launch { repository.updatePointInDatabase(point) }

    }

    /**
     * Changes the status of the fragment to display the quiz.
     * The player is paused
     */
    fun showQuiz() {
        if (_state.value != State.TEXT && _state.value != State.QUIZ) {
            _state.value = State.QUIZ
        }
    }

    /**
     * Changes the status of the fragment to display the text.
     * The player is paused
     */
    fun showText() {
        if (_state.value != State.TEXT && _state.value != State.QUIZ) {
            _state.value = State.TEXT
        }
    }

    fun backToInfoState() {
        _state.value = State.INFO
    }

    //Methods to control the audio
    /**
     * Sinalizes the state changes. If the audio isn't playing the [state] will change
     * to [State.INFO]. If the [state==State.INFO] then  the state will chante to [State
     * .PLAYING]
     */
    override fun onPlayButtonClicked() {
        when (state.value) {
            State.INFO -> _state.postValue(State.PLAYING)
            State.PLAYING -> _state.postValue(State.INFO)
        }
    }

    override fun onAudioComplete() {
    }

    override val reproductionListener: LiveData<Boolean>
        get() = Transformations.map(_state) { it == State.PLAYING }


    //Methods that control the quiz

    fun quizButtonVisibility(): Int {
        return if (point.questions.isNotEmpty()) View.VISIBLE else View.INVISIBLE
    }

    override fun onCloseQuiz() {
        backToInfoState()
    }

    /**
     * Expected behaviour when clicked on the correct answer
     * If there aren't any more questions the quiz is marked as complete
     * The question answered is marked as correctly answered and the score is increased
     */
    override fun onCorrectAnswer() {
        if (++numQuestion == point.questions.size) {
            point.isQuizCorrect = true
        }
        val q = question.value
        q?.let {
            if (!q.isAnsweredCorrectly) {
                q.isAnsweredCorrectly = true
                q.score += Question.CORRECT_SCORE
                museum.score += Question.CORRECT_SCORE
                point.pointScore += Question.CORRECT_SCORE
                museum.isScoreUpdated = true
            }
        }
    }

    /**
     * Expected behaviour when clicked on the wrong answer
     * The [museum.score] id decreased
     */
    override fun onWrongAnswer() {
        val q = question.value
        q?.let {
            if (!q.isAnsweredCorrectly) {
                q.score -= Question.INCORRECT_PENALTY
                museum.score -= Question.INCORRECT_PENALTY
                point.pointScore -= Question.INCORRECT_PENALTY
                museum.isScoreUpdated = true

            }
        }
    }

    /**
     * Behavior expected from a click in the next question button, in the quiz
     * When there are more questions the button should skip to the next question. If
     * there are no more questions the button should return to the main screen and
     * update the database [Point.isQuizCorrect] and update the score on the remote sever
     */
    override fun onClickNextQuestion() {

        if (numQuestion < point.questions.size - 1) {
            val q = point.questions[numQuestion]
            q.order = 1 + numQuestion++
            _question.postValue(q)
        } else {
            updateDatabase()
            _pointObservable.postValue(point)
            backToInfoState()

        }
    }

    fun updateDatabase() {
        viewModelScope.launch {
            repository.updateScoreInDatabase(point)
        }
    }

    override val isQuizVisible = Transformations.map(state) { it == State.QUIZ }


    override fun isLastQuestion(): Boolean {
        return question.value?.order == pointObservable.value?.questions?.size
    }

    //Text popup methods

    override fun onClosePopup() {
        backToInfoState()
    }

    override val isPopupVisible = Transformations.map(state) { it == State.TEXT }

    override val popupText: String
        get() = point.description

    override val popupTitle: String
        get() = getApplication<Application>().getString(R.string.text_title)
}






