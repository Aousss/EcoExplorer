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

            // To avoid META-INF conflicts
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE*"
            excludes += "META-INF/NOTICE*"
        }
    }

    aaptOptions{
        noCompress += "tflite"
    }
}

dependencies {

    /* ----------------
     * FIREBASE
     * ---------------- */
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ml.modeldownloader)
    implementation(libs.google.firebase.analytics)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.database)
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.storage)
    implementation(libs.firebase.ui.auth)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    // Google Play Services Base
    implementation(libs.play.services.base)

    // Logging + AndroidX core
    implementation(libs.core.ktx)

    /* ----------------
     * CAMERA X
     * ---------------- */
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.camera.extensions)

    // UI Components
    implementation(libs.recyclerview)
    implementation(libs.material.v150)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.task.vision)
    implementation(libs.tensorflow.lite.select.tf.ops)

    /* ----------------
    * TESTING
    * ---------------- */
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}