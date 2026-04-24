plugins {
    id("com.escala.android.feature")
}

android {
    namespace = "com.escala.ministerial.feature.eventos"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:network"))
    implementation(project(":core:data"))
    implementation(libs.bundles.lifecycle)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.hilt.navigation.compose)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    testImplementation(libs.bundles.testing.unit)
    testImplementation(libs.junit5.engine)
}
