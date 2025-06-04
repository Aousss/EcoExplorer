plugins {
    alias(libs.plugins.android.application) apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false

}

// Register a custom task to generate the signing report
tasks.register("signingReport") {
    doLast {
        println("SHA-1 fingerprint generation is not directly supported in Gradle Kotlin DSL.")
        println("Use the command line or Android Studio's signing report.")
    }
}

buildscript {
    dependencies {
        classpath ("com.google.gms:google-services:4.3.15")
    }
}
