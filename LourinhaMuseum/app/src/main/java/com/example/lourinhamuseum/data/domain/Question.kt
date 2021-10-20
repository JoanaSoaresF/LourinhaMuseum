package com.example.lourinhamuseum.data.domain

import com.example.lourinhamuseum.data.database.entities.DatabaseQuestion

data class Question(
    val id: Int,
    val pointId: Int,
    val question: String,
    val answers: List<Answer>,
    var order: Int = 0,
    var isAnsweredCorrectly: Boolean,
    var score: Int

) {
    companion object {
        const val CORRECT_SCORE = 5
        const val INCORRECT_PENALTY = 1
    }

    fun asDatabaseModel(): DatabaseQuestion {
        return DatabaseQuestion(pointId, id, question, isAnsweredCorrectly, score)

    }
}

fun List<Question>.asDatabaseModel(): List<DatabaseQuestion> {
    return map {
        it.asDatabaseModel()
    }

}