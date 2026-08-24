plugins {
    // AGP + Kotlin are applied version-less here: both host builds already put
    // them on the plugin classpath (via the app/other modules), and the two
    // version catalogs spell these aliases differently. kotlin-compose /
    // kotlin-serialization aliases exist (identically) in both catalogs.
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ai.nami.shared_sample"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
        // enable for demo host SDK by Fragment / xml based layout
        dataBinding = true
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "META-INF/**/*"
        }
    }
}

val isInternalSample = providers.gradleProperty("is_internal_sample").orNull == "true"

dependencies {

    // Nami SDK UI extensions: project dependency for the internal sample,
    // published artifact for the external sample.
    if (isInternalSample) {
        api(project(":sdk-ui-extensions"))
    } else {
        api("ai.nami:sdk-ui-extensions:2.8.0.2-snapshot")
    }


    api(libs.androidx.core.ktx)
    api(libs.androidx.activity.compose)

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui.tooling)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.ui)
    api(libs.androidx.material)
    implementation(libs.constraintlayout.compose)
    implementation(libs.navigation.common.compose)
    implementation(libs.navigation.compose)

    api("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // libraries for camera preview scan qr code
    // if you custom NamiQRScanView, you do not need to add these libraries
    implementation(libs.bundles.androidCameraLibs)

    // google vision for scan qrcode
    // if you custom NamiQRScanView, you do not need to add this library
    implementation(libs.google.barcode.scanning)

    // libraries for demo host SDK by Fragment or xml based layout
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.5")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")

    // Core Navigation 3 API. Includes NavEntry, EntryProvider and the associated DSL.
    api("androidx.navigation3:navigation3-runtime:1.1.4")
    // Provides classes to display content, including NavDisplay and Scene.
    implementation("androidx.navigation3:navigation3-ui:1.1.4")
    // Allows ViewModels to be scoped to entries in the back stack.
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.9.4")
}
