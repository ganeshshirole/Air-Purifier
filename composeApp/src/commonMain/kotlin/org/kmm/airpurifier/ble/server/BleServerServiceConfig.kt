package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid

data class BleServerServiceConfig(
    val uuid: Uuid,
    val characteristics: List<BleServerCharacteristicConfig>
)
