
# Setup
## Add dependency 
```kotlin
 implementation("ai.nami:sdk-ui-extensions:1.0.0-snapshots")
```

## Add `sdkUiExtensionsGraph` to their `NavHost`
```kotlin
 NavHost(navController, startDestination = startDestination) {

    sdkUiExtensionsGraph(navController = navController, onExit = {
        navController.popBackStack( )
    }, onFinish = { })
     
 }

```

# Presenting Template
## Initialize SDK
```kotlin
SDK.init(sessionCode)
```
Note: this function is a suspend function

## Present the template
```kotlin
val route = NamiSdkUiExtensions.presentTemplate(
                    navController.context,
                    NamiSdkUiExtensionsEntryPoint().startSetupAKitUrl
                )
navController.navigate(route)
```
