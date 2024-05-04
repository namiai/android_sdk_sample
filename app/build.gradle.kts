plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.protobuf") version "0.9.3"
}

android {
    namespace = "ai.nami.sdk.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "ai.nami.sdk.sample"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources.excludes.add("META-INF/**/*")
    }
}

dependencies {
//    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar","*.aar"))))

    implementation(
        files(
            "libs/nami-pairing-sdk-1.5.4.aar",
            "libs/A3LLocation-1.0.0.aar",
            "libs/nami-widar-sdk-1.0.8.1.aar"
        )
    )

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")

    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation("androidx.navigation:navigation-common-ktx:2.5.3")
    implementation("androidx.navigation:navigation-compose:2.5.3")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // libraries for pairing
    implementation("com.google.protobuf:protobuf-javalite:3.14.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.5")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation("com.google.accompanist:accompanist-permissions:0.30.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
    // libraries for camera preview scan qr code
    implementation("androidx.camera:camera-view:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("androidx.camera:camera-camera2:1.2.3")

    // google vision for scan qrcode
    implementation("com.google.mlkit:barcode-scanning:17.1.0")

    implementation("com.google.android.gms:play-services-threadnetwork:16.0.0")

    // libraries for positioning (widar)
    val coroutinesVersion = "1.7.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    val coapVersion = "3.7.0"
    implementation("org.eclipse.californium:californium-core:$coapVersion")
    implementation("org.eclipse.californium:scandium:$coapVersion")
    implementation("com.google.protobuf:protobuf-javalite:3.14.0")
    implementation("com.airbnb.android:lottie-compose:6.2.0")

}