package com.example.labandroid.utils


import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData

class ConnectivityLiveData(private val connectivityManager: ConnectivityManager):
    LiveData<Boolean>() {
    
//    constructor(application: Application): this(application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

    private val networkCallback =
        object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                postValue(true)
            }

            override fun onLost(network: Network) {
                postValue(false)
            }
        }

    override fun onActive() {
        super.onActive()
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}