package com.melitopolcherry.hackathon

import android.app.Application
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(this, BuildConfig.STRIPE_KEY)
        Realm.init(this)
        val config2 = RealmConfiguration.Builder().name("default2").schemaVersion(3)
            .deleteRealmIfMigrationNeeded().allowWritesOnUiThread(true).build()
        Realm.setDefaultConfiguration(config2)
        Realm.deleteRealm(RealmConfiguration.Builder().build())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}