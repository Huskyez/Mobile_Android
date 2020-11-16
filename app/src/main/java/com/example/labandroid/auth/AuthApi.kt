package com.example.labandroid.auth

import com.example.labandroid.utils.API
import retrofit2.http.Body
import retrofit2.http.POST

object AuthApi {

    interface AuthService {
        @POST("api/auth/login")
        suspend fun login(@Body user : User) : Token
    }

    val authService: AuthService = API.retrofit.create(AuthService::class.java)
}