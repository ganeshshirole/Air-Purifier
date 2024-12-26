package org.kmm.airpurifier

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun RequestPermissions(content: @Composable () -> Unit) {
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    var allPermissionsGranted by remember { mutableStateOf(false) }
    var permissionsDenied by remember { mutableStateOf(false) }
    var isEnabled by remember { mutableStateOf(false) }

    // Define  permissions based on API level
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // Register the  enabling result launcher
    val enableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isEnabled = true
            Log.d("", " enabled successfully.")
        } else {
            isEnabled = false
            Log.d("", " enabling failed or was canceled.")
        }
    }

    // Function to prompt user to enable
    fun promptToEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableLauncher.launch(enableBtIntent)
    }

    // Create a permission launcher for  permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        allPermissionsGranted = permission.values.all { it }
        permissionsDenied = !allPermissionsGranted

        if (allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == false) {
                promptToEnable()
            } else {
                isEnabled = true
            }
        }
    }

    // Ask for  permissions when the Composable is first launched
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissions.toTypedArray())
    }

    //  permissions UI content
    if (permissionsDenied) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(" permissions are required to continue.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                permissionLauncher.launch(permissions.toTypedArray())
            }) {
                Text("Retry")
            }
        }
    } else if (!allPermissionsGranted || !isEnabled) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (allPermissionsGranted) {
                Button(onClick = {
                    permissionLauncher.launch(permissions.toTypedArray())
                }) {
                    Text("Retry")
                }
            } else {
                Text("Requesting  Permissions...")
            }
        }
    } else {
        content() // Show the provided content once  is enabled
    }
}