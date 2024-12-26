package org.kmm.airpurifier.ble.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.kmm.airpurifier.ble.scanner.IoTDevice
import platform.CoreBluetooth.CBManagerStatePoweredOn

@Suppress("CONFLICTING_OVERLOADS", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Client(
    private val client: IOSClientWrapper
) {

    fun scan(): Flow<List<IoTDevice>> {
        return client.value.scan()
    }

    actual fun connectionStatus(scope: CoroutineScope, onConnectionStateChanged: (Boolean) -> Unit) {
        scope.launch {
            client.value.connectionStatus.collect {
                onConnectionStateChanged(it)
            }
        }
    }

    actual suspend fun connect(device: IoTDevice, scope: CoroutineScope) {
        client.value.connect(device)
    }
    actual suspend fun connect(address: String, scope: CoroutineScope) {
        client.value.connect(address)
    }

    actual suspend fun disconnect() {
        return client.value.disconnect()
    }

    actual suspend fun discoverServices(): ClientServices {
        return client.value.discoverServices()
    }

    actual fun isConnected() = client.value.isConnected()
}
