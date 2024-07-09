package ai.nami.sdk.sample.pairing.fragments

import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.routing.pairing.ui.navigation.NamiPairingSdkNavigation
import ai.nami.sdk.routing.pairing.ui.navigation.namiPairingSdkGraph
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositionSdkNavigation
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositioningSdkRoute
import ai.nami.sdk.routing.positioning.ui.navigation.namiPositioningSDKGraph
import ai.nami.sdk.sample.databinding.FragmentNamiPairingBinding
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class NamiPairingFragment: Fragment() {

    private lateinit var dataBinding: FragmentNamiPairingBinding

    private val args: NamiPairingFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dataBinding =
            FragmentNamiPairingBinding.inflate(inflater, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        val sessionCode = args.sessionCode
        val roomId = args.roomId


        dataBinding.composeView.setContent {
            //setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            NamiSDKSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "temp_loading") {

                        composable(route = "temp_loading") {
                            LaunchedEffect(key1 = Unit) {
                                Log.e("debug_xml_demo","sessionCode: $sessionCode  ----  roomId: $roomId")
                                val route =
                                    NamiPairingSdkNavigation.createRoute(sessionCode, roomId)
                                navController.navigate(route)
                            }
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Loading ....")
//                                CircularProgressIndicator(modifier = Modifier.size(36.dp))
                            }
                        }

                        namiPairingSdkGraph(
                            navController = navController,
                            onPairSuccess = { listPairingDeviceInfo, parameters, output ->
                                val isWidarDevice = output.isWidarDevice
                                val placeId = output.placeId
                                val deviceName = output.deviceName
                                val deviceUrn = listPairingDeviceInfo.firstOrNull()?.deviceUrn
                                if (isWidarDevice && deviceUrn != null) {
                                    NamiSDKUI.initPositioning(context = navController.context)
                                    val widarRoute = NamiPositionSdkNavigation.createRoute(
                                        deviceUrn = deviceUrn,
                                        placeId = placeId,
                                        deviceName = deviceName
                                    )
                                    navController.navigate(widarRoute)
                                } else {
                                    NamiSDKUI.resetPairingSession()
                                    navController.navigate("pair-success")
                                }
                            }
                        )

                        namiPositioningSDKGraph(navController = navController, onCancel = {
                            navController.popBackStack(NamiPositioningSdkRoute, true)
                        }, onPositionDone = {
                            NamiSDKUI.resetPairingSession()
                            NamiSDKUI.resetPositioningSession()
                            navigateToSuccessFragment()
                        })
                    }
                }
            }
        }


        return dataBinding.root

    }


    private fun navigateToSuccessFragment() {
        this.findNavController()
            .navigate(NamiPairingFragmentDirections.actionNamiPairingFragmentToSuccessFragment())
    }
}