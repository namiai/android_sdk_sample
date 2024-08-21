pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
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
        maven {
            name = "nami-maven-repository-snapshots"
            setUrl("https://raw.githubusercontent.com/namiai/nami-maven-repository/main/repository/snapshots")
        }
    }
}
rootProject.name = "NamiSDKSample"
include(":app")
include(":demo-sdk-ui")
include(":demo-sdk-routing")
include(":demo-core-sdk")
include(":demo-common")
