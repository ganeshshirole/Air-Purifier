package org.kmm.airpurifier.ble.client

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import com.benasher44.uuid.Uuid

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientServices(
    private val bluetoothGatt: BluetoothGatt,
    private val value: List<BluetoothGattService>
) {

    private val services = value.map { ClientService(bluetoothGatt, it) }

    actual fun findService(uuid: Uuid): ClientService? {
        return services.firstOrNull { it.uuid == uuid }
    }

    internal fun onEvent(event: AndroidGattEvent) {
        services.forEach { it.onEvent(event) }
    }
}
