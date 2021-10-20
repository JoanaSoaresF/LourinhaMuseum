package com.example.lourinhamuseum.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.lourinhamuseum.data.domain.Point

data class PointWithSlideshow(
    @Embedded
    val point: DatabasePoint,

    @Relation(
        parentColumn = "id",
        entityColumn = DatabaseSlideshow.POINT_ID,
        entity = DatabaseSlideshow::class
    )
    val slideshow: List<DatabaseSlideshow> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = DatabaseQuestion.POINT_ID,
        entity = DatabaseQuestion::class)
    val questions: List<QuestionWithAnswers> = emptyList()
) {
    fun asDomainModel(): Point {
        return Point(
            point.id,
            point.roomId,
            point.name,
            point.arImage?:"",
            point.listImage,
            point.socialNetworkMedia,
            point.baseImage,
            point.title,
            point.description,
            point.media,
            point.descriptionAudio,
            point.mediaSignLanguage,
            slideshow.asDomainModel(),
            questions.asDomainModel(),
            point.isFound,
            point.isQuizComplete,
            point.pointScore
        )
    }
}

fun List<PointWithSlideshow>.asDomainModel(): List<Point> {
    return map {
        it.asDomainModel()
    }
}