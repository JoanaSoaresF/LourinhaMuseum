package com.example.lourinhamuseum.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getDrawable
import com.bumptech.glide.Glide
import com.example.lourinhamuseum.R

class AnswerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    val mRootView: View = inflate(context, R.layout.answer_button_view, this)
    val answerButton: Button = findViewById(R.id.answer_button)
    val scoreText: TextView = findViewById(R.id.question_score)
    val check: ImageView = findViewById(R.id.check_answer)
    var wasClicked = false

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    fun setAnswer(answer: String) {
        answerButton.text = answer
    }

    fun show() {
        visibility = View.VISIBLE
        scoreText.visibility = View.INVISIBLE
        check.visibility = View.INVISIBLE
        answerButton.visibility = View.VISIBLE
//        answerButton.setBackgroundColor(getColor(context, R.color.answer_base_color))
        answerButton.background = getDrawable(context, R.drawable.quiz_button_black)
    }

    fun hide() {
        wasClicked = false
        visibility = View.INVISIBLE
        scoreText.visibility = View.INVISIBLE
        check.visibility = View.INVISIBLE
        answerButton.visibility = View.INVISIBLE
    }

    fun setCorrect(score: Int) {
        wasClicked = true
        scoreText.text =
            String.format(context.getString(R.string.score_button_text), score)
        scoreText.visibility = View.VISIBLE
        check.visibility = View.VISIBLE
        answerButton.background = getDrawable(context, R.drawable.quiz_button_green)
//        answerButton.setBackgroundColor(getColor(context, R.color.correct_answer))
        Glide.with(context)
            .load(R.drawable.correct_answer)
            .into(check)
    }

    fun setWrong() {
        wasClicked = true
        check.visibility = View.VISIBLE
//        answerButton.setBackgroundColor(getColor(context, R.color.black))
        Glide.with(context)
            .load(R.drawable.wrong_answer)
            .into(check)
    }

    fun setAnswered() {
//        answerButton.setBackgroundColor(getColor(context, R.color.wrong_answer))
        answerButton.background = getDrawable(context, R.drawable.quiz_button_grey)
    }

}