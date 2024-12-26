package org.kmm.airpurifier.ble.scanner

import no.nordicsemi.android.kotlin.ble.core.BleDevice

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class IoTDevice(internal val device: BleDevice) {

    actual val name: String
        get() = device.name ?: ""

    actual val address: String
        get() = device.address

}
