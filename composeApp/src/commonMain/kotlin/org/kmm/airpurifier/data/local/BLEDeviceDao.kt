package org.kmm.airpurifier.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.kmm.airpurifier.data.model.BLEDevice

@Dao
interface BLEDeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(bleDevice: BLEDevice)

    @Query("DELETE FROM BLEDevice WHERE dateTime = (SELECT MIN(dateTime) FROM BLEDevice)")
    suspend fun deleteOldestDevice()

    @Transaction
    suspend fun insertWithLimit(bleDevice: BLEDevice) {
        if (count() >= 5) {
            deleteOldestDevice()
        }
        insertDevice(bleDevice)
    }

    @Query("UPDATE bledevice SET name = :newName WHERE address = :address")
    suspend fun updateDeviceNameByAddress(address: String, newName: String)

    @Query("DELETE FROM bledevice WHERE address = :deviceAddress")
    suspend fun deleteByAddress(deviceAddress: String)

    @Query("SELECT * FROM bledevice ORDER BY dateTime DESC")
    fun getAllBLEDevice(): Flow<List<BLEDevice>>

    @Query("SELECT * FROM bledevice ORDER BY dateTime DESC LIMIT 1")
    suspend fun getBLEDevice(): BLEDevice?

    @Query("SELECT count(*) FROM bledevice")
    suspend fun count(): Int

//    @Upsert
//    suspend fun upsert(bleDevice: BLEDevice)

//    @Delete
//    suspend fun delete(bleDevice: BLEDevice)

//    @Query("SELECT * FROM BLEDevice WHERE address = :deviceAddress LIMIT 1")
//    suspend fun getBLEDeviceByAddress(deviceAddress: String): BLEDevice?
}