package com.uir.lostfound;

import android.app.Application;

import com.uir.lostfound.db.DataSeeder;
import com.uir.lostfound.db.RealmHelper;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * MyApplication — custom Application class, runs once when the process starts.
 *
 * Responsibilities:
 * 1. Initialises Realm with the default configuration (schema v1, local DB file "lostfound.realm").
 * 2. Seeds demo data via {@link com.uir.lostfound.db.DataSeeder#seedData} so the feed
 *    is non-empty on first launch.
 *
 * Registered in AndroidManifest via {@code android:name=".MyApplication"}.
 */
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