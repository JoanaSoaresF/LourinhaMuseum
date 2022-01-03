package com.example.lourinhamuseum.data.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.lourinhamuseum.data.database.entities.*


@Dao
interface ScoresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScores(vararg score: DatabaseScore)

    @Query("select * from scores_table order by score desc")
    fun getRanking(): LiveData<List<DatabaseScore>>

}

@Database(
    entities = [DatabaseScore::class],
    version = 1
)
abstract class ScoresDatabase : RoomDatabase(){
    abstract val scoresDao : ScoresDao
    companion object {
        private const val SCORE_DATABASE = "score_database"
        /**
         * INSTANCE will keep a reference to any reference returned cia GetInstance
         * This will help avoid repeatedly initializing the database, which is expensive
         */
        private var INSTANCE: ScoresDatabase? = null

        /**
         * Helper function to get a database
         * Is the database is already been retrieved, the previous data base will be
         * returned. Otherwise, create a new instance.
         * @param context The application context Singleton, used to get access to the
         * filesystem
         */
        fun getDatabase(context: Context): ScoresDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ScoresDatabase::class.java,
                        SCORE_DATABASE
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}