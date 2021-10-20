package com.example.lourinhamuseum.data.network.museumObjects

import com.example.lourinhamuseum.data.database.entities.DatabaseMuseum
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Network museum object
 */
@JsonClass(generateAdapter = true)
data class NetworkMuseum(
    @Json(name = "id")
    val museumId: Int,
    val name: String?,
    val contents: List<NetworkContent>?,
    val rooms: List<NetworkRoom>
) {
    /**
     * Transforms a network museum object to a database museum object. The contents and
     * the rooms have to be added to a different tables
     */
    fun asDatabaseModel(): DatabaseMuseum {
        return DatabaseMuseum(
            id = museumId,
            title = this.name?:""
        )
    }
}