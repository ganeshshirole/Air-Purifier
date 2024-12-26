package org.kmm.airpurifier.ble.scanner

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class IoTDevice {
    val name: String
    val address: String
}
