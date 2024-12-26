package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid

data class BleServerCharacteristicConfig(
    val uuid: Uuid,
    val properties: List<GattProperty>,
    val permissions: List<GattPermission>,
    val descriptors: List<BleServerDescriptorConfig>
)
