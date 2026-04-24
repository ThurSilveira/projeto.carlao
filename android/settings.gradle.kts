pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            when {
                requested.id.id.startsWith("com.android") ->
                    useVersion("8.4.2")
                requested.id.id.startsWith("org.jetbrains.kotlin") ->
                    useVersion("2.0.0")
                requested.id.id == "com.google.dagger.hilt.android" ->
                    useVersion("2.51.1")
                requested.id.id == "com.google.devtools.ksp" ->
                    useVersion("2.0.0-1.0.21")
                requested.id.id == "androidx.room" ->
                    useVersion("2.6.1")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EscalaMinisterial"

include(":app")
include(":core:ui")
include(":core:network")
include(":core:data")
include(":feature:dashboard")
include(":feature:ministros")
include(":feature:eventos")
include(":feature:escalas")
include(":feature:feedback")
include(":feature:auditoria")
