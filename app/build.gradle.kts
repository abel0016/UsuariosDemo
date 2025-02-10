plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.usuariosdemo"
    compileSdk = 35

    viewBinding{
        enable=true
    }
    defaultConfig {
        applicationId = "com.example.usuariosdemo"
        minSdk = 28
        targetSdk = 34
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Google Sign In
    implementation ("com.google.android.gms:play-services-auth:20.6.0")
    implementation ("com.google.firebase:firebase-auth:21.0.8")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.appcompat)
    implementation("androidx.room:room-runtime:2.5.2")
    implementation(libs.lifecycle.viewmodel.android)
    annotationProcessor("androidx.room:room-compiler:2.5.2")
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}