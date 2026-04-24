pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            when {
                requested.id.id.startsWith("com.android") -> useVersion("8.4.2")
                requested.id.id.startsWith("org.jetbrains.kotlin") -> useVersion("2.0.0")
                requested.id.id == "com.google.dagger.hilt.android" -> useVersion("2.51.1")
                requested.id.id == "com.google.devtools.ksp" -> useVersion("2.0.0-1.0.21")
                requested.id.id == "androidx.room" -> useVersion("2.6.1")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
