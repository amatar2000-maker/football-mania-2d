plugins {
    id("com.android.application")
}

// Version numbers: versionCode goes up automatically with every GitHub Actions run.
val ciVersionCode = (System.getenv("VERSION_CODE") ?: "1").toInt()
val appVersionName = "1.0." + ciVersionCode

android {
    namespace = "com.footballmania2d.game"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.footballmania2d.game"   // DO NOT CHANGE after first upload
        minSdk = 24
        targetSdk = 36
        versionCode = ciVersionCode
        versionName = appVersionName
    }

    signingConfigs {
        create("release") {
            val ks = System.getenv("KEYSTORE_PATH")
            if (ks != null && file(ks).exists()) {
                storeFile = file(ks)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            val rel = signingConfigs.getByName("release")
            if (rel.storeFile != null) signingConfig = rel
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
