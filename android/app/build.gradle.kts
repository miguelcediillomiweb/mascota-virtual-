plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    namespace = "com.mascotavirtual"
    compileSdk = 36
    defaultConfig { applicationId = "com.mascotavirtual"; minSdk = 23; targetSdk = 35; versionCode = 1; versionName = "1.0" }
}
