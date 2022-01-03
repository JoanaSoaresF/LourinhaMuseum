package com.example.lourinhamuseum.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.lourinhamuseum.data.domain.Question

data class QuestionWithAnswers(
    @Embedded
    val question: DatabaseQuestion,
    @Relation(
        parentColumn = "id",
        entityColumn = DatabaseAnswer.QUESTION_ID,
        entity = DatabaseAnswer::class
    )
    val answers: List<DatabaseAnswer> = emptyList()
) {
    fun asDatabaseModel(): Question {
        return Question(
            question.id,
            question.pointId,
            question.question,
            answers.asDomainModel(),
            isAnsweredCorrectly = question.isAnsweredCorrectly,
            score = question.score
        )

    }
}

fun List<QuestionWithAnswers>.asDomainModel(): List<Question> {
    return map {
        it.asDatabaseModel()
    }
}