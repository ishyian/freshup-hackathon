package com.melitopolcherry.hackathon.features.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.core.presentation.BaseDataBindingActivity
import com.melitopolcherry.hackathon.custom_view.showInfoDialog
import com.melitopolcherry.hackathon.data.model.EventItem
import com.melitopolcherry.hackathon.data.model.Place
import com.melitopolcherry.hackathon.data.models.ConnectionModel
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.databinding.ActivityMainBinding
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.features.filters.SearchFragment
import com.melitopolcherry.hackathon.features.maps.home.presentation.MapHomeFragment
import com.melitopolcherry.hackathon.screens.account.SettingsFragment
import com.melitopolcherry.hackathon.screens.events.EventsFragment
import com.melitopolcherry.hackathon.screens.notifications.EventsNotificationsFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseDataBindingActivity<ActivityMainBinding>(),
    Observer<ConnectionModel> {

    @Inject
    lateinit var accountManager: IAccountManager

    override val layoutResId = R.layout.activity_main
    val viewModel: MainViewModel by viewModels()

    private var materialDialog: MaterialDialog? = null
    private var doubleBackToExitPressedOnce = false
    private var currentPage = 0

    val events: List<EventItem> by lazy {
        val arrayTutorialType = object : TypeToken<ArrayList<EventItem>>() {}.type
        Gson().fromJson(loadJSONEventsFromAsset(), arrayTutorialType)
    }

    val places: List<Place> by lazy {
        val arrayTutorialType = object : TypeToken<ArrayList<Place>>() {}.type
        Gson().fromJson(loadJSONPlacesFromAsset(), arrayTutorialType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDecorFitsSystemWindows(window, false)
        onNewIntent(intent)

        setupTabs()
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragment_container, MapHomeFragment.newInstance(), MapHomeFragment.TAG)
            }
        }
        Timber.d("\n\n||||| LV TOKEN🕰 \n ${viewModel.authToken?.value?.accessToken} \n\n 🕰|||||")
    }

    override fun onChanged(t: ConnectionModel?) {
        if (!t?.isConnected!!) {
            materialDialog = showInfoDialog("You are offline now.")
            materialDialog?.cancelable(false)
        } else {
            materialDialog?.dismiss()
        }
        Timber.d("🤟🏿 CONNECTION Main" + t.isConnected)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey(Enums.BundleCodes.NotificationMove.name)) {
                val msg = extras.getInt(Enums.BundleCodes.NotificationMove.name, 0)
                Timber.d("🍨 notIntent value : $msg")
                if (msg != 0) {
                    currentPage = msg
                }
            } else if (extras.containsKey(Enums.BundleCodes.PaymentMove.name)) {
                val msg = extras.getInt(Enums.BundleCodes.PaymentMove.name, 0)
                if (msg != 0) {
                    if (supportFragmentManager.findFragmentByTag(SearchFragment.TAG) != null) {
                        supportFragmentManager.commit {
                            supportFragmentManager.findFragmentByTag(SearchFragment.TAG)?.let {
                                remove(it)
                            }
                        }
                        supportFragmentManager.popBackStack()
                        return
                    }
                    currentPage = msg
                }
                Timber.d("🍨 payment value : $msg")
            }
        }
    }

    private fun setupTabs() {
        binding.navigationTabs
        binding.navigationTabs.setOnItemSelectedListener {
            onNavigationItemSelected(it)
            return@setOnItemSelectedListener true
        }

        if (viewModel.loginMethod != null && viewModel.loginMethod != Enums.LoginMethods.NONE && viewModel.authToken?.value?.accessToken != null) {
            viewModel.getUnreadNotificationsCount()
        }
    }

    private fun onNavigationItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.navigation_home -> {
                currentPage = 0
                if (supportFragmentManager.findFragmentByTag(MapHomeFragment.TAG) != null) {
                    supportFragmentManager.commit {
                        replace(
                            R.id.fragment_container,
                            supportFragmentManager.findFragmentByTag(MapHomeFragment.TAG)!!,
                            MapHomeFragment.TAG
                        )
                    }
                } else {
                    supportFragmentManager.commit {
                        replace(
                            R.id.fragment_container,
                            MapHomeFragment.newInstance(),
                            MapHomeFragment.TAG
                        )
                    }
                }
            }
            R.id.navigation_events -> {
                currentPage = 1
                supportFragmentManager.commit {
                    add(R.id.fragment_container, EventsFragment(), EventsFragment.TAG)
                }
            }
            R.id.navigation_notifications -> {
                currentPage = 2
                supportFragmentManager.commit {
                    add(
                        R.id.fragment_container,
                        EventsNotificationsFragment(),
                        EventsNotificationsFragment.TAG
                    )
                }
            }
            R.id.navigation_account -> {
                currentPage = 3
                supportFragmentManager.commit {
                    add(
                        R.id.fragment_container,
                        SettingsFragment(),
                        SettingsFragment.TAG
                    )
                }
            }
        }
    }

    private fun loadJSONEventsFromAsset(): String? {
        return try {
            val `is`: InputStream = assets?.open("events.json")!!
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    private fun loadJSONPlacesFromAsset(): String? {
        return try {
            val `is`: InputStream = assets?.open("places.json")!!
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }
}