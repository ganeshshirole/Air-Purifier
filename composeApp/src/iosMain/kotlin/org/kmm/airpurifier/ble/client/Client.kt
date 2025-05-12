package org.kmm.airpurifier.ble.client

import kotlinx.coroutines.flow.Flow

@Suppress("CONFLICTING_OVERLOADS", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Client(
    private val client: IOSClient
) {

    actual suspend fun connect(address: String): Flow<Boolean> {
        return client.connect(address)
    }

    actual fun disconnect() {
        return client.disconnect()
    }

    actual suspend fun discoverServices(): ClientServices {
        return client.discoverServices()
    }

    actual fun isConnected() = client.isConnected()
}
