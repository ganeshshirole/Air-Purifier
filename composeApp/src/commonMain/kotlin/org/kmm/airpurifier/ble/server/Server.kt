package org.kmm.airpurifier.ble.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.kmm.airpurifier.ble.scanner.IoTDevice

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Server {

    val connections: Flow<Map<IoTDevice, ServerProfile>>

    suspend fun startServer(services: List<BleServerServiceConfig>, scope: CoroutineScope)

    suspend fun stopServer()
}
