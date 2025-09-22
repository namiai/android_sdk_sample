plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ai.nami.demo.core.sdk"
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


    buildFeatures {
        compose = true
    }
    kotlin {
        jvmToolchain(17)
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

    implementation(libs.nami.core.sdk)

    implementation(libs.jnav)
    ksp(libs.jnav)

    implementation(libs.jetpack.compose.permission)

    //  check location service for Android 11 and below
    implementation(libs.play.services.location)
}