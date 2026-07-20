plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "pro.liliya.app"

    compileSdk = 35

    defaultConfig {
        applicationId = "pro.liliya.app"

        minSdk = 29
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters += "arm64-v8a"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }

        debug {
            isDebuggable = true
        }
    }
}

dependencies {

    implementation(project(":runtime"))
    implementation(project(":ui"))

}
