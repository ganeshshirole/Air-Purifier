package org.kmm.airpurifier.ble.server

import platform.CoreBluetooth.CBAttributePermissions
import platform.CoreBluetooth.CBAttributePermissionsReadable
import platform.CoreBluetooth.CBAttributePermissionsWriteable
import platform.CoreBluetooth.CBCharacteristicProperties
import platform.CoreBluetooth.CBCharacteristicPropertyIndicate
import platform.CoreBluetooth.CBCharacteristicPropertyNotify
import platform.CoreBluetooth.CBCharacteristicPropertyRead
import platform.CoreBluetooth.CBCharacteristicPropertyWrite

fun CBCharacteristicProperties.toProperties(): List<GattProperty> {
    val result = mutableListOf<GattProperty>()

    if (this and CBCharacteristicPropertyRead > 0u) {
        result.add(GattProperty.READ)
    }
    if (this and CBCharacteristicPropertyWrite > 0u) {
        result.add(GattProperty.WRITE)
    }
    if (this and CBCharacteristicPropertyNotify > 0u) {
        result.add(GattProperty.NOTIFY)
    }
    if (this and CBCharacteristicPropertyIndicate > 0u) {
        result.add(GattProperty.INDICATE)
    }

    return result.toList()
}

fun CBAttributePermissions.toPermissions(): List<GattPermission> {
    val result = mutableListOf<GattPermission>()

    if (this and CBAttributePermissionsWriteable > 0u) {
        result.add(GattPermission.WRITE)
    }
    if (this and CBAttributePermissionsReadable > 0u) {
        result.add(GattPermission.READ)
    }

    return result.toList()
}
