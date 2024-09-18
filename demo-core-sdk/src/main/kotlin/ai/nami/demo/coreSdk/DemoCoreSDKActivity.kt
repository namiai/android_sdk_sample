package ai.nami.demo.coreSdk

import ai.nami.demo.coreSdk.common.AppState
import ai.nami.demo.coreSdk.common.lifecycleIsResumed
import ai.nami.demo.coreSdk.common.rememberAppState
import ai.nami.demo.coreSdk.pairing.pairingGraph
import ai.nami.demo.coreSdk.pairing.qrCode.SkyNetQRCodeNavigation
import ai.nami.demo.coreSdk.positioning.positioningGraph
import ai.nami.demo.coreSdk.positioning.recommendations.SkyNetWidarRecommendationNavigation
import ai.nami.demo.coreSdk.shared.SkyNetHomeRoute
import ai.nami.demo.coreSdk.shared.SkyNetHomeRouteNavigation
import ai.nami.demo.coreSdk.shared.SkyNetInfoNavigation
import ai.nami.demo.coreSdk.shared.SkyNetInfoRoute
import ai.nami.demo.coreSdk.shared.SkyNetInfoViewModel
import ai.nami.sdk.NamiSDK
import ai.nami.sdk.model.NamiSavedThreadNetworkInfo
import ai.nami.sdk.pairing.NamiPairingSdk
import ai.nami.sdk.pairing.viewmodels.di.NamiPairingViewModelModule
import ai.nami.sdk.positioning.viewmodels.di.NamiPositioningViewModelModule
import ai.nami.sdk.registerNamiPairingEvent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fatherofapps.jnav.JNavigation
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject
import java.util.Base64


fun NamiSavedThreadNetworkInfo.toStringFormat(): String {
    val credentialsBase64String = Base64.getEncoder().encodeToString(credentialsDataset)

    val json = JSONObject()
    json.put("name", name)
    json.put("panId", panId)
    json.put("credentials", credentialsBase64String)

    return json.toString()
}

fun String.toNamiSavedThreadNetworkInfo(): NamiSavedThreadNetworkInfo {
    val json = JSONObject(this)
    val name = json.optString("name")
    val panId = json.optInt("panId")
    val credentials = json.optString("credentials")
    val decodeCredentials = Base64.getDecoder().decode(credentials)
    return NamiSavedThreadNetworkInfo(
        name = name,
        panId = panId.toInt(),
        credentialsDataset = decodeCredentials
    )
}

class DemoCoreSDKActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NamiSDK.enableReleaseLog()
        val namiLocalStorage = ai.nami.demo.common.NamiLocalStorage.getInstance(this)
        registerNamiPairingEvent {
            onConnectThreadNetworkSuccess { threadNetworkCredentials, key ->
                namiLocalStorage.saveThreadNetworkCredential(
                    key,
                    threadNetworkCredentials.toStringFormat()
                )
            }

            onGetSavedThreadCredentials { key1, key2, key3 ->
                val list = namiLocalStorage.listThreadCredentials.firstOrNull()?.toList()
                list?.firstOrNull { it.first == key2 }?.second?.toNamiSavedThreadNetworkInfo()

            }
        }
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

    val onExit: () -> Unit = {
        NamiPairingSdk.reset()
        onBack(SkyNetHomeRouteNavigation, false)
    }


    NavHost(navController = navHostController, startDestination = SkyNetHomeRouteNavigation.route) {

        composable(route = SkyNetHomeRouteNavigation.route) {
            SkyNetHomeRoute(onNext = { isPositioning ->
                onNavigateTo(
                    SkyNetInfoNavigation,
                    SkyNetInfoNavigation.createRoute(isPositioning)
                )
            }, onBack = {
                navHostController.popBackStack()
            })

        }

        composable(
            route = SkyNetInfoNavigation.route,
            arguments = SkyNetInfoNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val isOpenForPositioning = SkyNetInfoNavigation.isOpenForPositioning(it)
                SkyNetInfoRoute(
                    isOpenForPositioning = isOpenForPositioning,
                    onNext = { roomId, deviceCategory, deviceUrn ->
                        val placeInfo = NamiSDK.getPlaceInfo(roomId)
                        if (isOpenForPositioning && deviceUrn != null) {
                            NamiPositioningViewModelModule.init(navHostController.context)
                            val route = SkyNetWidarRecommendationNavigation.createRoute(
                                deviceUrn = deviceUrn,
                                placeId = placeInfo.placeId,
                                deviceHost = null,
                                devicePort = 0
                            )
                            onNavigateTo(SkyNetWidarRecommendationNavigation, route)
                        } else {
                            NamiPairingViewModelModule.init(navHostController.context)
                            val route = SkyNetQRCodeNavigation.createRoute(
                                deviceCategory = deviceCategory.id,
                                roomId = placeInfo.roomId,
                                placeId = placeInfo.placeId,
                                zoneId = placeInfo.zoneId,
                                zoneName = placeInfo.zoneName
                            )
                            onNavigateTo(SkyNetQRCodeNavigation, route)
                        }
                    }, onBack = {
                        onBack(null, false)
                    }, viewModel = SkyNetInfoViewModel()
                )
            }
        }

        pairingGraph(
            onNavigateTo = onNavigateTo,
            onBack = onBack,
            onExitPairing = onExit,
            navHostController = navHostController
        )

        positioningGraph(
            onNavigateTo = onNavigateTo,
            onBack = onBack,
            onExitPositioning = onExit
        )

    }
}