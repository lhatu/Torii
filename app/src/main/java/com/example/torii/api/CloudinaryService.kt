package com.example.torii.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CloudinaryService {
    @Multipart
    @POST("v1_1/dml7vsbx7/image/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): CloudinaryResponse
}

data class CloudinaryResponse(
    val secure_url: String
)

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.cloudinary.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private val cloudinaryService = retrofit.create(CloudinaryService::class.java)