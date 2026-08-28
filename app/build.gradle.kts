plugins {
    id("com.android.application")
}

android {
    namespace = "pro.liliya.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "pro.liliya.app"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":interaction"))
}
