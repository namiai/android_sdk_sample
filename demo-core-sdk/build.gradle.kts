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

    configureKotlin()
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}

fun Project.configureKotlin() {
    // Use withType to workaround https://youtrack.jetbrains.com/issue/KT-55947
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_17.toString()
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
            )
        }
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