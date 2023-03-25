package com.melitopolcherry.hackathon.features.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.databinding.ActivityOnBoardingBinding
import com.melitopolcherry.hackathon.screens.select_university.SelectUniversityActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity(R.layout.activity_on_boarding) {

    private val viewModel: OnBoardingViewModel by viewModels()
    lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        binding.lifecycleOwner = this
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, SelectUniversityActivity::class.java))
            finish()
        }
    }

}