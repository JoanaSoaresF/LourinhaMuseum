package com.example.lourinhamuseum.data.database

import androidx.room.*
import com.example.lourinhamuseum.data.database.entities.*


@Dao
interface MuseumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMuseum(museum: DatabaseMuseum)

    /**
     * We will only have one museum in the app database so we just want do get that
     * one, and not a list.
     * This will tell room to query all the necessary tables to construct the museum,
     * with contents, rooms and points
     */
    @Transaction
    @Query("SELECT * FROM museum_table LIMIT 1")
    fun getMuseum(): CompleteMuseum

    /**
     * Checks if a museum is already loaded to the database
     */
    @Query("SELECT EXISTS(SELECT * FROM museum_table LIMIT 1)")
    fun hasMuseum(): Boolean

}

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContents(vararg contents: DatabaseContent)

    @Query("select * from museum_content")
    fun getContents(): List<DatabaseContent>
}

@Dao
interface RoomsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRooms(vararg rooms: DatabaseRoom)

    @Query("select * from museum_rooms")
    fun getRooms(): List<DatabaseRoom>

    @Query("select * from museum_rooms where id=:id")
    fun getRoom(id: Int): DatabaseRoom

    @Transaction
    @Query("select * from museum_rooms")
    fun getRoomsWithPoints(): List<RoomWithPoints>
}

@Dao
interface PointsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPoints(vararg points: DatabasePoint)

    @Query("select * from rooms_points where room_id=:roomId")
    fun getPoints(roomId: Int): List<DatabasePoint>
}

@Dao
interface SlideshowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSlideShow(vararg slideshow: DatabaseSlideshow)

    @Query("select * from slideshow_table where point_id=:pointId")
    fun getSlideShows(pointId: Int): List<DatabaseSlideshow>
}

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllQuestions(vararg questions: DatabaseQuestion)

    @Query("select * from questions_table where point_id=:pointId")
    fun getQuestions(pointId: Int): List<DatabaseQuestion>
}

@Dao
interface AnswersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAnswers(vararg answer: DatabaseAnswer)

    @Query("select * from answers_table where question_id=:questionId")
    fun getAnswers(questionId: Int): List<DatabaseAnswer>
}


