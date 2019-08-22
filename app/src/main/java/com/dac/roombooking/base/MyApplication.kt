package com.dac.roombooking.base

import android.app.Application
import com.dac.roombooking.BuildConfig
import com.dac.roombooking.utaltis.Constant
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
        val config = RealmConfiguration.Builder()
            .schemaVersion(0)
            .deleteRealmIfMigrationNeeded()
            .name(Constant.DATABASE_NAME)
            .build()
        Realm.setDefaultConfiguration(config)
    }

}