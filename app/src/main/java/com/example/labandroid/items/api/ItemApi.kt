package com.example.labandroid.items.api

import com.example.labandroid.items.data.Item
import com.example.labandroid.utils.API
import com.google.gson.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ItemApi {

    interface ApiService {

        @GET("api/item")
        suspend fun getItems() : List<Item>

        @GET("api/item/{id}")
        suspend fun getItem(@Path("id") id: String) : Item

        @POST("api/item")
        suspend fun createItem(@Body item: Item) : Item

        @PUT("api/item/{id}")
        suspend fun updateItem(@Path("id") id: String, @Body item: Item) : Item
    }


    val itemService: ApiService = API.retrofit.create(ApiService::class.java)
}