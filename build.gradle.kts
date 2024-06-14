plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.devtools.ksp") version "1.8.0-1.0.8" apply false

}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0")
    }
//    dependencies{
//        val navVersion = "2.7.7"
//        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
//    }
}