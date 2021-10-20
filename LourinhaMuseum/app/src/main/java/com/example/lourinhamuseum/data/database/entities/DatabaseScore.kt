package com.example.lourinhamuseum.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lourinhamuseum.data.database.entities.DatabaseScore.Companion.SCORES_TABLE
import com.example.lourinhamuseum.data.domain.Score

@Entity(tableName = SCORES_TABLE)
data class DatabaseScore(
    @PrimaryKey
    val id:Long,
    val username: String,
    val score: Int
) {
    companion object {
        const val SCORES_TABLE = "scores_table"
    }

    fun asDomainModel(index : Int): Score {
        return Score(id, index, username, score)
    }
}

fun List<DatabaseScore>.asDomainModel(): List<Score> {
    return mapIndexed { index, databaseScore ->
        databaseScore.asDomainModel(index+1)
    }
}
