package iot.empiaurhouse.bayardte.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Slide
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import iot.empiaurhouse.bayardte.databinding.ActivityAboutMeactivityBinding

class AboutMEActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutMeactivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutMeactivityBinding.inflate(layoutInflater)
        val viewSetup = binding.root
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Slide()
            enterTransition.duration = 769

        }
        setContentView(viewSetup)
        initView()
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

    private fun initView(){
        binding.infoClose.setOnClickListener {
            finish()
        }
        binding.meInfoButton.setOnClickListener {
            finish()
        }
        binding.websiteUrl.setOnClickListener {
            val url = "https://www.mekaegwim.ca/"
            val websiteIntent = Intent(Intent.ACTION_VIEW)
            websiteIntent.data = Uri.parse(url)
            startActivity(websiteIntent)
        }
        binding.instaText.setOnClickListener {
            val url = "https://www.instagram.com/chromito.me"
            val websiteIntent = Intent(Intent.ACTION_VIEW)
            websiteIntent.data = Uri.parse(url)
            startActivity(websiteIntent)
        }
        binding.githubText.setOnClickListener {
            val url = "https://github.com/emeraldemperaur"
            val websiteIntent = Intent(Intent.ACTION_VIEW)
            websiteIntent.data = Uri.parse(url)
            startActivity(websiteIntent)
        }
    }

}