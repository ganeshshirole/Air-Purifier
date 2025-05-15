package org.kmm.airpurifier

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.kmm.airpurifier.data.model.BLEDevice
import org.kmm.airpurifier.data.repository.DeviceRepositoryImp
import kotlin.test.*

class DeviceRepositoryImpTest {

    // As there is currently no supported mock library for common code, I implemented a fake class instead.
    private lateinit var fakeDao: FakeBLEDeviceDao
    private lateinit var repository: DeviceRepositoryImp

    private val sampleDevice = BLEDevice(
        address = "00:11:22:33:44:55",
        name = "AirPurifierX",
        dateTime = 1,
        id = 1
    )

    @BeforeTest
    fun setup() {
        fakeDao = FakeBLEDeviceDao()
        repository = DeviceRepositoryImp(fakeDao)
    }

    @Test
    fun insertWithLimit_shouldAddDevice_andLimitSize() = runTest {
        repeat(6) {
            repository.insertWithLimit(BLEDevice(
                "00:00:00:00:00:0$it", "Device$it",
                dateTime = 1,
                id = 1
            ))
        }

        val allDevices = repository.getAllBLEDevice().first()
        assertEquals(5, allDevices.size)
        assertTrue(allDevices.none { it.address == "00:00:00:00:00:00" }) // first one removed
    }

    @Test
    fun deleteByAddress_shouldRemoveDevice() = runTest {
        repository.insertWithLimit(sampleDevice)
        repository.deleteByAddress(sampleDevice.address)

        val result = repository.getAllBLEDevice().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun updateDeviceNameByAddress_shouldUpdateNameCorrectly() = runTest {
        repository.insertWithLimit(sampleDevice)
        repository.updateDeviceNameByAddress(sampleDevice.address, "NewName")

        val updated = repository.getBLEDevice()
        assertEquals("NewName", updated?.name)
    }

    @Test
    fun getAllBLEDevice_shouldReturnCorrectList() = runTest {
        repository.insertWithLimit(sampleDevice)
        repository.insertWithLimit(BLEDevice(
            "AA:BB:CC:DD:EE", "Second",
            dateTime = 1,
            id = 1
        ))

        val allDevices = repository.getAllBLEDevice().first()
        assertEquals(2, allDevices.size)
    }

    @Test
    fun getBLEDevice_shouldReturnFirstDevice() = runTest {
        val device1 = BLEDevice(
            "A1", "First",
            dateTime = 1,
            id = 1
        )
        val device2 = BLEDevice(
            "A2", "Second",
            dateTime = 1,
            id = 1
        )

        repository.insertWithLimit(device1)
        repository.insertWithLimit(device2)

        val first = repository.getBLEDevice()
        assertEquals(device2, first) // because inserted at front
    }

    @Test
    fun count_shouldReturnCorrectSize() = runTest {
        assertEquals(0, repository.count())

        repository.insertWithLimit(sampleDevice)
        assertEquals(1, repository.count())

        repository.deleteByAddress(sampleDevice.address)
        assertEquals(0, repository.count())
    }
}
