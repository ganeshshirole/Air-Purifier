package org.kmm.airpurifier.ble.client

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Build
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientCharacteristic(
    private val bluetoothGatt: BluetoothGatt,
    private val characteristic: BluetoothGattCharacteristic
) {

    val uuid: Uuid = characteristic.uuid

    private var onCharacteristicWrite: ((OnGattCharacteristicWrite) -> Unit)? = null
    private var onCharacteristicRead: ((OnGattCharacteristicRead) -> Unit)? = null


    internal fun onEvent(event: CharacteristicEvent) {
        when (event) {
            is OnGattCharacteristicRead -> onCharacteristicRead?.invoke(event)
            is OnGattCharacteristicWrite -> onCharacteristicWrite?.invoke(event)
        }
    }

    actual suspend fun getNotifications(): Flow<ByteArray> {
        return callbackFlow {
            bluetoothGatt.setCharacteristicNotification(characteristic, true)

            onCharacteristicRead = {
                trySend(it.data ?: byteArrayOf())
            }

            awaitClose {
                bluetoothGatt.setCharacteristicNotification(characteristic, false)
            }
        }
    }

    actual suspend fun write(value: ByteArray, writeType: WriteType) {
        val androidWriteType = when (writeType) {
            WriteType.DEFAULT -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            WriteType.NO_RESPONSE -> BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        }
        return suspendCoroutine { continuation ->
            onCharacteristicWrite = {
                onCharacteristicWrite = null
                continuation.resume(Unit)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bluetoothGatt.writeCharacteristic(characteristic, value, androidWriteType)
            } else {
                characteristic.value = value
                characteristic.writeType = androidWriteType
                bluetoothGatt.writeCharacteristic(characteristic)
            }
        }
    }

    actual suspend fun read(): ByteArray {
        return suspendCoroutine { continuation ->
            onCharacteristicRead = {
                onCharacteristicRead = null
                continuation.resume(it.data ?: byteArrayOf())
            }
            bluetoothGatt.readCharacteristic(characteristic)
        }
    }
}