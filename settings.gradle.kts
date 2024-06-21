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
    }
}
rootProject.name = "NamiSDKSample"
include(":app")