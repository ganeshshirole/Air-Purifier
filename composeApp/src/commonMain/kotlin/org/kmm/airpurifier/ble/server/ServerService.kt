package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ServerService {
    val uuid: Uuid
    val characteristics: List<ServerCharacteristic>
    fun findCharacteristic(uuid: Uuid): ServerCharacteristic?
}
