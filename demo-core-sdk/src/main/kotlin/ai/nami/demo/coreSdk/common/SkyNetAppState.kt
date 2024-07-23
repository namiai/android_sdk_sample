package ai.nami.demo.coreSdk.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fatherofapps.jnav.JNavigation





@Stable
class AppState(val navController: NavHostController) {
    fun navigate(navigation: JNavigation, route: String?) {
        if (navigation.isTopDestination) {
            navController.navigate(route ?: navigation.route) {
                launchSingleTop = true
                restoreState = true
            }

        } else {
            navController.navigate(route ?: navigation.route)
        }
    }

    fun back(navigation: JNavigation? = null, inclusive: Boolean = false) {
        navigation?.let {
            navController.popBackStack(it.route, inclusive)
        } ?: run { navController.popBackStack() }
    }

    fun back(route: String? = null, inclusive: Boolean = false) {
        route?.let {
            navController.popBackStack(route, inclusive)
        } ?: run { navController.popBackStack() }
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        AppState(navController)
    }
