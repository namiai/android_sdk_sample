plugins {
    id("com.google.protobuf") version "0.9.3"
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ai.nami.sdk.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "ai.nami.sdk.sample"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("namisdkkeystore")
            storePassword = "namisdk@2024"
            keyPassword = "namisdk@2024"
            keyAlias = "namialias"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            buildConfigField("boolean", "ENABLE_R8_LOGS", "true")
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
        compose = true
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    packaging {
        resources.excludes.add("META-INF/**/*")
    }
}

dependencies {


//    implementation(project(":demo-sdk-ui"))
//    implementation(project(":demo-sdk-routing"))
//    implementation(project(":demo-core-sdk"))
//    implementation(project(":demo-common"))
    implementation(project(":demo-sdk-ui-extension"))
//    implementation(project(":demo-sdk-fragment"))

    implementation(libs.android.core.ktx)
    implementation(libs.android.lifecycle.runtime)
    implementation(libs.google.android.material)

    implementation(platform(libs.android.compose.bom))
    implementation(libs.bundles.androidComposeLibs)


//    implementation("androidx.appcompat:appcompat:1.4.1")
    //   implementation("com.google.android.material:material:1.5.0")
//    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")

//    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")


    implementation(libs.androidx.datastore)


    // dependencies for Nami SDK
//    implementation(libs.nami.sdk.ui)

    // if you do not publish your app to Amazon store, do not need to add this
    implementation(files("libs/A3LLocation-1.0.0.aar"))

    // libraries for camera preview scan qr code
    // if you custom NamiQRScanView, you do not need to add these libraries
    implementation(libs.bundles.androidCameraLibs)

    // google vision for scan qrcode
    // if you custom NamiQRScanView, you do not need to add this library
    implementation(libs.google.barcode.scanning)

    val lottie = "6.6.7"
    implementation("com.airbnb.android:lottie:$lottie")
    implementation("com.yandex.div:div-lottie:32.16.0")


}