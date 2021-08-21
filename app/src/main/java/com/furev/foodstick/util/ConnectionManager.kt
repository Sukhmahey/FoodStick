package com.furev.foodstick.util

import android.content.Context
import android.net.ConnectivityManager

class ConnectionManager {
    fun checkConnectivity(context: Context):Boolean{
        val connectionManager =context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectionManager.activeNetworkInfo
        if (activeNetwork?.isConnected != null){
            return activeNetwork.isConnected
        }else{
            return false
        }
    }
}