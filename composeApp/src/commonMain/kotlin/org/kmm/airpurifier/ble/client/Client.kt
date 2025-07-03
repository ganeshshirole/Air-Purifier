package org.kmm.airpurifier.ble.client

import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Client {

    suspend fun connect(address: String): Flow<Boolean>

    fun disconnect()

    suspend fun discoverServices(): ClientServices

    fun isConnected(): Boolean
}

sealed interface DeviceConnectionState

data object DeviceConnected : DeviceConnectionState

data object DeviceDisconnected : DeviceConnectionState


sealed interface OperationStatus

data object OperationSuccess : OperationStatus

data object OperationError : OperationStatus
