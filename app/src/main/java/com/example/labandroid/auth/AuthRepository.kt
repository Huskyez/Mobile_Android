package com.example.labandroid.auth


import android.util.Log
import com.example.labandroid.utils.API
import com.example.labandroid.utils.Result
import com.example.labandroid.utils.TAG
import java.lang.Exception


object AuthRepository {

    var user : User? = null

    val isLoggedIn : Boolean
        get() = user != null


    fun logout() {
        user = null
        API.tokenInterceptor.token = null
    }

    suspend fun login(username : String, password : String) : Result<Token> {

        return try {
            val token = AuthApi.authService.login(User(username, password))
            Log.d(TAG, "token: $token")
            Result.Success(token)
        } catch (ex : Exception) {
            Result.Error(ex)
        }

    }

}