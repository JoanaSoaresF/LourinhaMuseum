package com.example.lourinhamuseum.data.network.museumObjects

import com.example.lourinhamuseum.data.database.entities.DatabaseAnswer

data class NetworkAnswer(
    val id: Int,
    val answer: String,
    val isCorrect: Boolean
) {
    fun asDatabaseModel(questionId: Int): DatabaseAnswer {
        return DatabaseAnswer(questionId, id, answer, isCorrect)

    }

}

fun List<NetworkAnswer>.asDatabaseModel(questionId: Int): List<DatabaseAnswer> {
    return map {
        it.asDatabaseModel(questionId)
    }
}
