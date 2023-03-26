package com.melitopolcherry.hackathon.screens.select_university

import android.content.Intent
import androidx.core.view.WindowCompat
import com.melitopolcherry.hackathon.core.presentation.BaseActivity
import com.melitopolcherry.hackathon.databinding.ActivitySelectUniversityBinding
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectUniversityActivity : BaseActivity<ActivitySelectUniversityBinding>() {

    @Inject
    lateinit var accountManager: IAccountManager

    override fun initBinding(): ActivitySelectUniversityBinding {
        return ActivitySelectUniversityBinding.inflate(layoutInflater)
    }

    override fun onCreate() = with(binding) {
        super.onCreate()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        btnStart.setOnClickListener {
            startActivity(Intent(this@SelectUniversityActivity, MainActivity::class.java))
        }

        btnStart2.setOnClickListener {
            startActivity(Intent(this@SelectUniversityActivity, MainActivity::class.java))
        }
    }

}