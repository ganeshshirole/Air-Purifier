package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattDescriptor

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual data class ServerDescriptor(
    val native: ServerBleGattDescriptor
) {
    actual val uuid: Uuid = native.uuid

    actual val permissions: List<GattPermission> = emptyList() //FIXME

    actual val value: Flow<ByteArray> = native.value.map { it.value }

    actual suspend fun setValue(value: ByteArray) {
        native.setValue(DataByteArray(value))
    }
}
