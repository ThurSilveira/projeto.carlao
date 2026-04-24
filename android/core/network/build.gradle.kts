plugins {
    id("com.escala.android.library")
    id("com.escala.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.escala.ministerial.core.network"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(libs.bundles.network)
    api(libs.bundles.coroutines)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}
