package com.gbksoft.errorparser.connectivityManager

import androidx.lifecycle.LifecycleOwner

interface IBaseConnectivityManager {
    fun setConnectivityListener(
        lifecycleOwner: LifecycleOwner,
        connectivityListener: (Boolean) -> Unit
    )
}