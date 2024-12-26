package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ServerProfile {
    val services: List<ServerService>
    fun findService(uuid: Uuid): ServerService?
    fun copyWithNewService(service: ServerService): ServerProfile
}
