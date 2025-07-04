plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    compileSdkVersion 34
    namespace 'com.example.humandesign'

    defaultConfig {
        applicationId "com.example.humandesign"
        minSdkVersion 24
        targetSdkVersion 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    buildFeatures {
        compose true
    }

    composeOptions {
        kotlinCompilerExtensionVersion '1.5.11'
    }

}

dependencies {
    implementation "androidx.core:core-ktx:1.12.0"
    implementation "androidx.activity:activity-compose:1.9.0"
    implementation "androidx.compose.material3:material3:1.2.0"
 // ── Compose BOM (keeps all compose libs in sync) ──
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))   // ▲ ADD

    // Material3 (Compose) ------------------------------------------------
    implementation("androidx.compose.material3:material3")                // ▲ ADD
    implementation("androidx.compose.material3:material3-window-size-class") // optional

    // Google Places SDK --------------------------------------------------
    implementation("com.google.android.libraries.places:places:3.2.0")    // ▲ ADD

    // (keep whatever was already here, e.g. activity‑compose, SwissEph…)
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("com.github.aloiscochard:swisseph:2.10.2")
    implementation("com.github.johnpryan:timezone-mapper:1.0.0")

    testImplementation("junit:junit:4.13.2")

}

// Optional: used only to bias autocomplete to user’s GPS position
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

<application
    android:name=".MyApplication"   <!-- ▲ we’ll create this class next -->
    ... >
    <!-- Google Places API key (fallback to strings.xml) -->
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="@string/google_maps_key"/>
