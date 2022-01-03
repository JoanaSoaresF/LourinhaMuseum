package com.example.lourinhamuseum.data.database.entities

import androidx.room.*
import com.example.lourinhamuseum.data.database.entities.DatabaseRoom.Companion.MUSEUM_ID
import com.example.lourinhamuseum.data.database.entities.DatabaseRoom.Companion.ROOMS_TABLE
import com.example.fetchfromserver.domain.MuseumRoom

@Entity(
    tableName = ROOMS_TABLE,
    foreignKeys = [ForeignKey(
        entity = DatabaseMuseum::class,
        parentColumns = ["id"],
        childColumns = [MUSEUM_ID]
    )],
    indices = [Index(MUSEUM_ID)]
)
data class DatabaseRoom(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = MUSEUM_ID)
    val museumId: Int,
    val name: String,
    val title: String,
    val order: Int

) {
    companion object {
        //foreign key to the museum that the room belongs
        const val MUSEUM_ID = "museum_id"
        const val ROOMS_TABLE = "museum_rooms"
    }

    fun asDomainModel(): MuseumRoom {
        return MuseumRoom(
            id,
            name,
            title,
            order,
            emptyList()
        )
    }
}