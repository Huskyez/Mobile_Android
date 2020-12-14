package com.example.labandroid.auth.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.labandroid.R
import com.example.labandroid.auth.Token
import com.example.labandroid.utils.API
import com.example.labandroid.utils.Result
import com.example.labandroid.utils.TAG
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var viewModel : LoginViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        checkToken()

        setupLoginForm()
    }

    private fun checkToken() {
        val sharedPrefs = activity?.getSharedPreferences(getString(R.string.shared_prefs_file), 0)

        // API should also return a expiration date for the token
        // which should be checked here and updated in case there is
        // an arbitrary amount of time left until the token expires
        if (sharedPrefs != null) {
            if (sharedPrefs.contains("token")) {
                API.tokenInterceptor.token = sharedPrefs.getString("token", "")
                Log.d(TAG, "Token: ${API.tokenInterceptor.token}")
                findNavController().navigate(R.id.ItemMasterFragment)
            }
        }
    }

    private fun setupLoginForm() {
        viewModel.loginState.observe(viewLifecycleOwner, { loginState ->
            login.isEnabled = loginState.isValid
            if (loginState.usernameError != null) {
                username.error = loginState.usernameError!!
            }
            if (loginState.passwordError != null) {
                password.error = loginState.passwordError!!
            }
        })
        viewModel.result.observe(viewLifecycleOwner, { loginResult ->
            loading.visibility = View.GONE
            if (loginResult is Result.Success<Token>) {

                // on successful login save the token to shared preferences
                val sharedPrefs = activity?.getSharedPreferences(getString(R.string.shared_prefs_file), 0)
                sharedPrefs?.edit()?.putString("token", loginResult.data.token)?.apply()


                findNavController().navigate(R.id.ItemMasterFragment)
            } else if (loginResult is Result.Error) {
                error_text.text = "Login Error: ${loginResult.exception.message}"
                error_text.visibility = View.VISIBLE
            }
        })

        username.afterTextChanged {
            viewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }
        password.afterTextChanged {
            viewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }
        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            error_text.visibility = View.GONE
            viewModel.login(username.text.toString(), password.text.toString())
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
