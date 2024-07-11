plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "ai.nami.demo.sdk.ui"
    compileSdk = 34

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

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}

dependencies {

    implementation(project(":demo-common"))

    implementation(libs.appcompat)
    implementation(libs.android.core.ktx)
    implementation(libs.android.lifecycle.runtime)
    implementation(libs.google.android.material)

    implementation(platform(libs.android.compose.bom))
    implementation(libs.bundles.androidComposeLibs)

    implementation(libs.androidx.datastore)


    // dependencies for Nami SDK
    implementation(libs.nami.sdk.ui)

    // libraries for camera preview scan qr code
    // if you custom NamiQRScanView, you do not need to add these libraries
    implementation(libs.bundles.androidCameraLibs)

    // google vision for scan qrcode
    // if you custom NamiQRScanView, you do not need to add this library
    implementation(libs.google.barcode.scanning)

}