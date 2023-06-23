@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.anival01"
    compileSdk = 33

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.anival01"
        minSdk = 19
        targetSdk = 33
        multiDexEnabled = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.firebase.auth.ktx)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    // When using the BoM, don't specify versions in Firebase dependencies
    // implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.firebase.analytics.ktx)
    // Also add the dependency for the Google Play services library and specify its version
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore.ktx)

    // coroutines
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Material Design
    implementation(libs.material)

    //Alert Dialog
    implementation(libs.appcompat)

    //Timber logging
    implementation(libs.timber)
    //navigation libraries
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
// for old android
    implementation(libs.androidx.multidex)
    //facebook login
    implementation (libs.facebook.login)
    implementation (libs.facebook.android.sdk)




}