package org.kmm.airpurifier

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.kmm.airpurifier.data.model.BLEDevice
import org.kmm.airpurifier.data.local.BLEDeviceDao

class FakeBLEDeviceDao : BLEDeviceDao {

    private val devices = mutableListOf<BLEDevice>()
    private val deviceFlow = MutableStateFlow<List<BLEDevice>>(emptyList())

    override suspend fun insertDevice(bleDevice: BLEDevice) {
        devices.add(bleDevice)
        deviceFlow.value = devices.toList()
    }

    override suspend fun deleteOldestDevice() {
        if (devices.isNotEmpty()) {
            devices.removeAt(0)
            deviceFlow.value = devices.toList()
        }
    }

    override suspend fun insertWithLimit(bleDevice: BLEDevice) {
        devices.removeAll { it.address == bleDevice.address } // prevent duplicates
        devices.add(0, bleDevice)
        if (devices.size > 5) devices.removeLast()
        deviceFlow.value = devices.toList()
    }

    override suspend fun deleteByAddress(deviceAddress: String) {
        devices.removeAll { it.address == deviceAddress }
        deviceFlow.value = devices.toList()
    }

    override suspend fun updateDeviceNameByAddress(address: String, newName: String) {
        val index = devices.indexOfFirst { it.address == address }
        if (index != -1) {
            devices[index] = devices[index].copy(name = newName)
            deviceFlow.value = devices.toList()
        }
    }

    override fun getAllBLEDevice() = deviceFlow.asStateFlow()

    override suspend fun getBLEDevice(): BLEDevice? = devices.firstOrNull()

    override suspend fun count(): Int = devices.size
}
