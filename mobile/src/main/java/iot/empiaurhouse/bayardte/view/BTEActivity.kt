package iot.empiaurhouse.bayardte.view

import android.os.Bundle
import android.transition.Slide
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import iot.empiaurhouse.bayardte.databinding.ActivityBteactivityBinding

class BTEActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBteactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBteactivityBinding.inflate(layoutInflater)
        val viewSetup = binding.root
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Slide()
            enterTransition.duration = 769
        }
        setContentView(viewSetup)
        initView()
    }


    private fun initView() {
        binding.infoClose.setOnClickListener {
            finish()
        }
        binding.bayardInfoButton.setOnClickListener {
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

}

