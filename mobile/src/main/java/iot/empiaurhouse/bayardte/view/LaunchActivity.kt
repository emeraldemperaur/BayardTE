package iot.empiaurhouse.bayardte.view

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import iot.empiaurhouse.bayardte.R
import iot.empiaurhouse.bayardte.databinding.ActivityLaunchBinding

class LaunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchBinding
    private lateinit var fadeInAnimation : Animation
    private lateinit var rotationAnimation : Animation
    private lateinit var deRotationAnimation : Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        val viewLaunch = binding.root
        setContentView(viewLaunch)
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        deRotationAnimation = AnimationUtils.loadAnimation(this, R.anim.derotate)
        binding.devIntroLogo.visibility = View.INVISIBLE
        binding.devIntroLogoSpinner.visibility = View.INVISIBLE
        binding.devIntroLogo.startAnimation(fadeInAnimation)
        binding.devIntroLogoSpinner.startAnimation(fadeInAnimation)
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.devIntroLogo.startAnimation(rotationAnimation)
                binding.devIntroLogo.setColorFilter(Color.parseColor("#000000"))
                binding.devIntroLogoSpinner.startAnimation(deRotationAnimation)
                binding.devIntroLogoSpinner.setColorFilter(Color.parseColor("#000000"))
                Handler(Looper.getMainLooper()).postDelayed({
                    if (ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        startActivity(
                            Intent(this@LaunchActivity, InitActivity::class.java),
                            ActivityOptions.makeSceneTransitionAnimation(this@LaunchActivity)
                                .toBundle()
                        )
                        finish()
                    }else if (ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED
                            ){
                        requestBTEPermissions()
                    }
                }, 3000)
            }

            override fun onAnimationStart(animation: Animation?) {
                binding.devIntroLogo.setColorFilter(Color.parseColor("#000000"))
                binding.devIntroLogoSpinner.setColorFilter(Color.parseColor("#000000"))
                binding.devIntroLogo.visibility = View.VISIBLE
                binding.devIntroLogoSpinner.visibility = View.VISIBLE

            }
        })

    }

    private fun requestBTEPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) { if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && (grantResults[0]) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED)
                    ) {
                        startActivity(Intent(this@LaunchActivity, InitActivity::class.java),
                            ActivityOptions.makeSceneTransitionAnimation(this@LaunchActivity).toBundle())
                        finish()
                    }
                } else if (grantResults.isNotEmpty() && (grantResults[1]) ==
                    PackageManager.PERMISSION_GRANTED){
                    startActivity(Intent(this@LaunchActivity, InitActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this@LaunchActivity).toBundle())
                    finish()
                }
                else{
                    finish()
                }
                return
            }
        }
    }



    override fun onBackPressed()
    {
        moveTaskToBack(true)
    }

}