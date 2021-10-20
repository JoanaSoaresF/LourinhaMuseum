package com.example.lourinhamuseum.data.network

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import java.io.File

class FileManager(private val applicationContext: Context) {

    companion object{
        const val ERROR = Integer.MIN_VALUE
    }

    private val network = Network.museumRetrofit

    private val _numberFilesTransferred = MutableLiveData<Int>()
    val numberFilesTransferred: LiveData<Int>
        get() = _numberFilesTransferred


    /**
     * Gets a file from the server
     * Saves the file in phone's memory. If the file already exists them isn't downloaded
     * again.
     * @param filename name of the file to download from the server and save
     * @return returns true if the file is successfully saved, or if the
     * file already exists.
     * Returns false otherwise
     */
    private suspend fun saveFile(filename: String): Boolean {
        var success = true
        try {
            val filePath = File(
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                filename
            )
            if (!filePath.exists()) {
                val responseFile = network.downloadFile(filename)
                if (responseFile.isSuccessful && responseFile.body() != null) {
                    filePath.createNewFile()
                    filePath.writeBytes(responseFile.body()!!.bytes())
                }
            }
        } catch (e: Exception) {
            success = false
        }
        return success
    }

    /**
     * Saves the required files to phone's memory
     * @param files files to perform the download
     * @return true if all files were correctly saved
     */
    suspend fun saveFiles(files: List<String>): Boolean {
        var num = 0
        val totalNumFiles = files.size
        _numberFilesTransferred.postValue(num)

        for (file in files) {
            val success = saveFile(file)
            if (success) {
                _numberFilesTransferred.postValue((++num * 100) / totalNumFiles)
            } else {
                Timber.e("Saving file $file failed")
                _numberFilesTransferred.postValue(ERROR)
            }
        }
        return totalNumFiles == num
    }
}