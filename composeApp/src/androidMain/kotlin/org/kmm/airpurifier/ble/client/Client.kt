package org.kmm.airpurifier.ble.client

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectionStatus
import no.nordicsemi.android.kotlin.ble.core.data.GattConnectionState
import org.kmm.airpurifier.ble.scanner.IoTDevice

@SuppressLint("MissingPermission")
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Client(
    private val context: Context
) {

    private var client: ClientBleGatt? = null

    actual fun connectionStatus(scope: CoroutineScope, onConnectionStateChanged: (Boolean) -> Unit) {
        scope.launch {
            client?.connectionStateWithStatus?.collect { connectionStatus ->
                when (connectionStatus?.state) {
                    GattConnectionState.STATE_CONNECTED -> {
                        onConnectionStateChanged(true)
                    }

                    else -> {
                        onConnectionStateChanged(false)
                    }
                }
            }
        }
    }

    actual suspend fun connect(device: IoTDevice, scope: CoroutineScope) {
        client = ClientBleGatt.connect(context, device.device as ServerDevice, scope)
    }

    actual suspend fun connect(address: String, scope: CoroutineScope) {
        client = ClientBleGatt.connect(context, address, scope)
    }

    actual suspend fun disconnect() {
        client?.disconnect()
    }

    actual fun isConnected() = client?.isConnected == true

    actual suspend fun discoverServices(): ClientServices {
        return client!!.discoverServices().toCrossplatform()
    }
}
