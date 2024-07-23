package ai.nami.demo.coreSdk

import ai.nami.demo.coreSdk.common.AppState
import ai.nami.demo.coreSdk.common.rememberAppState
import ai.nami.demo.coreSdk.pairing.fetchPairingInfo.SkyNetFetchPairingInfoNavigation
import ai.nami.demo.coreSdk.pairing.fetchPairingInfo.SkyNetFetchPairingInfoRoute
import ai.nami.demo.coreSdk.pairing.qrCode.SkyNetQRCodeNavigation
import ai.nami.demo.coreSdk.pairing.qrCode.SkyNetQRCodeRoute
import ai.nami.demo.coreSdk.shared.SkyNetInfoNavigation
import ai.nami.demo.coreSdk.shared.SkyNetInfoRoute
import ai.nami.sdk.pairing.NamiPairingSdk
import ai.nami.sdk.pairing.viewmodels.di.NamiPairingViewModelModule
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fatherofapps.jnav.JNavigation

class DemoCoreSDKActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NamiPairingViewModelModule.init(this)
        setContent {
            // you should replace MaterialTheme by your Theme
            MaterialTheme {
                SkyNetApp()
            }
        }
    }
}

@Composable
fun SkyNetApp(
    appState: AppState = rememberAppState()
) {
    SkyNetHostScreen(
        navHostController = appState.navController,
        onNavigateTo = appState::navigate,
        onBack = appState::back
    )
}

@Composable
fun SkyNetHostScreen(
    navHostController: NavHostController,
    onNavigateTo: (navigation: JNavigation, route: String?) -> Unit,
    onBack: (navigation: JNavigation?, inclusive: Boolean) -> Unit
) {

    val onExitPairing: () -> Unit = {
        NamiPairingSdk.clear()
        onBack(SkyNetInfoNavigation, true)
    }
    NavHost(navController = navHostController, startDestination = SkyNetInfoNavigation.route) {

        composable(route = SkyNetInfoNavigation.route) {
            SkyNetInfoRoute(onNext = { sessionCode, roomId ->
                val route = SkyNetFetchPairingInfoNavigation.createRoute(sessionCode, roomId)
                onNavigateTo(SkyNetFetchPairingInfoNavigation, route)
            }, onBack = {
                onBack(null, false)
            })
        }

        composable(
            route = SkyNetFetchPairingInfoNavigation.route,
            arguments = SkyNetFetchPairingInfoNavigation.arguments()
        ) {
            val sessionCode = SkyNetFetchPairingInfoNavigation.sessionCode(it)
            val roomId = SkyNetFetchPairingInfoNavigation.roomId(it)
            val viewModel = NamiPairingViewModelModule.provideFetchPairingPlaceViewModel()
            SkyNetFetchPairingInfoRoute(
                sessionCode = sessionCode,
                roomId = roomId,
                viewModel = viewModel,
                onNext = {
                    onNavigateTo(SkyNetQRCodeNavigation, SkyNetQRCodeNavigation.createRoute())
                }, onBack = {
                    onBack(null, false)
                })
        }

        composable(route = SkyNetQRCodeNavigation.route) {
            val viewModel = NamiPairingViewModelModule.provideScanQRCodeViewModel()
            SkyNetQRCodeRoute(viewModel = viewModel, onNext = { /*TODO*/ }, onBack = {
                onExitPairing()
            })
        }

    }
}