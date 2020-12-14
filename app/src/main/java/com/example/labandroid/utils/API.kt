package com.example.labandroid.utils

import com.google.gson.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object API {

    const val HOST = "192.168.1.2:3000"

    private const val BASE_URL = "http://$HOST/"

    var tokenInterceptor = TokenInterceptor()

    private val client = OkHttpClient.Builder()
        .apply { addInterceptor(tokenInterceptor) }
        .apply { addInterceptor(HttpLoggingInterceptor()) }
        .build()


    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer {
                jsonElement: JsonElement, _: Type, _: JsonDeserializationContext ->
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            return@JsonDeserializer LocalDateTime.parse(jsonElement.asString, formatter)
        })
        .registerTypeAdapter(LocalDateTime::class.java, JsonSerializer {
                param: LocalDateTime, _: Type, _: JsonSerializationContext ->
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val parser = JsonParser()
            return@JsonSerializer parser.parse("\"" + param.format(formatter) + "\"")
        })
        .create()


    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
}