package org.kmm.airpurifier.ble.client

import android.annotation.SuppressLint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Client(private val client: AndroidClient) {

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
