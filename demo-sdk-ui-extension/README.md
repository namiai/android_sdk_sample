
# Setup
## Add dependency 
```kotlin
 implementation("ai.nami:sdk-ui-extensions:1.0.8-snapshots")
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
    baseUrl = "https://mobile-screens.nami.surf/divkit/v0.2.0/precompiled_layouts",
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

