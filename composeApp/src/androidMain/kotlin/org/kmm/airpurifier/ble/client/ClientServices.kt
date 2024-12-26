package org.kmm.airpurifier.ble.client

import com.benasher44.uuid.Uuid
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientServices(private val value: ClientBleGattServices) {

    actual fun findService(uuid: Uuid): ClientService? {
        return value.findService(uuid)?.let { ClientService(it) }
    }
}
