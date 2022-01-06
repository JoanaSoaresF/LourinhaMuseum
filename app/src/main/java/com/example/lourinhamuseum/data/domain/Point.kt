package com.example.lourinhamuseum.data.domain

import com.example.lourinhamuseum.data.database.entities.DatabasePoint

data class Point(
    val id: Int,
    val roomId: Int,
    val name: String,
    val outlineImage: String,
    val imageNotFound: String,
    val socialNetworkMedia: String,
    val imageFound: String,
    val title: String,
    val description: String,
    val media: String,
    val helpTip: String,
    val video: String,
    val slideshow: List<Slideshow>,
    val questions: List<Question>,
    var isFound: Boolean,
    var isQuizCorrect: Boolean,
    var pointScore: Int
) {
    val hasIcon: Boolean
        get() = pointScore > 0

    fun asDatabaseModel(): DatabasePoint {
        return DatabasePoint(
            id,
            roomId,
            name,
            outlineImage,
            imageNotFound,
            socialNetworkMedia,
            imageFound,
            title,
            description,
            helpTip,
            video,
            media,
            isFound,
            isQuizCorrect,
            pointScore
        )
    }
}

