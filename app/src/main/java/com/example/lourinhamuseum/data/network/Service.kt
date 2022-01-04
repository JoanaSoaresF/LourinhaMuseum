package com.example.lourinhamuseum.data.network

import com.example.lourinhamuseum.data.network.museumObjects.NetworkMuseum
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming


/**
 * A retrofit service to fetch the museum information
 */
interface MuseumService {


    @GET("Data.json")
    suspend fun loadMuseum(): NetworkMuseum

    @Streaming
    @GET("Files/{filename}")
    suspend fun downloadFile(@Path("filename") filename: String): Response<ResponseBody>
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
    private const val BASE_URL = "https://museu-lourinha-support-gmlourenco.vercel.app/"


    //Configure retrofit to parse Json and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val museumRetrofit: MuseumService = retrofit.create(MuseumService::class.java)
}