package com.dac.roombooking.base;

import com.dac.roombooking.BuildConfig;
import com.dac.roombooking.di.component.DaggerAppComponent;
import com.dac.roombooking.utaltis.Constant;
import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

public class BaseApplication extends DaggerApplication {
    @Override
    public void onCreate() {
        super.onCreate();
/**
 * for log message
 * */
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
/**
 * Realm is the fastest data base it is faster than room data base
 * i had work with room for long time
 * in realm you can create relation very easy
 * one to many
 * many to many
 * i have done many to many query with room and for create it you should create class to combine all different object and write you dio interface an query
 * i thin realm is best chose for mobile database
 * */
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .name(Constant.INSTANCE.getDATABASE_NAME())
                .build();
        Realm.setDefaultConfiguration(config);


    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();

    }
}
