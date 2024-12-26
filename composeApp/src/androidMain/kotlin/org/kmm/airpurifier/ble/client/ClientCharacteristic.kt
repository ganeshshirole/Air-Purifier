package org.kmm.airpurifier.ble.client

import android.annotation.SuppressLint
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.data.BleWriteType

@SuppressLint("MissingPermission")
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientCharacteristic(private val characteristic: ClientBleGattCharacteristic) {

    actual fun findDescriptor(uuid: Uuid): ClientDescriptor? {
        return characteristic.findDescriptor(uuid)?.let { ClientDescriptor(it) }
    }

    actual suspend fun getNotifications(): Flow<ByteArray> {
        return characteristic.getNotifications().map { it.value }
    }

    actual suspend fun write(value: ByteArray, writeType: WriteType) {
        val bleWriteType = when (writeType) {
            WriteType.DEFAULT -> BleWriteType.DEFAULT
            WriteType.NO_RESPONSE -> BleWriteType.NO_RESPONSE
        }
        characteristic.write(DataByteArray(value), bleWriteType)
    }

    actual suspend fun read(): ByteArray {
        return characteristic.read().value
    }
}
