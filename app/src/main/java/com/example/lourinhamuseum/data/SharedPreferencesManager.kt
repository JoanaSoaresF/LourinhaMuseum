package com.example.lourinhamuseum.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class SharedPreferencesManager {


    companion object {
        private const val MUSEUM_PREFERENCES = "museum_vuforia_preferences"
        private const val DOWNLOAD_DONE_PREF = "download_done_preference"
        private const val USERNAME_PREFERENCE = "username_preference"
        private const val ID_PREFERENCE = "id_username_preference"
        private const val CREATE_DATE_PREFERENCE = "createdAt_preference"

        /**
         * Saves in a shared preference that the download of the files is done, do it
         * isn't necessary to do it again
         */
        fun setFilesDownloadDone(context: Context) {
            val preferences = getSharedPreferences(context)

            val editor = preferences.edit()
            editor.putBoolean(DOWNLOAD_DONE_PREF, true)
            editor.apply()

        }

        /**
         * Retrieves the shared preference saving the information if the files are
         * already downloaded. If there isn't a shared preference them the files are
         * not downloaded and returns false
         */
        fun isDownloadDone(context: Context): Boolean {
            val preferences = getSharedPreferences(context)
            return preferences.getBoolean(DOWNLOAD_DONE_PREF, false)

        }

        /**
         * Retrieves the username saved in a shared preference. If the isn't a username
         * saved returns null
         */
        fun getUserName(context: Context): String? {
            val preferences = getSharedPreferences(context)
            return preferences.getString(USERNAME_PREFERENCE, null)

        }

        /**
         * Saves in a shared preference the username selected,
         * generates a unique user id
         * @return user id generated
         */
        fun saveUsername(context: Context, username: String) : String {
            val preferences = getSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(USERNAME_PREFERENCE, username)
            editor.apply()
            return saveUserID(context)
        }

        fun getUserID(context: Context): String? {
            val preferences = getSharedPreferences(context)
            return preferences.getString(ID_PREFERENCE, null)
        }

        fun getCreateAt(context: Context): String? {
            val preferences = getSharedPreferences(context)
            return preferences.getString(CREATE_DATE_PREFERENCE, null)
        }

        /**
         * Generates a unique user id using nanotime and saves that id in shared
         * preferences.
         * @return the generated id
         */
        private fun saveUserID(context: Context) : String{
            val preferences = getSharedPreferences(context)
            val editor = preferences.edit()
            val id = System.nanoTime().toString()
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
            editor.putString(CREATE_DATE_PREFERENCE, date)
            editor.putString(ID_PREFERENCE, id)
            editor.apply()
            return id
        }

        private fun getSharedPreferences(context: Context) =
            context.getSharedPreferences(MUSEUM_PREFERENCES, 0)
    }

}