plugins {
    id("com.escala.android.application")
    id("com.escala.android.compose")
    id("com.escala.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.escala.ministerial"
    defaultConfig {
        applicationId = "com.escala.ministerial"
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://escala-ministerial-api.onrender.com/api/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://escala-ministerial-api.onrender.com/api/\"")
        }
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:network"))
    implementation(project(":core:data"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:ministros"))
    implementation(project(":feature:eventos"))
    implementation(project(":feature:escalas"))
    implementation(project(":feature:feedback"))
    implementation(project(":feature:auditoria"))

    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.bundles.lifecycle)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.android)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
