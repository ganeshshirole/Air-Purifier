package org.kmm.airpurifier.data.repository

import kotlinx.coroutines.flow.Flow
import org.kmm.airpurifier.data.local.BLEDeviceDao
import org.kmm.airpurifier.data.model.BLEDevice
import org.kmm.airpurifier.domain.repository.DeviceRepository

class DeviceRepositoryImp(private val bleDeviceDao: BLEDeviceDao): DeviceRepository {
    override suspend fun insertWithLimit(bleDevice: BLEDevice) {
        bleDeviceDao.insertWithLimit(bleDevice)
    }

    override suspend fun deleteByAddress(address: String) {
        bleDeviceDao.deleteByAddress(address)
    }

    override suspend fun updateDeviceNameByAddress(address: String, newName: String) {
        bleDeviceDao.updateDeviceNameByAddress(address, newName)
    }

    override fun getAllBLEDevice(): Flow<List<BLEDevice>> {
        return bleDeviceDao.getAllBLEDevice()
    }

    override suspend fun getBLEDevice(): BLEDevice? {
        return bleDeviceDao.getBLEDevice()
    }

    override suspend fun count(): Int {
        return bleDeviceDao.count()
    }

}