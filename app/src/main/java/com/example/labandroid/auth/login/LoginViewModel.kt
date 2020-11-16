package com.example.labandroid.auth.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labandroid.auth.AuthRepository
import com.example.labandroid.auth.Token
import com.example.labandroid.utils.API
import com.example.labandroid.utils.TAG
import com.example.labandroid.utils.Result
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val mutableLoginState : MutableLiveData<LoginState> = MutableLiveData()
    val loginState : LiveData<LoginState> = mutableLoginState

    private val mutableResult : MutableLiveData<Result<Token>> = MutableLiveData()
    val result : LiveData<Result<Token>> = mutableResult


    fun login(username : String, password : String) {
        viewModelScope.launch {
            Log.d(TAG, "login")
            val loginResult = AuthRepository.login(username, password)
            if (loginResult is Result.Success) {
                API.tokenInterceptor.token = loginResult.data.token
                Log.d(TAG, "token value: ${API.tokenInterceptor.token}")
            }
            mutableResult.value = loginResult
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            mutableLoginState.value = LoginState(usernameError = "Invalid Username")
        } else if (!isPasswordValid(password)) {
            mutableLoginState.value = LoginState(passwordError = "Invalid Password")
        } else {
            mutableLoginState.value = LoginState(isValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotEmpty()
    }
}