plugins {
    id("com.escala.android.library")
    id("com.escala.android.compose")
}

android {
    namespace = "com.escala.ministerial.core.ui"
}

dependencies {
    api(libs.core.ktx)
    implementation(platform(libs.compose.bom))
    api(libs.bundles.compose)
    api(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}
