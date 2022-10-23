package iot.empiaurhouse.bayardte.view

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import iot.empiaurhouse.bayardte.R
import iot.empiaurhouse.bayardte.databinding.ActivityInitBinding
import iot.empiaurhouse.bayardte.utils.TypeWriterTextView
import iot.empiaurhouse.bayardte.viewmodel.TEMarkerViewModel
import java.util.*

class InitActivity : AppCompatActivity() {
    private lateinit var fadeInAnimation : Animation
    private lateinit var floaterAnimation : Animation
    private lateinit var rotationAnimation : Animation
    private lateinit var binding: ActivityInitBinding
    private lateinit var greetingText: TypeWriterTextView
    private lateinit var vendorTitle: TypeWriterTextView
    private lateinit var vendorSubTitle: TypeWriterTextView
    private lateinit var greeting: String
    private lateinit var teMarkerViewModel: TEMarkerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitBinding.inflate(layoutInflater)
        val viewLaunch = binding.root
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Fade()
            enterTransition.duration = 1000
            exitTransition = Fade()
            exitTransition.duration = 1200
        }
        setContentView(viewLaunch)
        val app = this.application
        teMarkerViewModel = ViewModelProvider(this)[TEMarkerViewModel::class.java]
        teMarkerViewModel.processTEMarkers(app)
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fader)
        rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        floaterAnimation = AnimationUtils.loadAnimation(this, R.anim.floater)
        greetingText = binding.vantaIntroGreeting
        vendorTitle = binding.bayardIntroLogo
        vendorSubTitle = binding.bayardIntroSubtitle
        Handler(Looper.getMainLooper()).postDelayed({
            binding.introLogo.startAnimation(fadeInAnimation)
        }, 666)
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                binding.bayardIntroLogo.visibility = View.VISIBLE
                binding.bayardIntroLogo.startAnimation(floaterAnimation)
                binding.vantaIntroProgress.visibility = View.VISIBLE
                binding.vantaIntroProgress.setColorFilter(Color.parseColor("#00FFFFFF"))
                binding.vantaIntroProgress.startAnimation(rotationAnimation)
                binding.bayardIntroLogo.clearAnimation()
            }
            override fun onAnimationStart(animation: Animation?) {
                greetingText.visibility = View.VISIBLE
                typeWriterText(fetchGreeting(), 131, greetingText)
                Handler(Looper.getMainLooper()).postDelayed({
                    typeWriterText(getString(R.string.bayard), 131, vendorTitle )
                    vendorTitle.visibility = View.VISIBLE }, 666)
                Handler(Looper.getMainLooper()).postDelayed({
                    typeWriterText(getString(R.string.m_e_time_experience), 131, vendorSubTitle )
                    vendorSubTitle.visibility = View.VISIBLE }, 1111)
            }
        })
        rotationAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {

            }
            override fun onAnimationStart(animation: Animation?) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(
                        Intent(this@InitActivity, TEActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this@InitActivity).toBundle()
                    )
                }, 6666)
            }
        })

    }

    private fun typeWriterText(typeText: String, charDelay: Int, targetTextView: TypeWriterTextView) {
        targetTextView.setCharacterDelay(charDelay.toLong())
        targetTextView.displayTextWithAnimation(typeText)
    }

    private fun fetchGreeting(): String{
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        println("\t\t OS Time >> $day/$month/$year -- $hour:$minute")
        if (month == 12 && day == 25){
            greeting = "Merry Christmas"
        }
        if (hour in 0..11){
            greeting = "Good Morning"
        }
        if (hour in 12..17) {
            greeting = "Good Afternoon"
        }
        if (hour in 18..24) {
            greeting = "Good Evening"
        }

        return greeting
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

    override fun onBackPressed()
    {
        moveTaskToBack(true)
    }

}