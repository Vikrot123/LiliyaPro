plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":model"))

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}
