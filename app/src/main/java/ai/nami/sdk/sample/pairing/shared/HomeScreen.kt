package ai.nami.sdk.sample.pairing.shared

import ai.nami.sdk.pairing.ui.navigation.DeviceCategory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun HomeScreen(showDeviceCategory: Boolean, onPairNamiDevice: (String, String, String?) -> Unit) {

    var sessionCode by remember {
        mutableStateOf("")
    }

    var roomId by remember {
        mutableStateOf("a6283c03-d013-4342-8681-661f7a647a72")
    }

    var deviceCategory: String? by remember(showDeviceCategory) {
        mutableStateOf(null)
    }

    val itemList = listOf(
        "-- optional --",
        DeviceCategory.MESH_SENSOR.categoryName,
        DeviceCategory.SECURITY_POD.categoryName,
        DeviceCategory.WIDAR_SENSOR.categoryName,
        DeviceCategory.WIFI_SENSOR.categoryName,
        DeviceCategory.CONTACT_SENSOR.categoryName
    )
    var selectedIndex by rememberSaveable(showDeviceCategory) { mutableIntStateOf(0) }
    var menuExpanded by remember(showDeviceCategory) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(value = sessionCode, onValueChange = {
            sessionCode = it
        }, modifier = Modifier.fillMaxWidth(), label = {
            Text(text = "Session code")
        })
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = roomId, onValueChange = {
            roomId = it
        }, modifier = Modifier.fillMaxWidth(), label = {
            Text(text = "Room ID")
        })

        if (showDeviceCategory) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Device category: ")
                DropDownListMenu(modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                    menuItems = itemList,
                    menuExpandedState = menuExpanded,
                    selectedIndex = selectedIndex,
                    updateMenuExpandStatus = {
                        menuExpanded = true
                    },
                    onDismissMenuView = {
                        menuExpanded = false
                    },
                    onMenuItemClicked = { index ->
                        menuExpanded = false
                        selectedIndex = index
                        if (index > 0) {
                            deviceCategory = itemList[index]
                        }
                    })
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = {
            onPairNamiDevice(sessionCode, roomId, deviceCategory)
        }) {
            Text("Pair Device")
        }
    }
}

@Composable
fun DropDownListMenu(
    modifier: Modifier = Modifier,
    menuItems: List<String>,
    menuExpandedState: Boolean,
    selectedIndex: Int = -1,
    updateMenuExpandStatus: () -> Unit,
    onDismissMenuView: () -> Unit,
    onMenuItemClicked: (index: Int) -> Unit,
) {
    Box(
        modifier = modifier.clickable(
            onClick = {
                updateMenuExpandStatus()
            },
        )
    ) {
        ConstraintLayout(
            modifier = Modifier.wrapContentSize()
        ) {

            val (label, dropDownList) = createRefs()

            Text(
                text = if (selectedIndex > -1) menuItems[selectedIndex] else "",
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .wrapContentWidth()
                    .alpha(if (menuExpandedState) 0.5f else 1f)
                    .wrapContentHeight()
                    .constrainAs(label) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
            )

            Box(modifier = Modifier
                .wrapContentWidth()
                .constrainAs(dropDownList) {
                    top.linkTo(label.bottom)
                    end.linkTo(label.end)
                    width = Dimension.fillToConstraints
                }) {
                DropdownMenu(modifier = Modifier
                    .width(180.dp)
                    .background(color = Color.White)
                    .wrapContentHeight(),
                    expanded = menuExpandedState,
                    onDismissRequest = { onDismissMenuView() }) {
                    menuItems.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            onClick = {
                                onMenuItemClicked(index)
                            }) {
                            Text(
                                text = item,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(start = 10.dp),
                            )
                        }

                        if (index < menuItems.size - 1) {
                            Divider(color = Color.Gray, thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}