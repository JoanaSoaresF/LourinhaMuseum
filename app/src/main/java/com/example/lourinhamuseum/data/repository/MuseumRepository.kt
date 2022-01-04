package com.example.lourinhamuseum.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.lourinhamuseum.data.SharedPreferencesManager
import com.example.lourinhamuseum.data.database.MuseumDatabase
import com.example.lourinhamuseum.data.database.ScoresDatabase
import com.example.lourinhamuseum.data.database.entities.asDomainModel
import com.example.lourinhamuseum.data.domain.*
import com.example.lourinhamuseum.data.network.FileManager
import com.example.lourinhamuseum.data.network.Network
import com.example.lourinhamuseum.data.network.museumObjects.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


class MuseumRepository private constructor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val database: MuseumDatabase = MuseumDatabase.getDatabase(context)

    private val rankingDatabase: ScoresDatabase = ScoresDatabase.getDatabase(context)


    var username = SharedPreferencesManager.getUserName(context)
    var userId = SharedPreferencesManager.getUserID(context)
    val isUserDefined: Boolean
        get() = userId != null && username != null
    var createAt = SharedPreferencesManager.getCreateAt(context)


    /**
     * List off all scores to show
     */
    val ranking: LiveData<List<Score>> =
        Transformations.map(rankingDatabase.scoresDao.getRanking()) {
            it.asDomainModel()
        }

    companion object {
        /**
         * INSTANCE will keep a reference to any reference returned via GetInstance
         * This will help avoid repeatedly initializing the Repository and load the
         * museum from the database, which is expensive
         */
        @Volatile
        private var INSTANCE: MuseumRepository? = null


        /**
         * Helper function to get a repository
         * If the repository is already been created, the previous repository will be
         * returned. Otherwise, create a new instance.
         * @param context The application context Singleton, used to get access to the
         * filesystem
         */
        fun getMuseumRepository(context: Context):
                MuseumRepository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = MuseumRepository(context)
                    INSTANCE = instance
                }
                return instance
            }
        }
    }


    /**
     * An object representing the museum an all of its information
     */
    var museum: Museum? = null

    /**
     * Manager to perform the download files and saving them to the phone's memory
     */
    private val fileManager = FileManager(context)


    /**
     * Loads the museum in to a domain object
     */
    private suspend fun loadMuseum() {
        return withContext(Dispatchers.IO) {
            if (!database.museumDao.hasMuseum()) {
                //if we don't have the museum in the database we did to retrieve it from
                // the server
                updateDatabase()
            }
            getMuseumFromDatabase()

        }
    }

    /**
     * Gets the museum data from the repository. If the museum data is already fetched
     * returns it, else will fetch the data first
     */
    suspend fun getMuseum(): Museum? {
        if (museum == null) {
            loadMuseum()
        }
        return museum
    }

    /**
     * Contacts the sever and gets the museum data.
     * Uses the data pulled from the server to construct da database representing the
     * museum.
     * Gets the museum information, all the rooms of the museum and all the points in
     * which room.
     * Perform with coroutines so it does not block the main thread
     */
    private suspend fun updateDatabase() {

        Timber.d("Load museum network available ${isNetworkAvailable()}")
        if (isNetworkAvailable()) {
            val networkMuseum =
                Network.museumRetrofit.loadMuseum()
            //insert museum into database
            val databaseMuseum = networkMuseum.asDatabaseModel()
            database.museumDao.insertMuseum(databaseMuseum)
            //insert contents into database
            val databaseContent =
                networkMuseum.contents?.asDatabaseModel(networkMuseum.museumId)
            databaseContent?.toTypedArray()
                ?.let { database.contentDao.insertAllContents(*it) }
            //insert rooms into database
            val rooms = networkMuseum.rooms
            val databaseRoom = rooms.asDatabaseModel(networkMuseum.museumId)
            database.roomsDao.insertAllRooms(*databaseRoom.toTypedArray())
            //insert points to database
            for (room in rooms) {
                val points = room.points.asDatabaseModel(room.id)
                database.pointsDao.insertAllPoints(*points.toTypedArray())
                for (point in room.points) {
                    val slideshow = point.slideshow.asDatabaseModel(point.id)
                    database.slideshowDao.insertAllSlideShow(*slideshow.toTypedArray())
                    val questions = point.questions.asDatabaseModel(point.id)
                    database.questionsDao.insertAllQuestions(*questions.toTypedArray())
                    for (question in point.questions) {
                        val answers = question.answers.asDatabaseModel(question.id)
                        database.answersDao.insertAllAnswers(*answers.toTypedArray())
                    }
                }
            }
        }

    }

    /**
     * Constructs a museum object from the data base. The museum object has all the
     * rooms and points information
     * Perform with coroutines so it does not block the main thread
     */
    private fun getMuseumFromDatabase() {
        if (database.museumDao.hasMuseum()) {
            museum = database.museumDao.getMuseum().asDomainModel()
        }
    }

    /**
     * Downloads all the necessary images and audio files to the museum presentation
     * Performed with coroutines so it does not block the main thread
     */
    suspend fun downloadALlFiles(): Boolean {
        if (isNetworkAvailable()) {
            return withContext(Dispatchers.IO) {
                var success = false
                museum?.let { museum ->
                    val files = museum.filesToDownload
                    success = fileManager.saveFiles(files)
                }
                return@withContext success
            }
        } else {
            return false
        }
    }

    fun getNumberFilesTransferred(): LiveData<Int> {
        return fileManager.numberFilesTransferred
    }

    /**
     * Saves the information of the point in the database, updating the values of
     * [Point.isFound] or/and [Point.isQuizCorrect]
     * @param point point to update in tne database
     *
     */
    suspend fun updatePointInDatabase(point: Point) {
        withContext(Dispatchers.IO) {
            database.pointsDao.insertAllPoints(point.asDatabaseModel())
        }
    }

    /**
     * Saves the information of the point in the database, updating the values of
     * [Point.isFound] or/and [Point.isQuizCorrect]
     * Saves the information of the question in the database, updating the values of
     * [Question.isAnsweredCorrectly]
     * Saves the information of the museum in the database, updating the values of
     * [Museum.score]
     * @param point point to update in tne database
     *
     */
    suspend fun updateScoreInDatabase(point: Point) {
        withContext(Dispatchers.IO) {
            database.pointsDao.insertAllPoints(point.asDatabaseModel())
            database.questionsDao.insertAllQuestions(
                *point.questions.asDatabaseModel().toTypedArray()
            )
            museum?.asDatabaseMuseum()?.let { database.museumDao.insertMuseum(it) }
        }
    }


    /**
     * Saves the username selected to the shared preferences
     * @param username username to save
     *
     */
    fun saveUsername(context: Context, username: String) {
        if (!isUserDefined) {
            this.userId = SharedPreferencesManager.saveUsername(context, username)
            this.username = username
            this.createAt = SharedPreferencesManager.getCreateAt(context)
        }
    }


    /**
     * Verifies if the phone is connected to internet
     */
    private fun isNetworkAvailable(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected == true
        }

        return false
    }

}
