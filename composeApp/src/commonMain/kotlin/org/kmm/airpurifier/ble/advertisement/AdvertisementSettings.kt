package org.kmm.airpurifier.ble.advertisement

import com.benasher44.uuid.Uuid

data class AdvertisementSettings(
    val name: String,
    val uuid: Uuid
)
