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

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

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
