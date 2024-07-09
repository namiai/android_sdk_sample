plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.protobuf") version "0.9.3"
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "ai.nami.sdk.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "ai.nami.sdk.sample"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.5.7-1.0.8.7"

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
        viewBinding = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources.excludes.add("META-INF/**/*")
    }
}

dependencies {
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
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.0")
    implementation( "androidx.fragment:fragment-ktx:1.4.1")

    implementation("androidx.navigation:navigation-common-ktx:2.5.3")
    implementation("androidx.navigation:navigation-compose:2.5.3")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")


    // dependencies for Nami SDK
    implementation("ai.nami:sdk-ui:2.1.1")

    // if you do not publish your app to Amazon store, do not need to add this
    implementation(files("libs/A3LLocation-1.0.0.aar"))

    // zxing
    implementation("com.google.zxing:core:3.5.0")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")

    implementation("com.google.android.gms:play-services-threadnetwork:16.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")


}