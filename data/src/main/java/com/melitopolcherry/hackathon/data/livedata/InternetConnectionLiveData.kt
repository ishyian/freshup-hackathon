package com.melitopolcherry.hackathon.data.livedata

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import com.melitopolcherry.hackathon.data.models.ConnectionModel

class InternetConnectionLiveData(private val context: Context) : LiveData<ConnectionModel>() {

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities)
            if (actNw != null) {
                when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        postValue(ConnectionModel(NetworkCapabilities.TRANSPORT_WIFI, true))
                    }
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        postValue(ConnectionModel(NetworkCapabilities.TRANSPORT_CELLULAR, true))
                    }
                }
            }
        }
    }

    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, filter)
    }

    override fun onInactive() {
        context.unregisterReceiver(networkReceiver)
        super.onInactive()
    }
}