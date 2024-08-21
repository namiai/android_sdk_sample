package ai.nami.demo.sdk.pairing.shared

import ai.nami.sdk.model.DeviceCategory
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

object PairingSuccessNavigation {

    const val deviceNameArg = "deviceNameArg"

    const val zoneNameArg = "zoneNameArg"

    const val roomIdArg = "roomIdArg"

    const val placeIdArg = "placeIdArg"

    const val deviceCategoryArg = "deviceCategoryArg"

    const val zoneIdArg = "zoneIdArg"

    private const val baseRoute = "pairing_success_route"

    val route =
        "$baseRoute/$deviceCategoryArg={$deviceCategoryArg}&$zoneNameArg={$zoneNameArg}&$deviceNameArg={$deviceNameArg}&$roomIdArg={$roomIdArg}&$placeIdArg={$placeIdArg}&$zoneIdArg={$zoneIdArg}"


    fun createRoute(
        deviceName: String,
        zoneName: String,
        roomId: Int,
        placeId: Int,
        deviceCategory: DeviceCategory,
        zoneId: Int
    ): String {
        val encodedDeviceName = Uri.encode(deviceName)
        val encodedZoneName = Uri.encode(zoneName)
        return "$baseRoute/$deviceCategoryArg=${deviceCategory.categoryName}&$zoneNameArg=$encodedZoneName&$deviceNameArg=$encodedDeviceName&$roomIdArg=$roomId&$placeIdArg=$placeId&$zoneIdArg=$zoneId"
    }

    fun getDeviceName(entry: NavBackStackEntry): String {
        val encodedDeviceName = entry.arguments?.getString(deviceNameArg)
            ?: throw IllegalArgumentException("Device's name is required.")
        return Uri.decode(encodedDeviceName)
    }

    fun getZoneName(entry: NavBackStackEntry): String {
        val encodedZoneName = entry.arguments?.getString(zoneNameArg)
            ?: throw IllegalArgumentException("zoneName is required")
        return Uri.decode(encodedZoneName)
    }

    fun placeId(entry: NavBackStackEntry): Int =
        entry.arguments?.getInt(placeIdArg) ?: throw IllegalArgumentException("placeId is required")

    fun roomId(entry: NavBackStackEntry) =
        entry.arguments?.getInt(roomIdArg) ?: throw Exception("roomId is required")

    fun deviceCategory(entry: NavBackStackEntry): DeviceCategory {
        val categoryName = entry.arguments?.getString(deviceCategoryArg)
            ?: throw Exception("deviceCategory is required")
        return DeviceCategory.from(categoryName)
    }

    fun zoneId(entry: NavBackStackEntry) =
        entry.arguments?.getInt(zoneIdArg) ?: throw Exception("zoneId is required")

    fun arguments() = listOf(
        navArgument(deviceNameArg) {
            type = NavType.StringType
            nullable = false
        },
        navArgument(zoneNameArg) {
            type = NavType.StringType
            nullable = false
        },
        navArgument(roomIdArg) {
            type = NavType.IntType
        },
        navArgument(placeIdArg) {
            type = NavType.IntType
        },
        navArgument(deviceCategoryArg) {
            type = NavType.StringType
            nullable = false
        },
        navArgument(zoneIdArg) {
            type = NavType.IntType
        }
    )
}


@Composable
fun PairingSuccessScreen(
    onPairAnotherDevice: () -> Unit,
    onFinishPairing: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text("Pairing is successfully")
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = {
            onPairAnotherDevice()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Pair another device")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            onFinishPairing()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Done")
        }
    }
}

@Preview
@Composable
fun PairingSuccessScreenPreview() {
    PairingSuccessScreen(onFinishPairing = {}, onPairAnotherDevice = {})
}