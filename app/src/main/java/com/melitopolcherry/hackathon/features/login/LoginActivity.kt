package com.melitopolcherry.hackathon.features.login

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.custom_view.setDarkSystemsBars
import com.melitopolcherry.hackathon.custom_view.setLightSystemsBars
import com.melitopolcherry.hackathon.features.login.main.LoginHomeFragment
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    var loginType: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("taggg", "LoginonCreate")

        setBackStackListener()

        getFCMToken()

        supportFragmentManager.addOnBackStackChangedListener {
            println("🤢 addOnBackStackChangedListener ${supportFragmentManager.backStackEntryCount} ")
        }

        supportFragmentManager.commit {
            replace(R.id.content_login, LoginHomeFragment.newInstance(), LoginHomeFragment.TAG)
            addToBackStack(LoginHomeFragment.TAG)
        }
    }

    private fun setBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount > 1) {
                setSystemBarsLight(true)
            } else {
                setSystemBarsLight(false)
            }
        }
    }

    private fun setSystemBarsLight(makeLight: Boolean) {
        if (makeLight) {
            val color = ContextCompat.getColor(this, R.color.background_primary)
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                window.setDarkSystemsBars(color)
            } else {
                window.setLightSystemsBars(color)
            }
        } else {
            val color = ContextCompat.getColor(this, R.color.black_back)
            window.setDarkSystemsBars(color)
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println("🤢 FCM Token: $fcmToken")
            fcmToken = it
        }
    }

    fun replaceFragments(fragment: Fragment, tag: String) {
        supportFragmentManager.commit {
            replace(R.id.content_login, fragment, tag)
            addToBackStack(tag)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    companion object {
        var fcmToken = ""
    }
}