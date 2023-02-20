package uz.devazimjon.sample.connectionstate.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.devazimjon.sample.connectionstate.utils.delegate.checkNetworkHasConnection

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
                val hasInternetCapability = capabilities?.hasCapability(NET_CAPABILITY_INTERNET)
                if (hasInternetCapability == true) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val hasConnection = checkNetworkHasConnection()
                        if (hasConnection) {
                            withContext(Dispatchers.Main) {
                                activeConnections.add(network)
                                checkActiveNetworks()
                            }
                        }
                    }
                }
            }

            override fun onLost(network: Network) {
                activeConnections.remove(network)
                checkActiveNetworks()
            }
        }
        return requireNotNull(networkCallback)
    }
}
