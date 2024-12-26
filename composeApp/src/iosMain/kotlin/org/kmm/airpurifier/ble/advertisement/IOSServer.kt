package org.kmm.airpurifier.ble.advertisement

import io.github.aakira.napier.Napier
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kmm.airpurifier.ble.client.toCBUUID
import org.kmm.airpurifier.ble.client.toUuid
import org.kmm.airpurifier.ble.scanner.CentralDevice
import org.kmm.airpurifier.ble.scanner.IoTDevice
import org.kmm.airpurifier.ble.server.BleServerServiceConfig
import org.kmm.airpurifier.ble.server.GattPermission
import org.kmm.airpurifier.ble.server.GattProperty
import org.kmm.airpurifier.ble.server.NotificationsRecords
import org.kmm.airpurifier.ble.server.ReadRequest
import org.kmm.airpurifier.ble.server.ServerProfile
import org.kmm.airpurifier.ble.server.WriteRequest
import platform.CoreBluetooth.CBATTRequest
import platform.CoreBluetooth.CBAdvertisementDataLocalNameKey
import platform.CoreBluetooth.CBAdvertisementDataServiceUUIDsKey
import platform.CoreBluetooth.CBAttributePermissions
import platform.CoreBluetooth.CBAttributePermissionsReadable
import platform.CoreBluetooth.CBAttributePermissionsWriteable
import platform.CoreBluetooth.CBCentral
import platform.CoreBluetooth.CBCentralManagerStatePoweredOn
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBCharacteristicProperties
import platform.CoreBluetooth.CBCharacteristicPropertyIndicate
import platform.CoreBluetooth.CBCharacteristicPropertyNotify
import platform.CoreBluetooth.CBCharacteristicPropertyRead
import platform.CoreBluetooth.CBCharacteristicPropertyWrite
import platform.CoreBluetooth.CBMutableCharacteristic
import platform.CoreBluetooth.CBMutableDescriptor
import platform.CoreBluetooth.CBMutableService
import platform.CoreBluetooth.CBPeripheralManager
import platform.CoreBluetooth.CBPeripheralManagerDelegateProtocol
import platform.CoreBluetooth.CBPeripheralManagerState
import platform.CoreBluetooth.CBPeripheralManagerStateUnknown
import platform.CoreBluetooth.CBService
import platform.Foundation.NSError
import platform.darwin.NSObject

private const val TAG = "BLE-TAG"

class IOSServer(
    private val notificationsRecords: NotificationsRecords,
) : NSObject(), CBPeripheralManagerDelegateProtocol {
    private val manager = CBPeripheralManager(this, null)

    private val _bleState = MutableStateFlow(CBPeripheralManagerStateUnknown)
    val bleState: StateFlow<CBPeripheralManagerState> = _bleState.asStateFlow()

    private val _connections = MutableStateFlow<Map<CBCentral, ServerProfile>>(emptyMap())
    val connections: Flow<Map<IoTDevice, ServerProfile>> = _connections.map {
        it.mapKeys { IoTDevice(CentralDevice(it.key)) }
    }

    private var services = listOf<CBService>()

    override fun peripheralManagerDidUpdateState(peripheral: CBPeripheralManager) {
        _bleState.value = peripheral.state
    }

    override fun peripheralManagerDidStartAdvertising(
        peripheral: CBPeripheralManager,
        error: NSError?
    ) {

    }

    override fun peripheralManagerIsReadyToUpdateSubscribers(peripheral: CBPeripheralManager) {
        Napier.i("Update subscribers", tag = TAG)
    }

    override fun peripheralManager(
        peripheral: CBPeripheralManager,
        didReceiveReadRequest: CBATTRequest
    ) {
        Napier.i("Receive read request", tag = TAG)
        val central = didReceiveReadRequest.central
        val profile = getProfile(central)

        profile.onEvent(ReadRequest(didReceiveReadRequest))
    }

    override fun peripheralManager(
        peripheral: CBPeripheralManager,
        didReceiveWriteRequests: List<*>
    ) {
        Napier.i("Receive write request", tag = TAG)
        try {

            val requests = didReceiveWriteRequests.map { it as CBATTRequest }
            Napier.i("Requests: $requests", tag = TAG)
            val central = requests.first().central
            Napier.i("Central: $central", tag = TAG)
            val profile = getProfile(central)
            Napier.i("Profile: $profile", tag = TAG)
            profile.onEvent(WriteRequest(requests))
        } catch (t: Throwable) {
            Napier.i("Receive write request", tag = TAG, throwable = t)
        }

    }

    @ObjCSignatureOverride
    override fun peripheralManager(
        peripheral: CBPeripheralManager,
        central: CBCentral,
        didSubscribeToCharacteristic: CBCharacteristic
    ) {
        Napier.i("Subscribe to characteristic", tag = TAG)
        notificationsRecords.addCentral(didSubscribeToCharacteristic.UUID.toUuid(), central)
    }

    @ObjCSignatureOverride
    override fun peripheralManager(
        peripheral: CBPeripheralManager,
        central: CBCentral,
        didUnsubscribeFromCharacteristic: CBCharacteristic
    ) {
        Napier.i("Unsubscribe from characteristic", tag = TAG)
        notificationsRecords.removeCentral(didUnsubscribeFromCharacteristic.UUID.toUuid(), central)
    }

    override fun peripheralManager(
        peripheral: CBPeripheralManager,
        didAddService: CBService,
        error: NSError?
    ) {
        Napier.i("Add service", tag = TAG)
        services = services + didAddService
    }

    suspend fun advertise(settings: AdvertisementSettings) {
        bleState.first { it == CBCentralManagerStatePoweredOn }
        val map: Map<Any?, Any> = mapOf(
            CBAdvertisementDataLocalNameKey to settings.name,
            CBAdvertisementDataServiceUUIDsKey to listOf(settings.uuid.toCBUUID()) //https://kotlinlang.org/docs/native-objc-interop.html#mappings
        )
        manager.startAdvertising(map)
    }

    suspend fun startServer(services: List<BleServerServiceConfig>) {
        bleState.first { it == CBCentralManagerStatePoweredOn }
        val iosServices = services.map {
            val characteristics = it.characteristics.map {
                val descriptors = it.descriptors.map {
                    CBMutableDescriptor(it.uuid.toCBUUID(), null)
                }
                CBMutableCharacteristic(
                    it.uuid.toCBUUID(),
                    it.properties.toDomain(),
                    null,
                    it.permissions.toDomain()
                ).also {
                    it.setDescriptors(descriptors)
                }
            }

            CBMutableService(it.uuid.toCBUUID(), true).also {
                it.setCharacteristics(characteristics)
            }
        }

        iosServices.forEach {
            manager.addService(it)
        }
    }

    suspend fun stopAdvertising() {
        manager.stopAdvertising()
    }

    private fun getProfile(central: CBCentral): ServerProfile {
        return _connections.value.getOrElse(central) {
            val profile = ServerProfile(services, manager, notificationsRecords)
            _connections.value = _connections.value + (central to profile)
            profile
        }
    }

    private fun List<GattPermission>.toDomain(): CBAttributePermissions {
        return this.map {
            when (it) {
                GattPermission.READ -> CBAttributePermissionsReadable
                GattPermission.WRITE -> CBAttributePermissionsWriteable
            }
        }.reduce { acc, permission -> acc or permission }
    }

    private fun List<GattProperty>.toDomain(): CBCharacteristicProperties {
        return this.map {
            when (it) {
                GattProperty.READ -> CBCharacteristicPropertyRead
                GattProperty.WRITE -> CBCharacteristicPropertyWrite
                GattProperty.NOTIFY -> CBCharacteristicPropertyNotify
                GattProperty.INDICATE -> CBCharacteristicPropertyIndicate
            }
        }.reduce { acc, permission -> acc or permission }
    }
}
