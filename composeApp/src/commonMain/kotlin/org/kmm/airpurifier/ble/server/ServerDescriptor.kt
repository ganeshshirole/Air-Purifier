package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ServerDescriptor {
    val uuid: Uuid
    val permissions: List<GattPermission>
    val value: Flow<ByteArray>
    suspend fun setValue(value: ByteArray)
}
