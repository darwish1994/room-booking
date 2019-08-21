package com.dac.roombooking.base

import android.app.Application
import com.dac.roombooking.BuildConfig
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Realm.init(this)
        val config = RealmConfiguration.Builder().name("myrealm.realm").build()
        Realm.setDefaultConfiguration(config)
    }

}