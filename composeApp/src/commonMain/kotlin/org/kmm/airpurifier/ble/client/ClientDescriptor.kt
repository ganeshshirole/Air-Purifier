package org.kmm.airpurifier.ble.client

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ClientDescriptor {

    suspend fun write(value: ByteArray)

    suspend fun read(): ByteArray
}
