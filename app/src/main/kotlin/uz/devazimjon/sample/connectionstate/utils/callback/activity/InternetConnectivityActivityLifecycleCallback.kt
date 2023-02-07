package uz.devazimjon.sample.connectionstate.utils.callback.activity

import android.app.Activity
import android.app.Application.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Bundle
import uz.devazimjon.sample.connectionstate.utils.delegate.InternetConnectionDelegate
import java.lang.ref.WeakReference

@Suppress("DEPRECATION")
internal object InternetConnectivityActivityLifecycleCallback : ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val broadcastReceiver = InternetConnectionReceiver(WeakReference(activity))
        activity.registerReceiver(broadcastReceiver, IntentFilter(CONNECTIVITY_ACTION))
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
    }

    private class InternetConnectionReceiver(
        private val activityRef: WeakReference<Activity>
    ) : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            val activity = activityRef.get() ?: return
            val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val hasConnection = manager.activeNetworkInfo?.isConnected == true
            InternetConnectionDelegate.updateConnectivityState(activity, hasConnection)
        }
    }
}
