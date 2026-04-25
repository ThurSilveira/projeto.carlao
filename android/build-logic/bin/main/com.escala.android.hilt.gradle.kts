plugins {
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

dependencies {
    "implementation"("com.google.dagger:hilt-android:2.51.1")
    "ksp"("com.google.dagger:hilt-android-compiler:2.51.1")
}
