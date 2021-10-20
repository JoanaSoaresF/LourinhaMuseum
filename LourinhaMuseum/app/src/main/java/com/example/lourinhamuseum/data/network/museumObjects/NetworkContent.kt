package com.example.lourinhamuseum.data.network.museumObjects

import com.example.lourinhamuseum.data.database.entities.DatabaseContent
import com.squareup.moshi.JsonClass

/**
 * Network object represent a content
 */
@JsonClass(generateAdapter = true)
data class NetworkContent(
    val id: Int,
    val title: String,
    val content: String
) {
    fun asDatabaseModel(museumId: Int): DatabaseContent {
        return DatabaseContent(id, museumId, title, content)
    }
}

/**
 * Transforms a collection of network Content objects to a collection of database
 * Content objects
 */
fun List<NetworkContent>.asDatabaseModel(museumId: Int): List<DatabaseContent> {
    return map {
        it.asDatabaseModel(museumId)

    }
}
