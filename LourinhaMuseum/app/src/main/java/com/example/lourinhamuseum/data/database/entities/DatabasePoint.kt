package com.example.lourinhamuseum.data.database.entities

import androidx.room.*
import com.example.lourinhamuseum.data.database.entities.DatabasePoint.Companion.POINTS_TABLE
import com.example.lourinhamuseum.data.database.entities.DatabasePoint.Companion.ROOM_ID
import com.example.lourinhamuseum.data.domain.Point


@Entity(
    tableName = POINTS_TABLE,
    foreignKeys = [ForeignKey(
        entity = DatabaseRoom::class,
        parentColumns = ["id"],
        childColumns = [ROOM_ID]
    )],
    indices = [Index(ROOM_ID)]
)
data class DatabasePoint(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = ROOM_ID)
    val roomId: Int,
    val name: String,
    @ColumnInfo(name = "ar_image")
    val arImage: String?,
    @ColumnInfo(name = "list_image")
    val listImage: String,
    @ColumnInfo(name = "social_network_media")
    val socialNetworkMedia: String,
    @ColumnInfo(name = "base_image")
    val baseImage: String,
    val title: String,
    val description: String,
    @ColumnInfo(name = "language_desc_audio")
    val descriptionAudio: String,
    @ColumnInfo(name = "media_sign_language")
    val mediaSignLanguage:String,
    val media: String,
    val isFound: Boolean = false,
    val isQuizComplete: Boolean = false,
    val pointScore: Int = 0

) {

    companion object {
        //foreign key to the room that the point belongs
        const val ROOM_ID = "room_id"
        const val POINTS_TABLE = "rooms_points"
    }

    fun asDomainModel(): Point {
        return Point(
            id,
            roomId,
            name,
            arImage ?: "",
            listImage,
            socialNetworkMedia,
            baseImage,
            title,
            description,
            media,
            descriptionAudio,
            mediaSignLanguage,
            emptyList(),
            emptyList(),
            isFound,
            isQuizComplete,
            pointScore
        )
    }
}