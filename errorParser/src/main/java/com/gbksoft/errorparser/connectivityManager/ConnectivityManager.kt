package com.gbksoft.errorparser.connectivityManager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class ConnectivityManager(context: Context) :
    IConnectivityManager,
    IBaseConnectivityManager {

    private var isConnectionAvailable = false
    private var connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val isOnlineLiveData = MutableLiveData<Boolean>()

    init {
        checkOnStart()
        registerConnectivityListener()
    }

    override fun isOnline(): Boolean {
        return isConnectionAvailable
    }

    private fun registerConnectivityListener() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isConnectionAvailable = true
                onStateChanged()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isConnectionAvailable = false
                onStateChanged()
            }

            override fun onUnavailable() {
                super.onUnavailable()
                isConnectionAvailable = false
                onStateChanged()
            }
        }
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun checkOnStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkAvailability =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            isOnlineLiveData.postValue(
                networkAvailability != null &&
                        networkAvailability.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkAvailability.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            )
        } else {
            val info = connectivityManager.activeNetworkInfo
            isOnlineLiveData.postValue(info != null && info.isConnected)
        }
    }

    private fun onStateChanged() {
        isOnlineLiveData.postValue(isConnectionAvailable)
    }

    override fun setConnectivityListener(
        lifecycleOwner: LifecycleOwner,
        connectivityListener: (Boolean) -> Unit
    ) {
        addListener(lifecycleOwner, connectivityListener)
    }

    private fun addListener(
        lifecycleOwner: LifecycleOwner,
        connectivityListener: (Boolean) -> Unit
    ) {
        isOnlineLiveData.observe(
            lifecycleOwner,
            Observer { isOnline: Boolean? -> connectivityListener.invoke(isOnline!!) })
    }
}