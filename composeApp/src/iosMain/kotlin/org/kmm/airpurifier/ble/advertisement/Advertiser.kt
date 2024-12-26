package org.kmm.airpurifier.ble.advertisement

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Advertiser(private val server: IOSServerWrapper) {

    actual suspend fun advertise(settings: AdvertisementSettings) {
        server.value.advertise(settings)
    }

    actual suspend fun stop() {
        server.value.stopAdvertising()
    }
}
