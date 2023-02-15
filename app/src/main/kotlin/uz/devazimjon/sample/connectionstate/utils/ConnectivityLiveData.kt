package uz.devazimjon.sample.connectionstate.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class ConnectivityLiveData(context: Context) : LiveData<Boolean>() {
    private val activeConnections = mutableSetOf<Network>()
    private var networkCallback: NetworkCallback? = null
    private val cm by lazy { context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager }

    override fun onActive() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        val networkCallback = createNetworkCallback()
        cm.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        networkCallback?.let { cm.unregisterNetworkCallback(it) }
        networkCallback = null
    }

    private fun checkActiveNetworks() {
        postValue(activeConnections.size > 0)
    }

    private fun createNetworkCallback(): NetworkCallback {
        networkCallback?.let { return it }
        networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                val capabilities = cm.getNetworkCapabilities(network)
                val hasConnection = capabilities?.hasCapability(NET_CAPABILITY_INTERNET)
                if (hasConnection == true) {
                    activeConnections.add(network)
                }
                checkActiveNetworks()
            }

            override fun onLost(network: Network) {
                activeConnections.remove(network)
                checkActiveNetworks()
            }
        }
        return requireNotNull(networkCallback)
    }
}
