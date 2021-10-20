package com.example.lourinhamuseum.data.database.entities

import androidx.room.*
import com.example.lourinhamuseum.data.database.entities.DatabaseQuestion.Companion.POINT_ID
import com.example.lourinhamuseum.data.database.entities.DatabaseQuestion.Companion.QUESTION_TABLE
import com.example.lourinhamuseum.data.domain.Question

@Entity(
    tableName = QUESTION_TABLE,
    foreignKeys = [ForeignKey(
        entity = DatabasePoint::class,
        parentColumns = ["id"],
        childColumns = [POINT_ID]
    )],
    indices = [Index(POINT_ID)]
)
data class DatabaseQuestion(
    @ColumnInfo(name = POINT_ID)
    val pointId: Int,
    @PrimaryKey
    val id: Int,
    val question: String,
    var isAnsweredCorrectly : Boolean = false,
    var score: Int = 0
) {
    companion object {
        const val QUESTION_TABLE = "questions_table"
        const val POINT_ID = "point_id"
    }

    fun asDomainModel(): Question {
        return Question(
            id,
            pointId,
            question,
            emptyList(),
            isAnsweredCorrectly = isAnsweredCorrectly,
            score = score
        )
    }
}