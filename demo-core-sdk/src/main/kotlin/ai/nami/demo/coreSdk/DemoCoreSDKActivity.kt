package ai.nami.demo.coreSdk

import ai.nami.demo.coreSdk.pairing.fetchPairingInfo.SkyNetFetchPairingInfoNavigation
import ai.nami.sdk.pairing.viewmodels.di.NamiPairingViewModelModule
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class DemoCoreSDKActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NamiPairingViewModelModule.init(this)

    }
}


@Composable
fun SkyNetHostScreen(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = "") {
        composable(
            route = SkyNetFetchPairingInfoNavigation.route,
            arguments = SkyNetFetchPairingInfoNavigation.arguments()
        ) {
            val sessionCode = SkyNetFetchPairingInfoNavigation.sessionCode(it)
            val roomId = SkyNetFetchPairingInfoNavigation.roomId(it)
            val viewModel = NamiPairingViewModelModule.provideFetchPairingPlaceViewModel()

        }
    }
}