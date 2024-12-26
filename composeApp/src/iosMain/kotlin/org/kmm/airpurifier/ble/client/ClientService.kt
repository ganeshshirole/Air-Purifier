package org.kmm.airpurifier.ble.client

import com.benasher44.uuid.Uuid
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBPeripheral
import platform.CoreBluetooth.CBService

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientService(private val peripheral: CBPeripheral, private val native: CBService) {

    private val characteristics = native.characteristics
        ?.map { it as CBCharacteristic }
        ?.map { ClientCharacteristic(peripheral, it) }
        ?: emptyList()

    actual val uuid: Uuid = native.UUID.toUuid()

    actual fun findCharacteristic(uuid: Uuid): ClientCharacteristic? {
        return characteristics.firstOrNull { it.uuid == uuid }
    }

    internal fun onEvent(event: IOSGattEvent) {
        (event as? CharacteristicEvent)?.let {
            characteristics.onEach { it.onEvent(event) }
        }
    }
}
