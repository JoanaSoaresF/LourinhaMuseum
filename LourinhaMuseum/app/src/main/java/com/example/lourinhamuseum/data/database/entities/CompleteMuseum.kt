package com.example.lourinhamuseum.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.lourinhamuseum.data.domain.Museum


data class CompleteMuseum(
    @Embedded
    val museum: DatabaseMuseum,
    @Relation(parentColumn = "id", entityColumn = DatabaseContent.MUSEUM_ID)
    val contents: List<DatabaseContent> = emptyList(),
    @Relation(
        parentColumn = "id", entityColumn = DatabaseRoom.MUSEUM_ID, entity =
        DatabaseRoom::class
    )
    val rooms: List<RoomWithPoints> = emptyList()
) {

    fun asDomainModel(): Museum {

        return Museum(
            museum.id,
            museum.title,
            contents.map { it.asDomainModel() },
            rooms.asDomainModel(),
            score = museum.score
        )
    }
}