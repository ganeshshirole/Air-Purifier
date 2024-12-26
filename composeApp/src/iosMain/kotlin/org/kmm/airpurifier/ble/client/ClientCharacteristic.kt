package org.kmm.airpurifier.ble.client

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBCharacteristicWriteWithResponse
import platform.CoreBluetooth.CBCharacteristicWriteWithoutResponse
import platform.CoreBluetooth.CBDescriptor
import platform.CoreBluetooth.CBPeripheral
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClientCharacteristic(
    private val peripheral: CBPeripheral,
    private val native: CBCharacteristic,
) {

    val uuid: Uuid = native.UUID.toUuid()

    private var onCharacteristicWrite: ((OnGattCharacteristicWrite) -> Unit)? = null
    private var onCharacteristicRead: ((OnGattCharacteristicRead) -> Unit)? = null

    private val descriptors = native.descriptors
        ?.map { it as CBDescriptor }
        ?.map { ClientDescriptor(peripheral, it) }
        ?: emptyList()

    internal fun onEvent(event: CharacteristicEvent) {
        when (event) {
            is OnGattCharacteristicRead -> onCharacteristicRead?.invoke(event)
            is OnGattCharacteristicWrite -> onCharacteristicWrite?.invoke(event)
            is DescriptorEvent -> descriptors.onEach { it.onEvent(event) }
        }
    }

    actual fun findDescriptor(uuid: Uuid): ClientDescriptor? {
        return descriptors.firstOrNull { it.uuid == uuid }
    }

    actual suspend fun getNotifications(): Flow<ByteArray> {
        return callbackFlow {
            peripheral.setNotifyValue(true, native)

            onCharacteristicRead = {
                trySend(it.data?.toByteArray() ?: byteArrayOf())
            }

            awaitClose {
                peripheral.setNotifyValue(false, native)
            }
        }
    }

    actual suspend fun write(value: ByteArray, writeType: WriteType) {
        val iosWriteType = when (writeType) {
            WriteType.DEFAULT -> CBCharacteristicWriteWithResponse
            WriteType.NO_RESPONSE -> CBCharacteristicWriteWithoutResponse
        }
        return suspendCoroutine { continuation ->
            onCharacteristicWrite = {
                onCharacteristicWrite = null
                continuation.resume(Unit)
            }
            peripheral.writeValue(value.toNSData(), native, iosWriteType)
        }
    }

    actual suspend fun read(): ByteArray {
        return suspendCoroutine { continuation ->
            onCharacteristicRead = {
                onCharacteristicRead = null
                continuation.resume(it.data?.toByteArray() ?: byteArrayOf())
            }
            peripheral.readValueForCharacteristic(native)
        }
    }
}
