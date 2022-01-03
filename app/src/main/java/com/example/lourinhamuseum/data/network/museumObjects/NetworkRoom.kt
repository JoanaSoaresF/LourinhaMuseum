package com.example.lourinhamuseum.data.network.museumObjects


import com.example.lourinhamuseum.data.database.entities.DatabaseRoom
import com.squareup.moshi.JsonClass

/**
 * Network object representing a room
 */
@JsonClass(generateAdapter = true)
data class NetworkRoom(
    val id: Int,
    val name: String,
    val title: String?,
    val order: Int?,
    val points: List<NetworkPoint>
) {

    /**
     * Transforms a network room object to a database room object. The slideshows have to
     * be added to a different table
     */
    fun asDatabaseModel(museumId: Int): DatabaseRoom {
        return DatabaseRoom(
            id,
            museumId,
            name,
            title?:"",
            order?:0
        )

    }
}

fun List<NetworkRoom>.asDatabaseModel(museumId: Int): List<DatabaseRoom> {
    return map {
        it.asDatabaseModel(museumId)

    }
}


