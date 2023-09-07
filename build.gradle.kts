buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        val nav_version = "2.7.2"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}


plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}