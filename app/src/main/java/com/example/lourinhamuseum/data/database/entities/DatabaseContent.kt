package com.example.lourinhamuseum.data.database.entities

import androidx.room.*
import com.example.lourinhamuseum.data.database.entities.DatabaseContent.Companion.CONTENT_TABLE
import com.example.lourinhamuseum.data.database.entities.DatabaseContent.Companion.MUSEUM_ID
import com.example.fetchfromserver.domain.Content


@Entity(
    tableName = CONTENT_TABLE,
    foreignKeys = [ForeignKey(
        entity = DatabaseMuseum::class,
        parentColumns = ["id"],
        childColumns = [DatabaseContent.MUSEUM_ID]
    )],
    indices = [Index(MUSEUM_ID)]
)
data class DatabaseContent(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = DatabaseRoom.MUSEUM_ID)
    val museumId: Int,
    val title: String,
    val content: String
) {
    companion object {
        //foreign key to the museum that the room belongs
        const val MUSEUM_ID = "museum_id"
        const val CONTENT_TABLE = "museum_content"
    }

    fun asDomainModel(): Content {
        return Content(id, title, content)
    }
}





