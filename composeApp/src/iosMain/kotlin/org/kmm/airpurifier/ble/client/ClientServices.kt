package org.kmm.airpurifier.ble.client

import com.benasher44.uuid.Uuid
import platform.CoreBluetooth.CBPeripheral
import platform.CoreBluetooth.CBService

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientServices(
    private val peripheral: CBPeripheral,
    private val native: List<CBService>,
) {

    private val services = native.map { ClientService(peripheral, it) }

    actual fun findService(uuid: Uuid): ClientService? {
        return services.firstOrNull { it.uuid == uuid }
    }

    internal fun onEvent(event: IOSGattEvent) {
        services.forEach { it.onEvent(event) }
    }
}
