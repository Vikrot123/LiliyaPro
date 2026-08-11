plugins {
    kotlin("jvm")
}

group = "pro.liliya.core"
version = "1.0"

dependencies {
    testImplementation(
        "org.junit.jupiter:junit-jupiter:5.10.2"
    )

    testRuntimeOnly(
        "org.junit.platform:junit-platform-launcher:1.10.2"
    )
}

tasks.test {
    useJUnitPlatform()
}
