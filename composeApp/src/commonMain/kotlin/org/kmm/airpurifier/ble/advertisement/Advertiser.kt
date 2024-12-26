package org.kmm.airpurifier.ble.advertisement

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Advertiser {

    suspend fun advertise(settings: AdvertisementSettings)

    suspend fun stop()
}
