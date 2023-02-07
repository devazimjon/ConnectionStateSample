package uz.devazimjon.sample.connectionstate.app

import android.app.Application
import uz.devazimjon.sample.connectionstate.utils.callback.activity.InternetConnectivityActivityLifecycleCallback

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(InternetConnectivityActivityLifecycleCallback)
    }
}
