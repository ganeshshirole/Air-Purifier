package org.kmm.airpurifier.ble.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.kmm.airpurifier.ble.scanner.IoTDevice

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Client {

    suspend fun connect(device: IoTDevice, scope: CoroutineScope)

    suspend fun connect(address: String, scope: CoroutineScope)

    suspend fun disconnect()

    suspend fun discoverServices(): ClientServices

    fun isConnected(): Boolean

    fun connectionStatus(
        scope: CoroutineScope,
        onConnectionStateChanged: (Boolean) -> Unit
    )
}

sealed interface DeviceConnectionState

data object DeviceConnected : DeviceConnectionState

data object DeviceDisconnected : DeviceConnectionState


sealed interface OperationStatus

data object OperationSuccess : OperationStatus

data object OperationError : OperationStatus
