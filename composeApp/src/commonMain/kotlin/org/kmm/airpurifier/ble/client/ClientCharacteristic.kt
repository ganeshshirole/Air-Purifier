package org.kmm.airpurifier.ble.client

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ClientCharacteristic {

    suspend fun getNotifications(): Flow<ByteArray>

    suspend fun write(value: ByteArray, writeType: WriteType = WriteType.DEFAULT)

    suspend fun read(): ByteArray
}
