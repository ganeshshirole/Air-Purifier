package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import org.kmm.airpurifier.ble.client.toUuid
import platform.CoreBluetooth.CBMutableCharacteristic
import platform.CoreBluetooth.CBPeripheralManager
import platform.CoreBluetooth.CBService

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ServerService(
    val service: CBService,
    private val manager: CBPeripheralManager,
    private val notificationsRecords: NotificationsRecords,
) {

    actual val uuid: Uuid = service.UUID.toUuid()

    actual val characteristics: List<ServerCharacteristic> = service.characteristics
        ?.map { it as CBMutableCharacteristic }
        ?.map { ServerCharacteristic(it, manager, notificationsRecords) }
        ?: emptyList()

    actual fun findCharacteristic(uuid: Uuid): ServerCharacteristic? {
        return characteristics.find { it.uuid == uuid }
    }

    fun onEvent(event: ServerRequest) {
        characteristics.forEach { it.onEvent(event) }
    }
}
