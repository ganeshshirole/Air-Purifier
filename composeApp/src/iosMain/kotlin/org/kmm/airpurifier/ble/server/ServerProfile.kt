package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import platform.CoreBluetooth.CBPeripheralManager
import platform.CoreBluetooth.CBService

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual data class ServerProfile(
    private val nativeServices: List<CBService>,
    private val manager: CBPeripheralManager,
    private val notificationsRecords: NotificationsRecords,
) {

    actual val services: List<ServerService> = nativeServices.map {
        ServerService(it, manager, notificationsRecords)
    }

    actual fun findService(uuid: Uuid): ServerService? {
        return services.first { it.uuid == uuid }
    }

    actual fun copyWithNewService(service: ServerService): ServerProfile {
        return copy()
    }

    fun onEvent(event: ServerRequest) {
        services.forEach { it.onEvent(event) }
    }
}
