#!/usr/bin/env bash
set -euo pipefail

# ---------- config ----------
GH_USER="rwurdig"
REPO="DH_test"
BRANCH="main"            # change to 'develop' if you prefer Git Flow
# ----------------------------

gh repo view "$GH_USER/$REPO" >/dev/null 2>&1 || gh repo create "$GH_USER/$REPO" --public --confirm
git clone "https://github.com/$GH_USER/$REPO.git"
cd "$REPO"

# initialise only if empty
if [ ! -f settings.gradle.kts ]; then
  ################## gradle settings ##################
  cat > settings.gradle.kts <<'EOF'
rootProject.name = "DH_test"
include(":app")
EOF

  ################## root build.gradle.kts ##################
  cat > build.gradle.kts <<'EOF'
plugins {
    id("com.android.application") version "8.4.1" apply false
    kotlin("android") version "1.9.24" apply false
}
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
EOF

  ################## app module ##################
  mkdir -p app/src/main/kotlin/com/rwurdig/hdbodygraph/ui
  mkdir -p app/src/main/kotlin/com/rwurdig/hdbodygraph/domain
  mkdir -p app/src/main/kotlin/com/rwurdig/hdbodygraph/data
  mkdir -p app/src/main/res/drawable

  cat > app/build.gradle.kts <<'EOF'
plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.rwurdig.hdbodygraph"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rwurdig.hdbodygraph"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.11" }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation("com.github.aloiscochard:swisseph:2.10.2")
    implementation("com.github.johnpryan:timezone-mapper:1.0.0")

    testImplementation("junit:junit:4.13.2")
}
EOF

  ################## AndroidManifest ##################
  cat > app/src/main/AndroidManifest.xml <<'EOF'
<manifest package="com.rwurdig.hdbodygraph"
          xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:allowBackup="true"
        android:label="HD BodyGraph"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar">
        <activity android:name=".ui.MainActivity"
                  android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>
</manifest>
EOF

  ################## MainActivity ##################
  cat > app/src/main/kotlin/com/rwurdig/hdbodygraph/ui/MainActivity.kt <<'EOF'
package com.rwurdig.hdbodygraph.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }
}

@Composable
fun MainScreen() {
    Scaffold(topBar = { SmallTopAppBar(title = { Text("HD BodyGraph") }) }) { _ ->
        Text("Hello Human‑Design!")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() { MainScreen() }
EOF

  ################## EphemerisService ##################
  cat > app/src/main/kotlin/com/rwurdig/hdbodygraph/domain/EphemerisService.kt <<'EOF'
package com.rwurdig.hdbodygraph.domain

import swisseph.SweDate
import swisseph.SwissEph
import java.time.ZonedDateTime

class EphemerisService(private val swe: SwissEph = SwissEph()) {
    data class PlanetPos(val longitude: Double)

    fun positions(utc: ZonedDateTime): Map<Int, PlanetPos> {
        val jdUt = SweDate(
            utc.year, utc.monthValue, utc.dayOfMonth,
            utc.hour + utc.minute / 60.0
        ).julDay

        return listOf(
            SwissEph.SE_SUN, SwissEph.SE_EARTH, SwissEph.SE_MOON,
            SwissEph.SE_MERCURY, SwissEph.SE_VENUS, SwissEph.SE_MARS,
            SwissEph.SE_JUPITER, SwissEph.SE_SATURN, SwissEph.SE_URANUS,
            SwissEph.SE_NEPTUNE, SwissEph.SE_PLUTO
        ).associateWith { idx ->
            val xx = DoubleArray(6)
            swe.swe_calc_ut(jdUt, idx, SwissEph.SEFLG_SWIEPH, xx, DoubleArray(6))
            PlanetPos(xx[0])
        }
    }
}
EOF

  ################## GitHub Action ##################
  mkdir -p .github/workflows
  cat > .github/workflows/android.yml <<'EOF'
name: Android CI
on:
  push: { branches: [ main ] }
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: gradle/gradle-build-action@v3
        with:
          arguments: assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/*.apk
EOF
fi  # end of repo initialisation

git add .
git commit -m "Initial Android Compose + SwissEph skeleton"
git branch -M "$BRANCH"
git push -u origin "$BRANCH"
echo "✅ All done – check https://github.com/$GH_USER/$REPO/actions for the APK artifact."
