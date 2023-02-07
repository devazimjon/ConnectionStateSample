package uz.devazimjon.sample.connectionstate.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.devazimjon.sample.connectionstate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _viewBinding: ActivityMainBinding? = null
    private val viewBinding get() = requireNotNull(_viewBinding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.button.setOnClickListener {
            Intent(this, SecondActivity::class.java).let(::startActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding = null
    }
}
