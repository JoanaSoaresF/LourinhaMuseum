package com.example.lourinhamuseum.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lourinhamuseum.data.database.entities.*
import com.example.lourinhamuseum.data.database.entities.DatabaseContent

@Database(
    entities = [DatabaseMuseum::class,
        DatabaseContent::class,
        DatabaseRoom::class,
        DatabasePoint::class,
        DatabaseSlideshow::class,
        DatabaseQuestion::class,
        DatabaseAnswer::class],
    version = 1
)
abstract class MuseumDatabase : RoomDatabase() {

    /**
     * Connects the database to the DAO's
     */
    abstract val museumDao: MuseumDao
    abstract val contentDao: ContentDao
    abstract val roomsDao: RoomsDao
    abstract val pointsDao: PointsDao
    abstract val slideshowDao: SlideshowDao
    abstract val questionsDao: QuestionDao
    abstract val answersDao: AnswersDao

    companion object {

        private const val MUSEUM_DATABASE = "museum_database"

        /**
         * INSTANCE will keep a reference to any reference returned cia GetInstance
         * This will help avoid repeatedly initializing the database, which is expensive
         */
        private var INSTANCE: MuseumDatabase? = null

        /**
         * Helper function to get a database
         * Is the database is already been retrieved, the previous data base will be
         * returned. Otherwise, create a new instance.
         * @param context The application context Singleton, used to get access to the
         * filesystem
         */
        fun getDatabase(context: Context): MuseumDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MuseumDatabase::class.java,
                        MUSEUM_DATABASE
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }


}


