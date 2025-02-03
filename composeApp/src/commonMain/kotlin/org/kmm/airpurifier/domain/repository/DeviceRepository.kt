package org.kmm.airpurifier.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kmm.airpurifier.data.model.BLEDevice

interface DeviceRepository {
    suspend fun insertWithLimit(bleDevice: BLEDevice)
    suspend fun deleteByAddress(address: String)
    suspend fun updateDeviceNameByAddress(address: String, newName: String)
    fun getAllBLEDevice(): Flow<List<BLEDevice>>
    suspend fun getBLEDevice(): BLEDevice?
    suspend fun count(): Int
}