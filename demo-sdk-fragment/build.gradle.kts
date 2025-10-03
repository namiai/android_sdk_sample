plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ai.nami.demo_sdk_fragment"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.android.core.ktx)
    implementation(libs.appcompat)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.google.android.material)
    implementation(libs.android.lifecycle.runtime)
    implementation(platform(libs.android.compose.bom))
    implementation(libs.bundles.androidComposeLibs)

    implementation(libs.androidx.datastore)

    implementation(libs.nami.sdk.ui.extensions)
//    implementation(libs.nami.sdk.ui)


    // libraries for camera preview scan qr code
    // if you custom NamiQRScanView, you do not need to add these libraries
    implementation(libs.bundles.androidCameraLibs)

    // google vision for scan qrcode
    // if you custom NamiQRScanView, you do not need to add this library
    implementation(libs.google.barcode.scanning)
    implementation(libs.android.navigation.compose)


    implementation(libs.androidx.fragment.ktx)
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.5")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")

}