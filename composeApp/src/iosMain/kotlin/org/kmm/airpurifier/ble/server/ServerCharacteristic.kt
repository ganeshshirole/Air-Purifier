package org.kmm.airpurifier.ble.server

import com.benasher44.uuid.Uuid
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.kmm.airpurifier.ble.client.toByteArray
import org.kmm.airpurifier.ble.client.toNSData
import org.kmm.airpurifier.ble.client.toUuid
import platform.CoreBluetooth.CBATTErrorSuccess
import platform.CoreBluetooth.CBATTRequest
import platform.CoreBluetooth.CBDescriptor
import platform.CoreBluetooth.CBMutableCharacteristic
import platform.CoreBluetooth.CBPeripheralManager

private const val TAG = "BLE-TAG"

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ServerCharacteristic(
    private val native: CBMutableCharacteristic,
    private val manager: CBPeripheralManager,
    private val notificationsRecords: NotificationsRecords,
) {
    actual val uuid: Uuid = native.UUID.toUuid()

    actual val properties: List<GattProperty> = native.properties.toProperties()

    actual val permissions: List<GattPermission> = emptyList()

    actual val descriptors: List<ServerDescriptor> = native.descriptors
        ?.map { it as CBDescriptor }
        ?.map { ServerDescriptor(it) }
        ?: emptyList()

    private val _value = MutableStateFlow(byteArrayOf())
    actual val value: Flow<ByteArray> = _value

    fun onEvent(event: ServerRequest) {
        when (event) {
            is ReadRequest -> handleReadRequest(event.request)
            is WriteRequest -> handleWriteRequest(event.request)
        }
    }

    private fun handleReadRequest(request: CBATTRequest) {
        if (request.characteristic().UUID != native.UUID) {
            return
        }
        Napier.i("handleReadRequest ${native.UUID}", tag = TAG)
        val dataToSend = _value.value.copyOfRange(
            _value.value.size - request.offset.toInt(),
            _value.value.size
        )

        request.value = dataToSend.toNSData()
        manager.respondToRequest(request, CBATTErrorSuccess)
    }

    private fun handleWriteRequest(requests: List<CBATTRequest>) {
        Napier.i("handleWriteRequest 1: ${native.UUID}", tag = TAG)

        requests.onEach {
            Napier.i("handleWriteRequest 2: ${it.characteristic().UUID}", tag = TAG)
        }
        requests.filter { it.characteristic().UUID == native.UUID }.forEach {
            Napier.i("handleWriteRequest 3: ${it.characteristic().UUID}", tag = TAG)
            it.value?.toByteArray()?.let {
                sendUpdatedValue(it)
            }
            manager.respondToRequest(it, CBATTErrorSuccess)
        }
    }

    actual suspend fun setValue(value: ByteArray) {
        sendUpdatedValue(value)
    }

    private fun sendUpdatedValue(value: ByteArray) {
        Napier.i("set value", tag = TAG)
        _value.value = value
        val centralsToUpdate = notificationsRecords.getCentrals(native.UUID.toUuid())

        val newValue = value.toNSData()

        manager.updateValue(newValue, native, centralsToUpdate)
    }
}
