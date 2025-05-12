package org.kmm.airpurifier.ble.client

import android.bluetooth.BluetoothDevice

internal sealed interface AndroidGattEvent

internal sealed interface CharacteristicEvent : AndroidGattEvent

internal data class OnGattCharacteristicWrite(
    val error: Error?
) : CharacteristicEvent

internal data class OnGattCharacteristicRead(
    val data: ByteArray?
) : CharacteristicEvent
