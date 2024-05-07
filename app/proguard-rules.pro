# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.impl.StaticMDCBinder

# keeps the pairing code so it can work in release build
-keep class pairing.** { *; }
-keep class nami.coap_proxy.v1.** { *; }
-keep class nami.localmsgs.v1.** { *; }
-keep class nami.platform_core.v1.** { *; }
-keep class ai.nami.pairing.** { *; }
-keep class androidx.camera.** { *; }
-keep class android.bluetooth.** { *; }

-keep class org.json.** { *; }
-keepclassmembers class org.json.** { *; }