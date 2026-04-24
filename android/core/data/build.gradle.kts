plugins {
    id("com.escala.android.library")
    id("com.escala.android.hilt")
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.escala.ministerial.core.data"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":core:network"))
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(libs.datastore.preferences)
    implementation(libs.security.crypto)
    implementation(libs.bundles.coroutines)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}
