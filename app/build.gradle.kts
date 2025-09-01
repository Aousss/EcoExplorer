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
        mlModelBinding = true   
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    aaptOptions{
        noCompress += "tflite"
    }
}

dependencies {

    // ADDED ON 26/8
    implementation (libs.tensorflow.lite.select.tf.ops)

    /* FIREBASE FUNCTIONS */
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Add the dependency for the Firebase ML model downloader library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.ml.modeldownloader)

    implementation(libs.google.firebase.analytics)

    // Firebase Services
    implementation(libs.com.google.firebase.firebase.analytics)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.database)
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.storage)

    /* FIREBASE */
    implementation(libs.firebase.ui.auth)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Glide for image loading
    implementation(libs.glide)
    implementation(libs.vision.common)
    implementation(libs.image.labeling.common)
    implementation(libs.core)
    annotationProcessor(libs.compiler) // Optional, for custom Glide modules

    // Google Play Services Base
    implementation(libs.play.services.base)

    // Logging and utilities
    implementation(libs.core.ktx) // For logging and extensions

    /* FOR CAMERA FUNCTIONS */
    // Required CameraX core library
    implementation (libs.camera.core)

    // CameraX Camera2 implementation (most common backend)
    implementation (libs.camera.camera2)

    // Optional: For lifecycle binding
    implementation (libs.camera.lifecycle)

    // Optional: For PreviewView and image capture
    implementation (libs.camera.view)
    implementation (libs.camera.extensions)

    implementation (libs.recyclerview) // or latest version
    implementation (libs.material.v150)

    /*-------------------
    * TENSORFLOW LITE
    * ------------------*/
//    implementation (libs.tensorflow.lite)
    implementation (libs.tensorflow.lite.task.vision)

    implementation (libs.tensorflow.lite)
//    implementation (libs.tensorflow.lite.support)
//    implementation (libs.tensorflow.lite.metadata)

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

    // AR Sceneview for augmented reality capabilities
    implementation ("io.github.sceneview:arsceneview:0.10.0")
}