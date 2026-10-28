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

val isInternalSample = providers.gradleProperty("is_internal_sample").orNull == "true"

android {
    namespace = "ai.nami.shared_sample"
    compileSdk = 37

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
    packaging.resources.excludes += setOf(
        "META-INF/legal/**",
        "META-INF/LICENSE*",
        "META-INF/NOTICE*",
    )
}

dependencies {
    // This sample explicitly uses Navigation 3. Replace sdk-ui-navigation3 with
    // sdk-ui-navigation2 for Navigation 2, or sdk-ui-navigation when supplying
    // an application-owned NamiNavigationProvider.
    if (isInternalSample) {
        api(platform(project(":sdk-ui-bom")))
        api(project(":sdk-ui-extensions"))
        api(project(":sdk-ui-navigation3"))
    } else {
        api(platform("ai.nami:sdk-ui-bom:2.10.0"))
        api("ai.nami:sdk-ui-extensions")
        api("ai.nami:sdk-ui-navigation3")
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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    api("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // The sample application's own host uses Navigation 3 independently of the SDK adapter.
    api("androidx.navigation3:navigation3-runtime:1.1.4")
    implementation("androidx.navigation3:navigation3-ui:1.1.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0")

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

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
}
