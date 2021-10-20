package com.example.lourinhamuseum.data.network.museumObjects

import com.example.lourinhamuseum.data.database.entities.DatabaseSlideshow

data class NetworkSlideshow(
    val id: Int,
    val name: String,
    val url: String,
    val time: Int,
    val caption: String?
) {
    fun asDatabaseModel(pointId: Int): DatabaseSlideshow {
        return DatabaseSlideshow(pointId, id, name, url, time, caption?:"")
    }
}

fun List<NetworkSlideshow>.asDatabaseModel(pointId: Int): List<DatabaseSlideshow> {
    return map {
        it.asDatabaseModel(pointId)

    }
}

