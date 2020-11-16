package com.example.labandroid.utils

import okhttp3.Interceptor
import okhttp3.Response


class TokenInterceptor : Interceptor {

    var token : String? = null

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        if (token == null) {
            return chain.proceed(request)
        }

        val newRequest = request.newBuilder().addHeader("Authorization", "Bearer $token").build()
        return chain.proceed(newRequest)
    }


}