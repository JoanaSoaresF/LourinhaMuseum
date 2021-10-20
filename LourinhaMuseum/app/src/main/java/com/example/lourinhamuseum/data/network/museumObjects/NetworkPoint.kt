package com.example.lourinhamuseum.data.network.museumObjects

import com.example.lourinhamuseum.data.database.entities.DatabasePoint
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Network object representing a Point
 */
@JsonClass(generateAdapter = true)
data class NetworkPoint(
    val id: Int,
    val name: String,
    val arImage:String?,
    val listImage: String,
    val socialNetworksMedia: String?,
    val baseImage: String?,
    val title: String,
    @Json(name = "languageDesc")
    val description: String,
    val media: String?,
    @Json(name = "languageDescAudio")
    val descriptionAudio: String?,
    val mediaSignLanguage:String?,
    val questions: List<NetworkQuestion>,
    val slideshow: List<NetworkSlideshow>
) {
    /**
     * Transforms a network point object to a database point object.
     */
    fun asDatabaseModel(roomId: Int): DatabasePoint {
        return DatabasePoint(
            id,
            roomId,
            name,
            arImage,
            listImage,
            socialNetworksMedia?:"",
            baseImage?:"",
            title,
            description,
            descriptionAudio?:"",
            mediaSignLanguage?:"",
            media?:""
        )

    }
}

fun List<NetworkPoint>.asDatabaseModel(roomID: Int): List<DatabasePoint> {
    return map {
        it.asDatabaseModel(roomID)
    }
}