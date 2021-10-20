package com.example.lourinhamuseum.data.database.entities

import androidx.room.*
import com.example.lourinhamuseum.data.database.entities.DatabaseAnswer.Companion.ANSWERS_TABLE
import com.example.lourinhamuseum.data.database.entities.DatabaseAnswer.Companion.QUESTION_ID
import com.example.lourinhamuseum.data.domain.Answer

@Entity(
    tableName = ANSWERS_TABLE,
    foreignKeys = [ForeignKey(
        entity = DatabaseQuestion::class,
        parentColumns = ["id"],
        childColumns = [QUESTION_ID]
    )],
    indices = [Index(QUESTION_ID)]
)
data class DatabaseAnswer(
    @ColumnInfo(name = QUESTION_ID)
    val questionId: Int,
    @PrimaryKey
    val id: Int,
    val answer: String,
    @ColumnInfo(name = IS_CORRECT)
    val isCorrect: Boolean
) {
    companion object {
        const val ANSWERS_TABLE = "answers_table"
        const val QUESTION_ID = "question_id"
        const val IS_CORRECT = "is_correct"
    }

    fun asDomainModel(): Answer {
        return Answer(id, answer, isCorrect)
    }
}

fun List<DatabaseAnswer>.asDomainModel(): List<Answer> {
    return map {
        it.asDomainModel()
    }
}
