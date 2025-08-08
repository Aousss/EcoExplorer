plugins {

    id("com.android.application")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ecoexplorer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ecoexplorer"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    /* FIREBASE FUNCTIONS */
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))

    // Add the dependency for the Firebase ML model downloader library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-ml-modeldownloader")

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    // Firebase Services
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    /* FIREBASE */
    implementation("com.firebaseui:firebase-ui-auth:9.0.0")

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.vision.common)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1") // Optional, for custom Glide modules

    // Google Play Services Base
    implementation("com.google.android.gms:play-services-base:18.2.0")

    // Logging and utilities
    implementation("androidx.core:core-ktx:1.10.1") // For logging and extensions


    /* FOR CAMERA FUNCTIONS */
    // Required CameraX core library
    implementation ("androidx.camera:camera-core:1.3.0")

    // CameraX Camera2 implementation (most common backend)
    implementation ("androidx.camera:camera-camera2:1.3.0")

    // Optional: For lifecycle binding
    implementation ("androidx.camera:camera-lifecycle:1.3.0")

    // Optional: For PreviewView and image capture
    implementation ("androidx.camera:camera-view:1.3.0")
    implementation ("androidx.camera:camera-extensions:1.3.0")

    implementation ("androidx.recyclerview:recyclerview:1.3.2") // or latest version
    implementation ("com.google.android.material:material:1.5.0")

    /*-------------------
    * TENSORFLOW LITE
    * ------------------*/
    implementation ("org.tensorflow:tensorflow-lite:2.13.0")
    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.4.3")


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.camera.view)
    implementation(libs.camera.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}