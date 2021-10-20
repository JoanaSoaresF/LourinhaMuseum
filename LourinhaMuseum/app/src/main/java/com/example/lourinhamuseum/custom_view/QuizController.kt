package com.example.lourinhamuseum.custom_view

import androidx.lifecycle.LiveData

/**
 * Interface that defines the expected behaviour for controlling the Quiz View
 */
interface QuizController {

    /**
     * Defines what happen when the quiz view is closed
     */
    fun onCloseQuiz()

    /**
     * Defines what happen when answer selected is correct
     */
    fun onCorrectAnswer()

    /**
     * Defines what happen when answer selected is wrong
     */
    fun onWrongAnswer()

    /**
     * Defines what happens when the next question button is clicked
     */
    fun onClickNextQuestion()

    /**
     * Defines if the quiz view should be visible or not
     */
    val isQuizVisible : LiveData<Boolean>

    /**
     * Sinalizes that the question displayed is the last question, changing the next
     * question button
     */
    fun isLastQuestion() : Boolean

}