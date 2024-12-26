package org.kmm.airpurifier.ble.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.kmm.airpurifier.ble.advertisement.IOSServerWrapper
import org.kmm.airpurifier.ble.scanner.IoTDevice

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Server(private val server: IOSServerWrapper) {

    actual val connections: Flow<Map<IoTDevice, ServerProfile>>
        get() = server.value.connections

    actual suspend fun startServer(services: List<BleServerServiceConfig>, scope: CoroutineScope) {
        server.value.startServer(services)
    }

    actual suspend fun stopServer() {
        server.value.stopAdvertising()
    }
}
