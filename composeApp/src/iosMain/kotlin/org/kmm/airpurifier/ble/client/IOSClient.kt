package org.kmm.airpurifier.ble.client

import io.github.aakira.napier.Napier
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kmm.airpurifier.ble.scanner.IoTDevice
import org.kmm.airpurifier.ble.scanner.PeripheralDevice
import platform.CoreBluetooth.CBAdvertisementDataServiceUUIDsKey
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBCentralManagerStatePoweredOn
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBDescriptor
import platform.CoreBluetooth.CBManagerState
import platform.CoreBluetooth.CBManagerStateUnknown
import platform.CoreBluetooth.CBPeripheral
import platform.CoreBluetooth.CBPeripheralDelegateProtocol
import platform.CoreBluetooth.CBPeripheralStateConnected
import platform.CoreBluetooth.CBService
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import platform.CoreBluetooth.CBUUID
import platform.Foundation.NSUUID

private const val TAG = "BLE-TAG"
//private val BLINKY_SERVICE_UUID = uuidFrom("00001523-1212-efde-1523-785feabcd123")

class IOSClient : NSObject(), CBCentralManagerDelegateProtocol, CBPeripheralDelegateProtocol {

    private lateinit var peripheral: CBPeripheral

    private val manager = CBCentralManager(this, null)

    private var onDeviceConnected: ((DeviceConnectionState) -> Unit)? = null
    private var onDeviceDisconnected: (() -> Unit)? = null
    private var onServicesDiscovered: ((OperationStatus) -> Unit)? = null

    private var services: ClientServices? = null

    private val _scannedDevices = MutableStateFlow<List<IoTDevice>>(emptyList())
    val scannedDevices = _scannedDevices.asStateFlow()

    private val _connectionStatus = MutableStateFlow<Boolean>(false)
    val connectionStatus = _connectionStatus.asStateFlow()

    private val _bleState = MutableStateFlow(CBManagerStateUnknown)
    val bleState: StateFlow<CBManagerState> = _bleState.asStateFlow()

    private fun onEvent(event: IOSGattEvent) {
        services?.onEvent(event)
    }

    fun scan(): Flow<List<IoTDevice>> {
        Napier.d { "Start Scam" }
        return callbackFlow {
            bleState.first { it == CBCentralManagerStatePoweredOn }
            manager.scanForPeripheralsWithServices(null, null)

            scannedDevices.onEach {
                trySend(it)
                it.forEach {
                    Napier.d { "Device Address: ${it.address}" }
                }
            }.launchIn(this)

            awaitClose {
                Napier.d { "Stop Scam" }
                manager.stopScan()
            }
        }
    }

    fun isConnected(): Boolean {
        return peripheral.state == CBPeripheralStateConnected
    }

    suspend fun connect(device: IoTDevice) {
        manager.stopScan()
        peripheral = (device.device as PeripheralDevice).peripheral
        peripheral.delegate = this
        Napier.i("Connect", tag = TAG)
        bleState.first { it == CBCentralManagerStatePoweredOn }
        return suspendCoroutine { continuation ->
            onDeviceConnected = {
                onDeviceConnected = null
                continuation.resume(Unit)
            }
            Napier.i("Connect peripheral", tag = TAG)
            manager.connectPeripheral(peripheral, null)
        }
    }

    suspend fun connect(uuid: String) {
        manager.stopScan()
        bleState.first { it == CBCentralManagerStatePoweredOn }
        if(manager.state == CBCentralManagerStatePoweredOn) {
            peripheral = manager.retrievePeripheralsWithIdentifiers(listOf(NSUUID(uuid)))
                .firstOrNull() as? CBPeripheral
                ?: throw IllegalStateException("Peripheral not found for UUID: $uuid")
            peripheral.delegate = this
            Napier.i("Connect", tag = TAG)
        } else {
            throw IllegalStateException("CBCentralManager State Powered No On: ${manager.state}")
        }

        return suspendCoroutine { continuation ->
            onDeviceConnected = {
                onDeviceConnected = null
                continuation.resume(Unit)
            }
            Napier.i("Connect peripheral", tag = TAG)
            manager.connectPeripheral(peripheral, null)
        }
    }

    suspend fun disconnect() {
        return suspendCoroutine { continuation ->
            onDeviceDisconnected = {
                onDeviceDisconnected = null
                continuation.resume(Unit)
            }
            manager.cancelPeripheralConnection(peripheral)
        }
    }

//    fun connectionStatus() : Flow<Boolean> {
//        return callbackFlow {
//            bleState.first { it == CBCentralManagerStatePoweredOn }
//
//            connectionStatus.onEach {
//                trySend(it)
//            }.launchIn(this)
//
//            awaitClose {
//                // nothing
//            }
//        }
//    }

    suspend fun discoverServices(): ClientServices {
        Napier.i("Discover services", tag = TAG)
        return suspendCoroutine { continuation ->
            onServicesDiscovered = {
                onServicesDiscovered = null
                val nativeServices = peripheral.services?.map { it as CBService } ?: emptyList()
                val clientServices = ClientServices(peripheral, nativeServices)
                services = clientServices
                continuation.resume(clientServices)
            }
            peripheral.discoverServices(null)
        }
    }

    override fun peripheral(peripheral: CBPeripheral, didDiscoverServices: NSError?) {
        Napier.i("Did iscover services", tag = TAG)
        peripheral.services?.map {
            it as CBService
        }?.onEach {
            Napier.i("Service uuid: ${it.UUID}", tag = TAG)
            peripheral.discoverCharacteristics(null, it)
        }
    }

    override fun peripheral(
        peripheral: CBPeripheral,
        didDiscoverCharacteristicsForService: CBService,
        error: NSError?,
    ) {
        Napier.i("Discover characteristic", tag = TAG)
        peripheral.services
            ?.map { it as CBService }
            ?.map { it.characteristics }
            ?.mapNotNull { it?.map { it as CBCharacteristic } }
            ?.onEach {
                it.forEach {
                    Napier.i("Characteristic uuid: ${it.UUID}", tag = TAG)
                    peripheral.discoverDescriptorsForCharacteristic(it)
                }
            }
        Napier.i("Discover characteristic: $", tag = TAG)
    }

    @ObjCSignatureOverride
    override fun peripheral(
        peripheral: CBPeripheral,
        didDiscoverDescriptorsForCharacteristic: CBCharacteristic,
        error: NSError?,
    ) {
        Napier.i("Discover descriptors", tag = TAG)
        onServicesDiscovered?.invoke(getOperationStatus(error))
        //todo aggregate responses
    }

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        Napier.i("New state: ${central.state}", tag = TAG)
        _bleState.value = central.state
    }

    @ObjCSignatureOverride
    override fun centralManager(
        central: CBCentralManager,
        didFailToConnectPeripheral: CBPeripheral,
        error: NSError?,
    ) {
        Napier.i("didFailToConnectPeripheral", tag = TAG)
        _connectionStatus.value = false
        onDeviceConnected?.invoke(DeviceDisconnected)
    }

    override fun centralManager(central: CBCentralManager, didConnectPeripheral: CBPeripheral) {
        Napier.i("didConnectPeripheral", tag = TAG)
        _connectionStatus.value = true
        onDeviceConnected?.invoke(DeviceConnected)
    }

    @ObjCSignatureOverride
    override fun centralManager(
        central: CBCentralManager,
        didDisconnectPeripheral: CBPeripheral,
        error: NSError?,
    ) {
        Napier.i("didDisconnectPeripheral", tag = TAG)
        _connectionStatus.value = false
        onDeviceDisconnected?.invoke()
    }

    @ObjCSignatureOverride
    override fun peripheral(
        peripheral: CBPeripheral,
        didUpdateValueForCharacteristic: CBCharacteristic,
        error: NSError?,
    ) {
        onEvent(
            OnGattCharacteristicRead(
                peripheral,
                didUpdateValueForCharacteristic.value,
                error
            )
        )
    }

    @ObjCSignatureOverride
    override fun peripheral(
        peripheral: CBPeripheral,
        didWriteValueForCharacteristic: CBCharacteristic,
        error: NSError?,
    ) {
        onEvent(
            OnGattCharacteristicWrite(
                peripheral,
                didWriteValueForCharacteristic.value,
                error
            )
        )
    }

    @ObjCSignatureOverride
    override fun peripheral(
        peripheral: CBPeripheral,
        didWriteValueForDescriptor: CBDescriptor,
        error: NSError?,
    ) {
        onEvent(
            OnGattDescriptorWrite(
                peripheral,
                didWriteValueForDescriptor.value as NSData?,
                error
            )
        )
    }

    @ObjCSignatureOverride
    override fun peripheral(
        peripheral: CBPeripheral,
        didUpdateValueForDescriptor: CBDescriptor,
        error: NSError?,
    ) {
        onEvent(
            OnGattDescriptorRead(
                peripheral,
                didUpdateValueForDescriptor.value as NSData?,
                error
            )
        )
    }

    override fun centralManager(
        central: CBCentralManager,
        didDiscoverPeripheral: CBPeripheral,
        advertisementData: Map<Any?, *>,
        RSSI: NSNumber,
    ) {
        val ioTDevice = IoTDevice(PeripheralDevice(didDiscoverPeripheral))
        Napier.d { "${advertisementData[CBAdvertisementDataServiceUUIDsKey]}" }
//        val uuid = try {
//            (advertisementData[CBAdvertisementDataServiceUUIDsKey] as? List<CBUUID>)?.first()
//                ?.toUuid()
//        } catch (e: Exception) {
//            null
//        }
//        Napier.d { "Uuid: $uuid" }
//        if (uuid != BLINKY_SERVICE_UUID) {
//            return
//        }
        _scannedDevices.value += ioTDevice
    }

    private fun getOperationStatus(error: NSError?): OperationStatus {
        return error?.let { OperationError } ?: OperationSuccess
    }
}
