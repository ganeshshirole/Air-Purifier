package org.kmm.airpurifier.ble.client

import com.benasher44.uuid.Uuid
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattService

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientService(private val service: ClientBleGattService) {

    actual val uuid: Uuid = service.uuid

    actual fun findCharacteristic(uuid: Uuid): ClientCharacteristic? {
        return service.findCharacteristic(uuid)?.let { ClientCharacteristic(it) }
    }
}
