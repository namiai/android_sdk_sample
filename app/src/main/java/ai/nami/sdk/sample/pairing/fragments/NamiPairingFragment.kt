package ai.nami.sdk.sample.pairing.fragments

import ai.nami.sdk.pairing.ui.navigation.NamiPairingSdkNavigation
import ai.nami.sdk.pairing.ui.navigation.NamiPairingSdkRoute
import ai.nami.sdk.pairing.ui.navigation.namiPairingSdkGraph
import ai.nami.sdk.sample.databinding.FragmentNamiPairingBinding
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.sample.utils.formatDeviceUrn
import ai.nami.sdk.widar.core.WidarSdkSession
import ai.nami.sdk.widar.ui.navigation.NamiWidarSdkRoute
import ai.nami.sdk.widar.ui.navigation.WidarSdkNavigation
import ai.nami.sdk.widar.ui.navigation.namiWidarSDKGraph
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavHost
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

                        composable(route = "temp_loading"){
                            LaunchedEffect(key1 = Unit){
                                val route = NamiPairingSdkNavigation.createRoute(sessionCode, roomId)
                                navController.navigate(route)
                            }
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                CircularProgressIndicator(modifier = Modifier.size(36.dp))
                            }
                        }

                        namiPairingSdkGraph(
                            navController = navController,
                            onPairSuccess = { listPairingDeviceInfo, parameters, output ->
                                val isWidarDevice = output.isWidarDevice
                                val placeId = output.placeId
                                val deviceName = output.deviceName
                                val deviceUrn = listPairingDeviceInfo.firstOrNull()?.deviceUrn

                                if (parameters != null && isWidarDevice && deviceUrn != null && WidarSdkSession.initFromPairingParameter(
                                        parameters
                                    )
                                ) {
                                    val formatDeviceUrn =
                                        formatDeviceUrn(deviceUrn, isLowerCase = false)
                                    val widarRoute = WidarSdkNavigation.createRoute(
                                        deviceUrn = formatDeviceUrn,
                                        placeId = placeId,
                                        deviceName = deviceName
                                    )
                                    navController.navigate(widarRoute)
                                } else {
                                    navigateToSuccessFragment()
                                }
                            }
                        )

                        namiWidarSDKGraph(navController = navController, onCancel = {
                            navController.popBackStack(NamiWidarSdkRoute, true)
                        }, onPositionDone = {
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