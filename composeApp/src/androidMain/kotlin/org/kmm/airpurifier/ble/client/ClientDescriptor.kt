package org.kmm.airpurifier.ble.client

import android.annotation.SuppressLint
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattDescriptor
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray

@SuppressLint("MissingPermission")
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientDescriptor(private val descriptor: ClientBleGattDescriptor) {

    actual suspend fun write(value: ByteArray) {
        descriptor.write(DataByteArray(value))
    }

    actual suspend fun read(): ByteArray {
        return descriptor.read().value
    }
}
