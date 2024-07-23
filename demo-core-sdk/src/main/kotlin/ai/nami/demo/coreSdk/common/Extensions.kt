package ai.nami.demo.coreSdk.common

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry

fun NavBackStackEntry.lifecycleIsResumed(): Boolean {
    return this.getLifecycle().currentState == Lifecycle.State.RESUMED
}