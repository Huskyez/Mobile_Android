package com.example.labandroid.auth.remote


import android.util.Log
import com.example.labandroid.auth.Token
import com.example.labandroid.auth.User
import com.example.labandroid.utils.API
import com.example.labandroid.utils.Result
import com.example.labandroid.utils.TAG
import java.lang.Exception


object AuthProxy {

    var user : User? = null

    val isLoggedIn : Boolean
        get() = API.tokenInterceptor.token != null


    fun logout() {
//        user = null
        API.tokenInterceptor.token = null
    }

    suspend fun login(username : String, password : String) : Result<Token> {

        return try {
            val token = AuthApi.authService.login(User(username, password))
            Log.d(TAG, "Received token: $token")
            Result.Success(token)
        } catch (ex : Exception) {
            Result.Error(ex)
        }

    }

}