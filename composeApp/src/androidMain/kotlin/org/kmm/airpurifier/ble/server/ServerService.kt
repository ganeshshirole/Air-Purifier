package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattService

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual data class ServerService(private val native: ServerBleGattService) {

    actual val uuid: Uuid = native.uuid

    actual val characteristics: List<ServerCharacteristic> = native.characteristics
        .map { ServerCharacteristic(it) }

    actual fun findCharacteristic(uuid: Uuid): ServerCharacteristic? {
        return characteristics.first { it.uuid == uuid }
    }
}
