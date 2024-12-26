package org.kmm.airpurifier.ble.scanner

import platform.CoreBluetooth.CBCentral
import platform.CoreBluetooth.CBPeripheral

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual data class IoTDevice(internal val device: NativeDevice) {

    actual val name: String
        get() = device.name

    actual val address: String = device.address

    companion object {

        var counter: Int = 0
    }
}

sealed interface NativeDevice {

    val name: String

    val address: String

    companion object {

        var counter: Int = 0
    }
}

data class PeripheralDevice(internal val peripheral: CBPeripheral) : NativeDevice {

    override val name: String
        get() = peripheral.name ?: "Unknown"

    override val address: String
        get() = peripheral.identifier.UUIDString()

}

data class CentralDevice(internal val central: CBCentral) : NativeDevice {

    override val name: String
        get() = "No name"

    override val address: String
        get() = central.identifier.UUIDString()
}
