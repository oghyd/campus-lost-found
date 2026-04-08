package com.uir.lostfound;

import android.app.Application;

import com.uir.lostfound.db.DataSeeder;
import com.uir.lostfound.db.RealmHelper;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("lostfound.realm")
                .schemaVersion(1)
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(config);

        DataSeeder.seedData(RealmHelper.getInstance());
    }
}