package org.kmm.airpurifier.dependencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.kmm.airpurifier.ble.scanner.Scanner
import org.kmm.airpurifier.database.BLEDeviceDao
import org.kmm.airpurifier.model.MyDevice

class ScannerViewModel(private val scanner: Scanner, private val dao: BLEDeviceDao) : ViewModel() {
    var selectedIoTDevice: MyDevice? = null

    private val _stateAllDevices = MutableStateFlow(emptyList<MyDevice>())
    val stateAllDevices = _stateAllDevices.asStateFlow()

    private var _stateSavedDevices = emptyList<MyDevice>()
    private var _stateScannedDevices = emptyList<MyDevice>()

    private fun updateCombinedDevices() {
        _stateScannedDevices = _stateScannedDevices.map { it.copy(title = null) }
        val distinctBy =
            (_stateSavedDevices + _stateScannedDevices).distinctBy { ioTDevice -> ioTDevice.address }
        if (_stateScannedDevices.isNotEmpty()) {
            distinctBy[_stateSavedDevices.size].title = "Searching Devices"
        }
        _stateAllDevices.value = distinctBy
    }

    fun scan() {
        dao.getAllBLEDevice().onEach { devices ->
            _stateSavedDevices = devices.mapIndexed { index, it ->
                MyDevice(
                    it.name,
                    it.address,
                    isSavedDevice = true,
                    title = if (index == 0) "Saved Devices" else null
                )
            }
            updateCombinedDevices()
        }.launchIn(viewModelScope)

        scanner.scan()
            .onEach { devices ->
                _stateScannedDevices =
                    (_stateScannedDevices + devices.map {
                        MyDevice(
                            it.name,
                            it.address
                        )
                    }).distinctBy { ioTDevice -> ioTDevice.address }

                updateCombinedDevices()
            }.launchIn(viewModelScope)
    }

    fun deleteDevice(address: String) {
        viewModelScope.launch {
            dao.deleteByAddress(address)
        }
    }

    fun renameDevice(address: String, newName: String) {
        viewModelScope.launch {
            dao.updateDeviceNameByAddress(address, newName)
        }
    }

    companion object {
        private const val TAG: String = "ScannerViewModel"
    }
}