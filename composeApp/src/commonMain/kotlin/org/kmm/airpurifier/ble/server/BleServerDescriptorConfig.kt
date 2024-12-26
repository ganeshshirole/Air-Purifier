package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid

data class BleServerDescriptorConfig(
    val uuid: Uuid,
    val permissions: List<GattPermission>
)