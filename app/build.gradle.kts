// ── app/build.gradle.kts ─────────────────────────────────────────────────────
// App-level build script (Kotlin DSL — no Groovy syntax).
// Applies the Android Application plugin and the Realm plugin.
// All dependency versions should eventually be centralised in gradle/libs.versions.toml.
// Ownership: Omar.
plugins {
    id("com.android.application")
    id("realm-android")  // Legacy Realm Java SDK plugin (io.realm)
}

android {
    namespace = "com.uir.lostfound"
    compileSdk = 35       // Always build against the latest API level

    defaultConfig {
        applicationId = "com.uir.lostfound"
        minSdk = 24       // Android 7.0 — required for FileProvider URI behaviour
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false  // Enable ProGuard here before production release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        // Java 11 required for lambdas and modern switch expressions used throughout the app
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")              // AppCompatActivity, Toolbar
    implementation("com.google.android.material:material:1.10.0")     // MaterialButton, MaterialCardView, FAB
    implementation("androidx.recyclerview:recyclerview:1.3.2")        // RecyclerView in feed + my posts
}

realm {
    isSyncEnabled = false   // Local-only Realm (no Atlas Device Sync)
}
