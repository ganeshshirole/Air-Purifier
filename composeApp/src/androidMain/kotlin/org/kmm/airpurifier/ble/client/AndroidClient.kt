package org.kmm.airpurifier.ble.client

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidClient(private val context: Context) {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private val bleScanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private var bluetoothGatt: BluetoothGatt? = null
    private val _connectionState = MutableStateFlow(false) // Holds connection state
    private val connectionState: StateFlow<Boolean> = _connectionState.asStateFlow()
    private var discoverServicesContinuation: Continuation<ClientServices>? = null

    private var services: ClientServices? = null

    private fun onEvent(event: AndroidGattEvent) {
        services?.onEvent(event)
    }

    @SuppressLint("MissingPermission")
    fun scan(serviceUuid: ParcelUuid? = null): Flow<BluetoothDevice> = callbackFlow {
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                trySend(result.device)
            }

            override fun onScanFailed(errorCode: Int) {
                close(Exception("Scan failed with error: $errorCode"))
            }
        }

        val scanFilters = mutableListOf<ScanFilter>().apply {
            serviceUuid?.let {
                add(ScanFilter.Builder().setServiceUuid(it).build())
            }
        }

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        bleScanner.startScan(scanFilters, scanSettings, scanCallback)

        awaitClose {
            bleScanner.stopScan(scanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String): Flow<Boolean> = callbackFlow {
        val device = bluetoothAdapter.getRemoteDevice(address)
        bluetoothGatt = device.connectGatt(context, false, gattCallback)

        val job = launch {
            connectionState.collect { trySend(it) }
        }

        awaitClose {
            job.cancel()
            disconnect()
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    @SuppressLint("MissingPermission")
    suspend fun discoverServices(): ClientServices {
        return suspendCoroutine { continuation ->
            discoverServicesContinuation = continuation
            bluetoothGatt?.discoverServices()
        }
    }

    @SuppressLint("MissingPermission")
    fun isConnected() = connectionState.value

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> _connectionState.value = true
                BluetoothProfile.STATE_DISCONNECTED -> _connectionState.value = false
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            discoverServicesContinuation?.let {
                val nativeServices = bluetoothGatt?.services ?: emptyList()
                services = ClientServices(bluetoothGatt!!, nativeServices)

                it.resume(ClientServices(bluetoothGatt!!, gatt?.services ?: listOf()))
                discoverServicesContinuation = null
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            onEvent(
                OnGattCharacteristicRead(
                    value
                )
            )
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            onEvent(
                OnGattCharacteristicWrite(
                    null
                )
            )
        }
    }
}