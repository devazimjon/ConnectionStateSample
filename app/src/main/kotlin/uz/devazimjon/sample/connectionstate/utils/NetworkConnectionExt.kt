package uz.devazimjon.sample.connectionstate.utils.delegate

import java.net.InetSocketAddress
import java.net.Socket

private const val GOOGLE_SITE_IP = "8:8:8:8"
private const val GOOGLE_SITE_PORT = 53
private const val CONNECTION_TIMEOUT = 1500

fun checkNetworkHasConnection(): Boolean {
    return runCatching {
        val socket = Socket()
        socket.connect(InetSocketAddress(GOOGLE_SITE_IP, GOOGLE_SITE_PORT), CONNECTION_TIMEOUT)
        socket.close()
        true
    }.getOrElse { false }
}
