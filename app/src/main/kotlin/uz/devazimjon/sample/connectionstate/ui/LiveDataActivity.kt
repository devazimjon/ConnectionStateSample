package uz.devazimjon.sample.connectionstate.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import uz.devazimjon.sample.connectionstate.R
import uz.devazimjon.sample.connectionstate.utils.ConnectivityLiveData
import kotlin.properties.Delegates

class LiveDataActivity : AppCompatActivity() {

    private var connectivityLiveData: ConnectivityLiveData by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityLiveData = ConnectivityLiveData(this)
        setContentView(R.layout.activity_live_data)
        val textView = findViewById<TextView>(R.id.connection_text_view)

        connectivityLiveData.observe(this) { isConnected ->
            val text = if (isConnected) {
                getString(R.string.online)
            } else {
                getString(R.string.offline)
            }
            textView.text = text
        }
    }
}
