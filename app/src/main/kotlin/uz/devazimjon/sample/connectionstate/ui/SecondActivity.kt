package uz.devazimjon.sample.connectionstate.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.devazimjon.sample.connectionstate.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private var _viewBinding: ActivitySecondBinding? = null
    private val viewBinding get() = requireNotNull(_viewBinding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.button.setOnClickListener {
            Intent(this, LiveDataActivity::class.java).let(::startActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding = null
    }
}
