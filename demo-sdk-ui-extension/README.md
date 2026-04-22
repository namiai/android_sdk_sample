# Nami Pairing SDK Demo (Android)

This project is a lightweight Android host application demonstrating the simplest use case of the Nami Pairing SDK UI Extensions.

Internally, this module is used by the Nami team to test local Android SDK changes on an emulator or device. Externally, it serves as a reference implementation for consumer apps integrating the Nami SDK via Jetpack Compose.

## Running the Demo Locally

1. Open the `targets/SdkDemo/android` directory in Android Studio.
2. Select the `demo-sdk-ui-extension` run configuration.
3. Click **Run** to build and deploy the demo app to your device.

*Note:* The demo app depends on the local SDK modules or snapshot builds depending on your configuration. 

---

## Integration Guide: Using the SDK in Consumer Code

The following steps illustrate how a third-party application integrates the Nami Pairing SDK using Jetpack Compose, as demonstrated in this project.

### 1. Add the Nami SDK Repository

Add the Nami Maven repository URLs to your project's `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "nami-maven-repository-releases"
            setUrl("https://raw.githubusercontent.com/namiai/nami-maven-repository/main/repository/releases")
        }
        maven {
            name = "nami-maven-repository-snapshots"
            setUrl("https://raw.githubusercontent.com/namiai/nami-maven-repository/main/repository/snapshots")
        }
    }
}
```

### 2. Add the Dependency

Add the SDK UI Extensions dependency to your app-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("ai.nami:sdk-ui-extensions:2.3.1.4-snapshots") // Use the latest release or snapshot
}
```

### 3. Initialize the SDK

The SDK requires a `sessionCode` obtained from your backend. You must initialize the SDK before attempting to present any of its UI components.

```kotlin
// Initialize the SDK, usually in your Application class or early in your launch flow
SDK.init(sessionCode)
```

### 4. Setup Jetpack Compose Navigation

The Nami SDK exposes its screens via a Compose Navigation graph. Add the `sdkUiExtensionsGraph` to your application's `NavHost`:

```kotlin
NavHost(navController = navController, startDestination = startDestination) {
    
    // Include the SDK's navigation graph
    sdkUiExtensionsGraph(
        navController = navController, 
        onExit = {
            // Handle when the user exits the SDK flow
        }, 
        onFinish = { 
            // Handle successful completion of the SDK flow
        }
    )
    
    // ... your other app destinations
}
```

### 5. Configure and Present the Template

To launch the SDK flow, you must construct an `SdkConfig` object that dictates the environment, styling, and language for the Server-Driven UI (SDUI).

```kotlin
// Define the SDK configuration
val sdkConfig = SdkConfig(
    baseUrl = "https://mobile-screens.nami.surf/divkit/v0.11.0/precompiled_layouts",
    measureSystem = NamiMeasureSystem.METRIC,
    clientID = "alarm_com_security",
    language = "en",
    appearance = NamiAppearance.Light,
)

// Retrieve the starting route for the entry point
val route = NamiSdkUiExtensions.presentTemplate(
    context = navController.context,
    templateUrl = NamiSdkUiExtensionsEntryPoint().startSetupAKitUrl, // Choose your entry point
    sdkConfig = sdkConfig
)

// Navigate to the route
navController.navigate(route)
```

#### Common Entry Points
* `startSetupAKitUrl`: Guided flow for setting up a complete kit of devices.
* `settingsUrl`: Device/system settings screen.
* `singleDeviceSetupUrl`: Flow for setting up a single device.