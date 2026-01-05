
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
 implementation("ai.nami:sdk-ui-extensions:2.2.0.9-snapshots")
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
    baseUrl = "https://mobile-screens.nami.surf/divkit/v0.5.0/precompiled_layouts",
    countryCode = "us",
    measureSystem = NamiMeasureSystem.METRIC,
    clientID = "alarm_com_security",
    language = "en",
    appearance = NamiAppearance.Light,
)
```