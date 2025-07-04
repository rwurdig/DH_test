$ErrorActionPreference = "Stop"
$GH_USER = "rwurdig"
$REPO    = "DH_test"
$BRANCH  = "main"

if (-not (gh repo view "$GH_USER/$REPO" 2>$null)) {
  gh repo create "$GH_USER/$REPO" --public --confirm
}

git clone "https://github.com/$GH_USER/$REPO.git"
Set-Location $REPO

if (-not (Test-Path "settings.gradle.kts")) {
  # -- write files with here‑strings --
  @"
rootProject.name = "DH_test"
include(":app")
"@ | Out-File -Encoding utf8 settings.gradle.kts

  @"
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
"@ | Out-File -Encoding utf8 build.gradle.kts

  mkdir app, ".github/workflows" -ea 0
  mkdir app\src\main\kotlin\com\rwurdig\hdbodygraph\ui -ea 0
  mkdir app\src\main\kotlin\com\rwurdig\hdbodygraph\domain -ea 0
  mkdir app\src\main\res\drawable -ea 0

  # (…same content as bash version – omitted for brevity …)
}

git add .
git commit -m "Initial Android Compose + SwissEph skeleton"
git switch -c $BRANCH
git push -u origin $BRANCH
Write-Host "✅ Repo bootstrapped. Check Actions tab for the debug APK."
