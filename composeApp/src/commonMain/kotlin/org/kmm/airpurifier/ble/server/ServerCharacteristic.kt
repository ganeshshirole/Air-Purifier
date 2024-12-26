package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ServerCharacteristic {
    val uuid: Uuid
    val properties: List<GattProperty>
    val permissions: List<GattPermission>
    val descriptors: List<ServerDescriptor>
    val value: Flow<ByteArray>
    suspend fun setValue(value: ByteArray)
}
