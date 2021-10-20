package com.example.lourinhamuseum.data.network

import com.example.lourinhamuseum.data.network.museumObjects.NetworkMuseum
import com.example.lourinhamuseum.data.network.rankingObjects.NetworkRankingScore
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


/**
 * A retrofit service to fetch the museum information
 */
interface MuseumService {


    @GET("projects/app/{languageCode}/{api_key}")
    suspend fun loadMuseum(
        @Path("languageCode") languageCode: String,
        @Path("api_key") apiKey: String
    ): NetworkMuseum

    @Streaming
    @GET("files/{filename}")
    suspend fun downloadFile(@Path("filename") filename: String): Response<ResponseBody>

    @Headers("Content-type:application/json")
    @POST("statistics/{api_key}")
    suspend fun sendRankingScore(
        @Path("api_key") apiKey: String,
        @Body rankingScore: NetworkRankingScore
    ): Response<ResponseBody>

    @GET("statistics/{api_key}")
    suspend fun getRanking(@Path("api_key") apiKey: String):
            List<NetworkRankingScore>
}

/**
 * Build the Moshi object that Retrofit will be using, making sure to ass the Kotlin
 * adapter for full Kotlin compatibility
 */

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Main entry point fot network access. Call like 'Network.museumService.getMuseum())'
 */
object Network {
    private const val BASE_URL = "https://api.management.realizasom.com/"

        const val API_KEY =
        "LgNBr0bzbYWOXkIJDVi64TETExLlfoO2n79QfZVZ8EKBbBsVkUFRAuLdl6BShTbc1ccFtNHOhMCnKjG8FajDSgu53c6qUO1AnFHF"
//    const val API_KEY =
//        "BYyZSCrDwHNNn98SrTVKoQ5922SoHoVXWbC0rtTfO3lVk5fbn72KTlafLFdkNGlvC4JvJZ5Y7tJYvmhnI1dbLev5FKhQ5RQz3aCw"
    const val LANGUAGE_CODE = "pt"

    //Configure retrofit to parse Json and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val museumRetrofit: MuseumService = retrofit.create(MuseumService::class.java)
}