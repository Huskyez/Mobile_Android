package com.example.labandroid.auth.login

data class LoginState (
    var usernameError : String? = null,
    var passwordError : String? = null,
    var isValid : Boolean = false
)
