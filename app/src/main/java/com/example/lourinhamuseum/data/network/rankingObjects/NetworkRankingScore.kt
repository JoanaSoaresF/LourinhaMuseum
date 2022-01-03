package com.example.lourinhamuseum.data.network.rankingObjects

import com.example.lourinhamuseum.data.database.entities.DatabaseScore
import com.squareup.moshi.Json

data class NetworkRankingScore(
    @Json(name = "username")
    val username: String,
    @Json(name = "extra1")
    val userId: String?, // user id
    @Json(name = "extra2")
    val score: String?, //score
    val deviceLanguage: String? = "pt",
    val statisticEnabled: Boolean? = true,
    val detectionEnabled: Boolean? = true,
    val createdAt: String?,
    val downloads: List<Any?>? = emptyList(),
    val sessions: List<Any?>? = emptyList()
) {

    private fun toInt(value: String?): Int {
        var n = 0
        try {
            value?.let { n = it.toInt() }
        } catch (e: Exception) {

        }
        return n
    }

    private fun toLong(value: String?): Long {
        var n = 0L
        try {
            value?.let { n = it.toLong() }
        } catch (e: Exception) {

        }
        return n
    }
    private fun getUsernameSeparated():String{
        return  ""

    }

    fun asDatabaseModel(): DatabaseScore {

        return DatabaseScore(toLong(userId), username, toInt(score))
    }
}

fun List<NetworkRankingScore>.asDatabaseModel(): Array<DatabaseScore> {
    return map { it.asDatabaseModel() }.toTypedArray()
}

