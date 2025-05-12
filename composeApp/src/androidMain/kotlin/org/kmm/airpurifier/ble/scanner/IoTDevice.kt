package org.kmm.airpurifier.ble.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class IoTDevice(private val device: BluetoothDevice) {
    actual val name: String
        @SuppressLint("MissingPermission")
        get() = device.name ?: ""

    actual val address: String
        get() = device.address
}
