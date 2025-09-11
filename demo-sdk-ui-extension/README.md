
# Setup

## Add Nami SDK repository url to the settings.gradle.kts file of your project

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

## Add dependency 
```kotlin
 implementation("ai.nami:sdk-ui-extensions:1.2.0")
```

## Add `sdkUiExtensionsGraph` to their `NavHost`
```kotlin
 NavHost(navController, startDestination = startDestination) {

    sdkUiExtensionsGraph(navController = navController, onExit = {

    }, onFinish = { })
     
 }

```

# Presenting Template
## Initialize SDK
```kotlin
SDK.init(sessionCode)
```

## Present the template
```kotlin
val route = NamiSdkUiExtensions.presentTemplate(
                    navController.context,
    NamiSdkUiExtensionsEntryPoint().startSetupAKitUrl,
    sdkConfig = SdkConfig(baseUrl, clientID)
                )
navController.navigate(route)
```

For testing the snapshots version, please use the below configuration:

```kotlin
SdkConfig(
    baseUrl = "https://mobile-screens.nami.surf/divkit/v0.3.0/precompiled_layouts",
    countryCode = "us",
    measureSystem = NamiMeasureSystem.METRIC,
    clientID = "alarm_com_security",
    language = "en",
    appearance = NamiAppearance.Light,
)
```

# Release notes

## 1.0.8-snapshots

- Support settings and start a single device entry point
- Bug fixes
- Integrate with the new core-SDK that supports fetching Thread credentials from LocalComm
- Send DeviceCategory to the pairing flow
- Create a new zone (support creating a default room

## 1.0.0

- Dark mode
- Add Entry & Exit Delays and Sensitivity
- Bug fixes

## 1.1.0

- System test
- Bug fixes

## 1.2.0

- RSSI Check up
- Popup / AlertDialog
- Snackbar error
- PIN Management
- Support French
- Bug fixes
