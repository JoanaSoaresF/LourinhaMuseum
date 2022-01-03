package com.example.lourinhamuseum.data.network.museumObjects

import com.example.lourinhamuseum.data.database.entities.DatabaseQuestion

data class NetworkQuestion(
    val id: Int,
    val question: String,
    val answers: List<NetworkAnswer>
) {
    fun asDatabaseModel(pointId: Int): DatabaseQuestion {
        return DatabaseQuestion(pointId, id, question)
    }
}

fun List<NetworkQuestion>.asDatabaseModel(pointId: Int): List<DatabaseQuestion> {
    return map {
        it.asDatabaseModel(pointId)
    }
}


