package ai.nami.demo.coreSdk.positioning

import ai.nami.demo.coreSdk.common.lifecycleIsResumed
import ai.nami.demo.coreSdk.positioning.error.SkyNetWidarErrorNavigation
import ai.nami.demo.coreSdk.positioning.error.SkyNetWidarErrorRoute
import ai.nami.demo.coreSdk.positioning.position.SkyNetWidarPositionNavigation
import ai.nami.demo.coreSdk.positioning.position.SkyNetWidarPositionRoute
import ai.nami.demo.coreSdk.positioning.recommendations.SkyNetWidarRecommendationNavigation
import ai.nami.demo.coreSdk.positioning.recommendations.SkyNetWidarRecommendationRoute
import ai.nami.demo.coreSdk.positioning.success.SkyNetWidarSuccessNavigation
import ai.nami.demo.coreSdk.positioning.success.SkyNetWidarSuccessRoute
import ai.nami.demo.coreSdk.shared.SkyNetHomeRouteNavigation
import ai.nami.sdk.positioning.model.PositioningErrorCode
import ai.nami.sdk.positioning.viewmodels.di.NamiPositioningViewModelModule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fatherofapps.jnav.JNavigation

const val PositioningGraphRoute = "sky_net_positioning_route"

fun NavGraphBuilder.positioningGraph(
    onNavigateTo: (navigation: JNavigation, route: String?) -> Unit,
    onBack: (navigation: JNavigation?, inclusive: Boolean) -> Unit,
    onExitPositioning: () -> Unit
) {
    navigation(
        route = PositioningGraphRoute,
        startDestination = SkyNetWidarRecommendationNavigation.route
    ) {

        composable(
            route = SkyNetWidarRecommendationNavigation.route,
            arguments = SkyNetWidarRecommendationNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPositioningViewModelModule.provideWidarRecommendationsModel()
                SkyNetWidarRecommendationRoute(
                    deviceUrn = SkyNetWidarRecommendationNavigation.deviceUrn(it),
                    deviceHost = SkyNetWidarRecommendationNavigation.deviceHost(it),
                    devicePort = SkyNetWidarRecommendationNavigation.devicePort(it),
                    placeId = SkyNetWidarRecommendationNavigation.placeId(it),
                    onBack = {
                        onBack(SkyNetHomeRouteNavigation, false)
                    },
                    onNavigatePositioningScreen = {
                        onNavigateTo(
                            SkyNetWidarPositionNavigation,
                            SkyNetWidarPositionNavigation.createRoute()
                        )
                    },
                    onNavigateErrorScreen = { errorCode ->
                        onNavigateTo(
                            SkyNetWidarErrorNavigation,
                            SkyNetWidarErrorNavigation.createRoute(errorCode = errorCode.code)
                        )
                    },
                    viewModel = viewModel
                )
            }
        }

        composable(
            route = SkyNetWidarPositionNavigation.route,

            ) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPositioningViewModelModule.provideWidarPositionViewModel()
                SkyNetWidarPositionRoute(
                    viewModel = viewModel,
                    onExitPositioning = onExitPositioning,
                    onNavigatePositionSuccessScreen = {
                        onNavigateTo(
                            SkyNetWidarSuccessNavigation,
                            SkyNetWidarSuccessNavigation.createRoute(deviceName = "WiDAR device ") // for demo purpose
                        )
                    },
                    onNavigateErrorScreen = { errorCode ->
                        onNavigateTo(
                            SkyNetWidarErrorNavigation,
                            SkyNetWidarErrorNavigation.createRoute(errorCode = errorCode.code)
                        )
                    }
                )
            }
        }

        composable(
            route = SkyNetWidarSuccessNavigation.route,
            arguments = SkyNetWidarSuccessNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                SkyNetWidarSuccessRoute(
                    deviceName = SkyNetWidarSuccessNavigation.deviceName(it),
                    onDone = onExitPositioning
                )
            }
        }

        composable(
            route = SkyNetWidarErrorNavigation.route,
            arguments = SkyNetWidarErrorNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val code = SkyNetWidarErrorNavigation.errorCode(it)
                SkyNetWidarErrorRoute(
                    errorCode = PositioningErrorCode.from(code),
                    onTryAgain = {
                        onBack(SkyNetWidarRecommendationNavigation, false)
                    },
                    onExit = onExitPositioning
                )
            }
        }
    }
}