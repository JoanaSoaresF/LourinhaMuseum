package com.example.lourinhamuseum.data.database.entities

import androidx.room.*
import com.example.lourinhamuseum.data.database.entities.DatabaseSlideshow.Companion.POINT_ID
import com.example.lourinhamuseum.data.database.entities.DatabaseSlideshow.Companion.SLIDESHOW_TABLE
import com.example.lourinhamuseum.data.domain.Slideshow

@Entity(
    tableName = SLIDESHOW_TABLE,
    foreignKeys = [ForeignKey(
        entity = DatabasePoint::class,
        parentColumns = ["id"],
        childColumns = [POINT_ID]
    )],
    indices = [Index(POINT_ID)]
)
data class DatabaseSlideshow(
    @ColumnInfo(name = POINT_ID)
    val point: Int,
    @PrimaryKey
    val id: Int,
    val name: String,
    val url: String,
    val time: Int,
    val caption: String
) {
    companion object {
        const val SLIDESHOW_TABLE = "slideshow_table"
        const val POINT_ID = "point_id"
    }

    fun asDomainModel(): Slideshow {
        return Slideshow(id, name, url, time, caption)
    }

}

fun List<DatabaseSlideshow>.asDomainModel(): List<Slideshow> {
    return map {
        it.asDomainModel()
    }

}