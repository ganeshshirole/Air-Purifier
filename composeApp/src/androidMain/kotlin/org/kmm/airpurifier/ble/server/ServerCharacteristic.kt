package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattCharacteristic

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ServerCharacteristic(
    private val native: ServerBleGattCharacteristic
) {

    actual val uuid: Uuid = native.uuid

    actual val properties: List<GattProperty> = native.properties.toDomainProperties()

    actual val permissions: List<GattPermission> = native.permissions.toDomainPermissions()

    actual val descriptors: List<ServerDescriptor> = native.descriptors.map {
        ServerDescriptor(it)
    }

    actual val value: Flow<ByteArray> = native.value.map { it.value }

    actual suspend fun setValue(value: ByteArray) {
        native.setValueAndNotifyClient(DataByteArray(value))
    }
}
