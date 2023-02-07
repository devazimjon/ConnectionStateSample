package uz.devazimjon.sample.connectionstate.utils.callback.activity

import android.Manifest
import android.app.Activity
import android.app.Application.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Global.AIRPLANE_MODE_ON
import android.provider.Settings.System
import android.telephony.PhoneStateListener
import android.telephony.PhoneStateListener.LISTEN_USER_MOBILE_DATA_STATE
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import uz.devazimjon.sample.connectionstate.ui.SecondActivity
import uz.devazimjon.sample.connectionstate.utils.delegate.InternetConnectionDelegate
import java.lang.ref.WeakReference

private const val ACTION_AIRPLANE_MODE = "android.intent.action.AIRPLANE_MODE"

@Suppress("DEPRECATION")
internal object InternetConnectivityActivityLifecycleCallback : ActivityLifecycleCallbacks {
    private val internetConnectionReceiverMap = mutableMapOf<String, BroadcastReceiver>()
    private val telephonyCallbacksMap = mutableMapOf<String, TelephonyCallback>()
    private val phoneStateListenersMap = mutableMapOf<String, PhoneStateListener>()
    private var isMobileDataEnabled = false

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activityList.none { it.isInstance(activity) }) return

        val broadcastReceiver = InternetConnectionReceiver {
            onConnectionChanged(WeakReference(activity))
        }
        internetConnectionReceiverMap[activity::class.java.simpleName] = broadcastReceiver
        activity.registerReceiver(broadcastReceiver, intentFilters)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val callback = object : TelephonyCallback(),
                TelephonyCallback.UserMobileDataStateListener {
                override fun onUserMobileDataStateChanged(enabled: Boolean) {
                    isMobileDataEnabled = enabled
                    onConnectionChanged(WeakReference(activity))
                }
            }
            activity.telephonyManager.registerTelephonyCallback(activity.mainExecutor, callback)
            telephonyCallbacksMap[activity::class.java.simpleName] = callback
        } else {
            object : PhoneStateListener() {
                override fun onUserMobileDataStateChanged(enabled: Boolean) {
                    isMobileDataEnabled = enabled
                    onConnectionChanged(WeakReference(activity))
                }
            }.let {
                activity.telephonyManager.listen(it, LISTEN_USER_MOBILE_DATA_STATE)
                phoneStateListenersMap[activity::class.java.simpleName] = it
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        internetConnectionReceiverMap.remove(activity::class.java.simpleName)
            ?.let(activity::unregisterReceiver)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallbacksMap.remove(activity::class.java.simpleName)?.let {
                activity.telephonyManager.unregisterTelephonyCallback(it)
            }
        } else {
            phoneStateListenersMap.remove(activity::class.java.simpleName)
        }
    }

    private fun onConnectionChanged(activityRef: WeakReference<Activity>) {
        val activity = activityRef.get() ?: return
        val isMobileDataEnabled = checkMobileDataEnabled(activity)
        val isAirModeEnabled = System.getInt(activity.contentResolver, AIRPLANE_MODE_ON, 0) != 0
        val hasConnection = checkInternetConnection(activity)

        val isConnected = hasConnection || (!isAirModeEnabled && isMobileDataEnabled)
        InternetConnectionDelegate.updateConnectivityState(activity, isConnected)
    }

    private fun checkMobileDataEnabled(context: Context): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
            || Build.VERSION.SDK_INT < Build.VERSION_CODES.O
        ) {
            return isMobileDataEnabled
        }

        val telephonyManager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.isDataEnabled
    }

    private fun checkInternetConnection(context: Context): Boolean {
        val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetworkInfo?.isConnected == true
    }

    private val Activity.telephonyManager
        get() = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

    private class InternetConnectionReceiver(
        private val onReceiveIntent: () -> Unit
    ) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            onReceiveIntent()
        }
    }

    private val activityList
        get() = listOf(
            SecondActivity::class
        )

    private val intentFilters
        get() = IntentFilter().apply {
            addAction(CONNECTIVITY_ACTION)
            addAction(ACTION_AIRPLANE_MODE)
        }
}
