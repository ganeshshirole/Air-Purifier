package org.kmm.airpurifier.ble.server

import no.nordicsemi.android.kotlin.ble.core.data.BleGattPermission
import no.nordicsemi.android.kotlin.ble.core.data.BleGattProperty

internal fun List<BleGattPermission>.toDomainPermissions(): List<GattPermission> {
    return this.mapNotNull {
        when (it) {
            BleGattPermission.PERMISSION_READ -> GattPermission.READ
            BleGattPermission.PERMISSION_WRITE -> GattPermission.WRITE
            BleGattPermission.PERMISSION_READ_ENCRYPTED,
            BleGattPermission.PERMISSION_READ_ENCRYPTED_MITM,
            BleGattPermission.PERMISSION_WRITE_ENCRYPTED,
            BleGattPermission.PERMISSION_WRITE_ENCRYPTED_MITM,
            BleGattPermission.PERMISSION_WRITE_SIGNED,
            BleGattPermission.PERMISSION_WRITE_SIGNED_MITM -> null
        }
    }
}

internal fun List<BleGattProperty>.toDomainProperties(): List<GattProperty> {
    return this.mapNotNull {
        when (it) {
            BleGattProperty.PROPERTY_READ -> GattProperty.READ
            BleGattProperty.PROPERTY_WRITE -> GattProperty.WRITE
            BleGattProperty.PROPERTY_INDICATE -> GattProperty.INDICATE
            BleGattProperty.PROPERTY_NOTIFY -> GattProperty.NOTIFY
            BleGattProperty.PROPERTY_BROADCAST,
            BleGattProperty.PROPERTY_EXTENDED_PROPS,
            BleGattProperty.PROPERTY_SIGNED_WRITE,
            BleGattProperty.PROPERTY_WRITE_NO_RESPONSE -> null
        }
    }
}

internal fun List<GattPermission>.toNativePermissions(): List<BleGattPermission> {
    return this.map {
        when (it) {
            GattPermission.READ -> BleGattPermission.PERMISSION_READ
            GattPermission.WRITE -> BleGattPermission.PERMISSION_WRITE
        }
    }
}

internal fun List<GattProperty>.toNativeProperties(): List<BleGattProperty> {
    return this.map {
        when (it) {
            GattProperty.READ -> BleGattProperty.PROPERTY_READ
            GattProperty.WRITE -> BleGattProperty.PROPERTY_WRITE
            GattProperty.NOTIFY -> BleGattProperty.PROPERTY_NOTIFY
            GattProperty.INDICATE -> BleGattProperty.PROPERTY_INDICATE
        }
    }
}
