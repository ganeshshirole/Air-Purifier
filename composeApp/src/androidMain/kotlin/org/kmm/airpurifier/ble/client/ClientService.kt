package org.kmm.airpurifier.ble.client

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import com.benasher44.uuid.Uuid

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientService(private val bluetoothGatt: BluetoothGatt, private val service: BluetoothGattService) {

    private val characteristics = service.characteristics
        ?.map { ClientCharacteristic(bluetoothGatt, it) }
        ?: emptyList()

    actual val uuid: Uuid = service.uuid

    actual fun findCharacteristic(uuid: Uuid): ClientCharacteristic? {
        return characteristics.firstOrNull { it.uuid == uuid }
    }

    internal fun onEvent(event: AndroidGattEvent) {
        (event as? CharacteristicEvent)?.let {
            characteristics.onEach { it.onEvent(event) }
        }
    }
}
