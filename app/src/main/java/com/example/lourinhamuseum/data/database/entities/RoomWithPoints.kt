package com.example.lourinhamuseum.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.fetchfromserver.domain.MuseumRoom

data class RoomWithPoints(
    @Embedded
    val room: DatabaseRoom,
    @Relation(
    parentColumn = "id", entityColumn = DatabasePoint.ROOM_ID, entity =
        DatabasePoint::class)
    val points: List<PointWithSlideshow> = emptyList()
) {
    fun asDomainModel(): MuseumRoom {
        return MuseumRoom(room.id,
            room.name,
            room.title,
            room.order,
            points.asDomainModel()
        )
    }
}

fun List<RoomWithPoints>.asDomainModel(): List<MuseumRoom> {
    return map {
        it.asDomainModel()
    }
}