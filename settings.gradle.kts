pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LiliyaPro"

include(
    ":app",
    ":domain",
    ":core",
    ":runtime",
    ":database",
    ":model",
    ":governance",
    ":ui"
)
