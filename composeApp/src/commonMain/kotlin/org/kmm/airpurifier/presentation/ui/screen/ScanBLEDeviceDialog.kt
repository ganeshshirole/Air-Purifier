package org.kmm.airpurifier.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.kmm.airpurifier.RequestPermissions
import org.kmm.airpurifier.presentation.ui.component.LoopAnimation
import org.kmm.airpurifier.presentation.ui.viewmodel.ScannerViewModel
import org.kmm.airpurifier.domain.model.MyDevice
import org.kmm.airpurifier.util.SecondaryColor
import org.koin.compose.viewmodel.koinViewModel

private const val TAG: String = "ScanBleDeviceDialog"

@Composable
fun ScanBleDeviceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    maxHeight: Dp,
    onItemClick: (ioTDevice: MyDevice) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                elevation = 0.dp
            ) {
                RequestPermissions {
                    DialogContent(maxHeight) {
                        onItemClick(it)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogContent(maxHeight: Dp, onItemClick: (ioTDevice: MyDevice) -> Unit) {
    val viewModel = koinViewModel<ScannerViewModel>()
    val allDevices = viewModel.stateAllDevices.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.scan()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(maxHeight * 0.9f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoopAnimation()
        Spacer(modifier = Modifier.height(16.dp))
        ListWithDividers(
            modifier = Modifier.weight(1f),
            items = allDevices.value,
            onItemClick = {
                onItemClick(it)
            },
            onDelete = {
                viewModel.selectedIoTDevice = it
                showDeleteDialog = true
            },
            onRename = {
                viewModel.selectedIoTDevice = it
                showRenameDialog = true
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red,
            ),
            onClick = { /* Handle click */ },
            modifier = Modifier
                .width(200.dp)
        ) {
            Text(
                text = "Rescan",
                color = Color.White
            ) // Change text color if needed
        }
    }

    // Show Delete Dialog
    DeleteAlertDialog(
        showDialog = showDeleteDialog,
        onDeleteConfirm = {
            // Perform delete action here
            showDeleteDialog = false
            viewModel.deleteDevice(viewModel.selectedIoTDevice?.address ?: "")
        },
        onDismiss = {
            showDeleteDialog = false
        }
    )

    RenameDeviceDialog(
        showDialog = showRenameDialog,
        currentName = viewModel.selectedIoTDevice?.name ?: "",
        onDismiss = { showRenameDialog = false },
        onSave = { newName ->
            viewModel.renameDevice(
                viewModel.selectedIoTDevice?.address ?: "",
                newName
            )
            showRenameDialog = false
        }
    )
}

@Composable
fun ListItem(
    name: String,
    address: String,
    showButtons: Boolean,
    onDeleteClick: () -> Unit,
    onRenameClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = name.ifBlank { "Unknown" },
                fontSize = 14.sp,
                color = SecondaryColor,
                modifier = Modifier.padding(0.dp)
            )
            Text(
                text = address,
                fontSize = 12.sp,
                color = SecondaryColor,
            )
        }

        if (showButtons) {
            IconButton(onClick = onRenameClick, modifier = Modifier.padding(0.dp)) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Rename",
                    tint = SecondaryColor,
                    modifier = Modifier.padding(0.dp)
                )
            }
            IconButton(onClick = onDeleteClick, modifier = Modifier.padding(0.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = SecondaryColor,
                    modifier = Modifier.padding(0.dp)
                )
            }
        }
    }
}


//@Composable
//fun ListItem(name: String, address: String) {
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth().wrapContentHeight(),
//        contentAlignment = Alignment.CenterStart
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//        ) {
//            Text(
//                text = name.ifBlank { "Unknown" },
//                fontSize = 14.sp,
//                color = SecondaryColor,
//                modifier = Modifier.padding(0.dp)
//            )
//            Text(
//                text = address,
//                fontSize = 12.sp,
//                color = SecondaryColor,
//                modifier = Modifier.padding(0.dp)
//            )
//        }
//    }
//}

@Composable
fun ListWithDividers(
    items: List<MyDevice>,
    modifier: Modifier,
    onItemClick: (ioTDevice: MyDevice) -> Unit,
    onDelete: (ioTDevice: MyDevice) -> Unit,
    onRename: (ioTDevice: MyDevice) -> Unit
) {
//    var expandedIndex by remember { mutableStateOf(-1) }

    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(items.size) { index ->
            val myDevice = items[index]
            Column(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { onItemClick(items[index]) }) {

                myDevice.title?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                    Divider(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
                }
                if (!myDevice.isSavedDevice) {
                    ListItem(
                        myDevice.name,
                        myDevice.address,
                        onDeleteClick = {},
                        onRenameClick = {},
                        showButtons = false
                    )
                } else {
                    ListItem(
                        myDevice.name, myDevice.address, onDeleteClick = {
                            onDelete(myDevice)
                        },
                        onRenameClick = {
                            onRename(myDevice)
                        },
                        showButtons = true
                    )
//                    SwappableItemWithActions(
//                        isRevealed = expandedIndex == index,
//                        onExpanded = {
//                            expandedIndex = index
//                            Napier.i("SwappableItemWithActions Expanded", tag = TAG)
//                        },
//                        onCollapsed = {
//                            expandedIndex = -1
//                            Napier.i("SwappableItemWithActions Collapsed", tag = TAG)
//                        },
//                        actions = {
//                            ActionIcon(
//                                onClick = {
//                                    expandedIndex = -1
//                                    onDelete(myDevice)
//                                },
//                                backgroundColor = Color.Red,
//                                iconVector = Icons.Default.Delete,
//                                modifier = Modifier.fillMaxHeight()
//                            )
//                            ActionIcon(
//                                onClick = {
//                                    expandedIndex = -1
//                                    onRename(myDevice)
//                                },
//                                backgroundColor = Color.Yellow,
//                                iconPainter = painterResource(Res.drawable.edit_note),
//                                modifier = Modifier.fillMaxHeight()
//                            )
//                        },
//                    ) {
//                        ListItem(myDevice.name, myDevice.address)
//                    }
                }
                // Divider
                if (index != items.lastIndex) {
                    Divider(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
                }
            }
        }
    }
}

@Composable
fun Divider(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )
    }
}

@Composable
fun DeleteAlertDialog(
    showDialog: Boolean,
    onDeleteConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Delete Item")
            },
            text = {
                Text(text = "Are you sure you want to delete this item? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = onDeleteConfirm) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}


@Composable
fun RenameDeviceDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    showDialog: Boolean
) {
    if (showDialog) {
        var newName by remember { mutableStateOf(currentName) }

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(text = "Rename Device")
            },
            text = {
                Column {
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Device Name") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSave(newName)
                        onDismiss()
                    },
                    enabled = newName.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
