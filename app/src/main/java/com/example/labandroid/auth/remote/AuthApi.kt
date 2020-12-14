package com.example.labandroid.auth.remote

import com.example.labandroid.auth.Token
import com.example.labandroid.auth.User
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