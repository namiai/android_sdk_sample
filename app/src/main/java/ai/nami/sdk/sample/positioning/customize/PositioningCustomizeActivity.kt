package ai.nami.sdk.sample.positioning.customize

import ai.nami.sdk.sample.data.NamiLocalStorage
import ai.nami.sdk.sample.positioning.info.WidarNetworkInfoScreen
import ai.nami.sdk.sample.positioning.info.WidarNetworkInfoViewModel
import ai.nami.sdk.sample.positioning.shared.AfterPositioningScreen
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.widar.WiDARSdk
import ai.nami.sdk.widar.core.WidarSdkSession
import ai.nami.sdk.widar.customizeWidarLayout
import ai.nami.sdk.widar.registerWidarEvent
import ai.nami.sdk.widar.ui.designsystem.theme.NamiThemeData
import ai.nami.sdk.widar.ui.designsystem.theme.WidarSdkColors
import ai.nami.sdk.widar.ui.designsystem.theme.WidarSdkShapes
import ai.nami.sdk.widar.ui.navigation.NamiWidarSdkRoute
import ai.nami.sdk.widar.ui.navigation.WidarSdkNavigation
import ai.nami.sdk.widar.ui.navigation.namiWidarSDKGraph
import ai.nami.sdk.widar.ui.screens.position.WidarPositionNavigation
import ai.nami.sdk.widar.ui.screens.success.WidarPositionSuccessNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class PositioningCustomizeActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customizeWidarLayout {
            customLayout(
                infoLayout = { onBack, onNext ->
                    CustomWidarInfoScreen(onBack = onBack, onNext = onNext)
                }
            )
        }

        registerWidarEvent {
            onRouteChange { data, _ ->
                if (data.currentDestination == WidarPositionNavigation.destination && data.nextDestination == WidarPositionSuccessNavigation.destination) {
                    "note_success_screen"
                } else
                    null
            }
        }

        WiDARSdk.customTheme(
            NamiThemeData(
                shapes = customNamiSDKShapes,
                colors = customNamiSDKColors
            )
        )

        setContent {
            NamiSDKSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CustomizeHostScreen()
                }
            }
        }
    }
}


@Composable
fun CustomizeHostScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "widar_info") {
        val widarNetworkInfoViewModel =
            WidarNetworkInfoViewModel(NamiLocalStorage.getInstance(context = navController.context))
        namiWidarSDKGraph(navController = navController, onCancel = {
            navController.popBackStack(NamiWidarSdkRoute, true)
        }, onPositionDone = {
            navController.navigate("done_position")
        })

        composable(route = "widar_info") {

            WidarNetworkInfoScreen(viewModel = widarNetworkInfoViewModel) { placeId, sessionCode, deviceUrn ->
                WidarSdkSession.init(sessionCode = sessionCode)
                val widarRoute = WidarSdkNavigation.createRoute(
                    deviceUrn = deviceUrn,
                    placeId = placeId
                )
                navController.navigate(widarRoute)

            }

        }

        composable(route = "note_success_screen") {
            NoteBeforeSuccessScreen {
                WidarSdkNavigation.resumeRoute(shouldCancelPairing = false)?.let {
                    navController.navigate(it)
                }
            }
        }

        composable(route = "done_position") {
            AfterPositioningScreen()
        }
    }
}


@Composable
fun CustomWidarInfoScreen(onBack: () -> Unit, onNext: () -> Unit) {

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Custom Widar Info Screen")
        }

        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onNext) {
            Text(text = "Click to continue with Widar SDK")
        }
    }

    BackHandler {
        onBack()
    }
}

@Composable
fun NoteBeforeSuccessScreen(onNext: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Note Screen")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("After positioning WiDAR device successfully, you should not change it. ")
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onNext) {
            Text(text = "Click to continue with Widar SDK")
        }
    }
}


val customNamiSDKColors = WidarSdkColors(
    primary = Color(0xffa03671),
    secondary = Color(0xff725762),
    tertiary = Color(0xff7f5539),
    error = Color(0xffba1a1a),
    onError = Color(0xFFffffff),
    background = Color(0xfffffbff),
    primaryText = Color(0xff1f1a1c),
    line = Color(0xff827378),
    statusBarColor = Color(0xfffdd9e7),
    toolbarColor = Color(0xffffd8e7)
)

val customNamiSDKShapes = WidarSdkShapes(

    buttonShape = RoundedCornerShape(16.dp),
    small = RoundedCornerShape(4.dp),

    medium = RoundedCornerShape(4.dp),

    large = RoundedCornerShape(16.dp),
)