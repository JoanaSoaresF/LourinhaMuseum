package com.example.lourinhamuseum.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.custom_view.PapyrusLoading.Companion.START_DELAY
import com.example.lourinhamuseum.data.domain.Question

class QuizView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr), PapyrusLoading {


    private var question: Question? = null
    private var quizController: QuizController? = null

    var mRootView: View = inflate(context, R.layout.quiz, this)
    val questionText: TextView = findViewById(R.id.question_text)
    val buttonA: AnswerView = findViewById(R.id.answerA_button)
    val buttonB: AnswerView = findViewById(R.id.answerB_button)
    val buttonC: AnswerView = findViewById(R.id.answerC_button)
    val buttonD: AnswerView = findViewById(R.id.answerD_button)
    val nextButton: Button = findViewById(R.id.next_button)
    val exitButton: ImageView = findViewById(R.id.close_question_button)
    private val buttons = arrayOf(buttonA, buttonB, buttonC, buttonD)
    val popupTitle: TextView = findViewById(R.id.quiz_title)
    override val papyrusImage: ImageView = findViewById(R.id.quiz_papyrus)
    var pointsGained = 0

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    fun setQuizController(controller: QuizController) {
        quizController = controller
        nextButton.setOnClickListener {
            quizController?.onClickNextQuestion()
            if (controller.isLastQuestion()) {
                hide()
            }
        }
        exitButton.setOnClickListener { hide() }
    }

    fun setQuestion(newQuestion: Question) {
        pointsGained = 0
        question = newQuestion
        setupNextButton()
        questionText.text = newQuestion.question
        for (i in newQuestion.answers.indices) {
            val answer = newQuestion.answers[i]
            val button = buttons[i]
            button.setAnswer(answer.answer)
//            button.visibility = View.VISIBLE
            if (answer.isCorrect) {
                button.answerButton.setOnClickListener {
                    quizController?.onCorrectAnswer()
                    nextButton.visibility = VISIBLE
                    button.setCorrect(question!!.score)
                    for (b in buttons) {
                        if (!b.wasClicked && b != button) {
                            b.setAnswered()
                        }
                    }
                }
            } else {
                button.answerButton.setOnClickListener {
                    quizController?.onWrongAnswer()
                    button.setWrong()
                }
            }
        }
        nextButton.visibility = View.INVISIBLE


    }

    private fun setupNextButton() {
        quizController?.let { controller ->
            val text = if (controller.isLastQuestion())
                context.getString(R.string.done_question)
            else context.getString(R.string.next)

            nextButton.text = text
        }
    }


    override fun onShowEnd() {
        exitButton.visibility = View.VISIBLE
        popupTitle.visibility = View.VISIBLE
        for (i in /*buttons.indices*/question!!.answers.indices) {
            buttons[i].show()
        }
    }

    override fun onShowStart() {
        visibility = View.VISIBLE
        questionText.animation =
            AnimationUtils.loadAnimation(context, R.anim.slide_down)
        questionText.animation.startOffset = START_DELAY
        questionText.animate().start()
        exitButton.visibility = View.INVISIBLE
        popupTitle.visibility = View.INVISIBLE
    }

    override fun onCloseEnd() {
        quizController?.onCloseQuiz()
        visibility = View.GONE
        popupTitle.visibility = View.INVISIBLE
        questionText.visibility = View.INVISIBLE
        exitButton.visibility = View.INVISIBLE
        papyrusImage.visibility = View.INVISIBLE
        popupTitle.visibility = View.INVISIBLE
    }

    override fun onCloseStart() {
        popupTitle.visibility = View.INVISIBLE
        exitButton.visibility = View.INVISIBLE
        nextButton.visibility = View.INVISIBLE
        for (i in question!!.answers.indices) {
            buttons[i].hide()
        }
        questionText.animation =
            AnimationUtils.loadAnimation(context, R.anim.slide_up)
        questionText.animate().start()
    }

}

@BindingAdapter("quizShow")
fun bindQuizVisibility(quizView: QuizView, show: Boolean) {
    if (show) {
        quizView.show()
    }
}

