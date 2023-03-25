package com.melitopolcherry.hackathon.features.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.features.onboarding.OnBoardingActivity
import com.melitopolcherry.hackathon.screens.select_university.SelectUniversityActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            installSplashScreen()
        }
        super.onCreate(savedInstanceState)
        viewModel.nextScreen.observe(this) {
            when (it) {
                1 -> {
                    val intent = Intent(
                        this@SplashScreenActivity,
                        OnBoardingActivity::class.java
                    )
                    startActivity(intent)
                    finish()
                }
                2 -> {
                    val intent =
                        Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                3 -> {
                    val intent =
                        Intent(this@SplashScreenActivity, SelectUniversityActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    val intent = Intent(
                        this@SplashScreenActivity,
                        OnBoardingActivity::class.java
                    )
                    startActivity(intent)
                    finish()
                }
            }
        }

        showDeviceInfo()
        val content: View = findViewById(android.R.id.content)
        Log.d("taggg", "=====⚡️ ${viewModel.nextScreen.value}")
        content.viewTreeObserver.addOnPreDrawListener {
            false
        }
    }

    private fun showDeviceInfo() {
        val type = when (resources.displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "LDPI"
            DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
            DisplayMetrics.DENSITY_HIGH -> "HDPI"
            DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
            DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
            DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
            else -> "HZ"
        }
        println("\n\n🕰 \n ${resources.displayMetrics.densityDpi} $type \n\n 🕰")
    }
}