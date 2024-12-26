package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattServices

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual data class ServerProfile(private val native: ServerBleGattServices) {

    actual val services: List<ServerService> = native.services.map {
        ServerService(it)
    }

    actual fun findService(uuid: Uuid): ServerService? {
        return services.find { it.uuid == uuid }
    }

    actual fun copyWithNewService(service: ServerService): ServerProfile {
        return copy()
    }
}
