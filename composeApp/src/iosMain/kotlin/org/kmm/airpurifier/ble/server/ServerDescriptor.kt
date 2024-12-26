package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.kmm.airpurifier.ble.client.toNSData
import org.kmm.airpurifier.ble.client.toUuid
import platform.CoreBluetooth.CBDescriptor
import platform.Foundation.setValue

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ServerDescriptor(
    private val descriptor: CBDescriptor
) {

    actual val uuid: Uuid = descriptor.UUID.toUuid()

    actual val permissions: List<GattPermission> = emptyList()

    private val _value = MutableStateFlow(byteArrayOf())
    actual val value: Flow<ByteArray> = _value

    actual suspend fun setValue(value: ByteArray) {
        descriptor.setValue(value.toNSData(), forKeyPath = "")
    }
}
