package org.kmm.airpurifier.dependencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.kmm.airpurifier.ble.client.Client
import org.kmm.airpurifier.ble.client.ClientCharacteristic
import org.kmm.airpurifier.ble.client.ClientService
import org.kmm.airpurifier.database.BLEDevice
import org.kmm.airpurifier.database.BLEDeviceDao
import org.kmm.airpurifier.dependencies.DataParser.toDisplayString
import org.kmm.airpurifier.util.AirPurifierUUID.CHAR_UUID_AMBIENT_LIGHT
import org.kmm.airpurifier.util.AirPurifierUUID.CHAR_UUID_AQI
import org.kmm.airpurifier.util.AirPurifierUUID.CHAR_UUID_ECHO
import org.kmm.airpurifier.util.AirPurifierUUID.CHAR_UUID_ERROR
import org.kmm.airpurifier.util.AirPurifierUUID.CHAR_UUID_FILTER_LIFE
import org.kmm.airpurifier.util.AirPurifierUUID.CHAR_UUID_INDICATOR_LED
import org.kmm.airpurifier.util.AirPurifierUUID.CHAR_UUID_MOTOR_SPEED
import org.kmm.airpurifier.util.AirPurifierUUID.CHAR_UUID_POWER
import org.kmm.airpurifier.util.AirPurifierUUID.CHAR_UUID_UV_LIGHT
import org.kmm.airpurifier.util.AirPurifierUUID.SERVICE_UUID_AIR_PURIFIER

class HomeViewModel(private val client: Client, private val dao: BLEDeviceDao) : ViewModel() {

    private val _state = MutableStateFlow(ViewState())
    val state = _state.asStateFlow()

    //    private var _ambientLight: Int = 0
//    private var lastSpeed: Int = 0

    private lateinit var powerCharacteristic: ClientCharacteristic
    private lateinit var motorSpeedCharacteristic: ClientCharacteristic
    private lateinit var uvLightCharacteristic: ClientCharacteristic
    private lateinit var ambientLightCharacteristic: ClientCharacteristic
    private lateinit var indicatorLEDCharacteristic: ClientCharacteristic
    private lateinit var errorCharacteristic: ClientCharacteristic
    private lateinit var echoCharacteristic: ClientCharacteristic

    fun connectToDevice(devName: String = "", devAddress: String = "") {
        var name: String? = null
        var address: String? = null
        viewModelScope.launch {
            if (devAddress.isEmpty()) {
                val bleDevice = dao.getBLEDevice()
                if (bleDevice != null) {
                    name = bleDevice.name
                    address = bleDevice.address
                }
            } else {
                address = devAddress
                name = devName
            }

            if (address == null) return@launch
            try {
                client.connect(address!!, viewModelScope)
//            if (client.isConnected()) {
//                _state.value = _state.value.copy(isConnected = true)
//                dao.insertWithLimit(BLEDevice(name!!, address!!, timestamp))
//                val count = dao.count()
//                Napier.i("BLE DB Devices $count", tag = TAG)
//            } else {
//                _state.value = _state.value.copy(isConnected = false)
//                return@launch
//            }

                client.connectionStatus(viewModelScope) { isConnected ->
                    Napier.i("BLE Connection $isConnected", tag = TAG)
                    viewModelScope.launch {
                        _state.value = _state.value.copy(isConnected = isConnected)
                        if (isConnected) {
                            val timestamp =
                                Clock.System.now().toEpochMilliseconds()
                            dao.insertWithLimit(BLEDevice(name!!, address!!, timestamp))
                            val count = dao.count()
                            Napier.i("BLE DB Devices $count", tag = TAG)
                        }
                    }
                }

                val services = client.discoverServices()

                val service = services.findService(SERVICE_UUID_AIR_PURIFIER)!!

                getCharacteristic(service)
                setupAllNotifications()


                // Read only characteristic
                val filterLifeCharacteristic: ClientCharacteristic =
                    service.findCharacteristic(CHAR_UUID_FILTER_LIFE)!!
                val characteristicAQI = service.findCharacteristic(CHAR_UUID_AQI)!!

                filterLifeCharacteristic.getNotifications()
                    .onEach {
                        Napier.i("Filter Life $it", tag = TAG)
                        _state.value = _state.value.copy(filterLife = DataParser.byteArrayToInt(it))
                    }
                    .launchIn(viewModelScope)


                characteristicAQI.getNotifications()
                    .onEach {
                        Napier.i("AQI ${it.toDisplayString()}", tag = TAG)
                        _state.value = _state.value.copy(
                            aiq = DataParser.byteArrayToInt(it)
                        )
                    }
                    .launchIn(viewModelScope)

            } catch (e: Exception) {
                println("Caught an exception: ${e.message}")
            }
        }
    }

    private fun getCharacteristic(service: ClientService) {
        powerCharacteristic =
            service.findCharacteristic(CHAR_UUID_POWER)!!

        motorSpeedCharacteristic =
            service.findCharacteristic(CHAR_UUID_MOTOR_SPEED)!!

        uvLightCharacteristic =
            service.findCharacteristic(CHAR_UUID_UV_LIGHT)!!

        indicatorLEDCharacteristic =
            service.findCharacteristic(CHAR_UUID_INDICATOR_LED)!!

        ambientLightCharacteristic =
            service.findCharacteristic(CHAR_UUID_AMBIENT_LIGHT)!!

        errorCharacteristic =
            service.findCharacteristic(CHAR_UUID_ERROR)!!

        echoCharacteristic =
            service.findCharacteristic(CHAR_UUID_ECHO)!!
    }

    // Function to set up notifications for a characteristic
    private suspend fun setupNotifications(
        characteristic: ClientCharacteristic,
        updateAction: (Int) -> Unit
    ) {
        characteristic.getNotifications()
            .onEach {
                updateAction(DataParser.byteArrayToInt(it))
            }
            .launchIn(viewModelScope)
    }

    // Set up notifications for all characteristics
    private suspend fun setupAllNotifications() {
        setupNotifications(motorSpeedCharacteristic) { motorSpeed ->
//            echoValueBasedOnMotorSpeed(motorSpeed)
            _state.value = _state.value.copy(motorSpeed = motorSpeed)
            Napier.i("Motor Speed $motorSpeed", tag = TAG)
        }

        setupNotifications(powerCharacteristic) { power ->
            _state.value = _state.value.copy(power = power == 1)
            Napier.i("Power Status $power", tag = TAG)
        }

        setupNotifications(uvLightCharacteristic) { uv ->
            _state.value = _state.value.copy(uv = uv == 1)
            Napier.i("UV Light Status $uv", tag = TAG)
        }

        setupNotifications(indicatorLEDCharacteristic) { ledStatus ->
            _state.value = _state.value.copy(isLedOn = ledStatus == 1)
            Napier.i("Indicator LED Status $ledStatus", tag = TAG)
        }

        setupNotifications(ambientLightCharacteristic) { ambientLight ->
            _state.value = _state.value.copy(ambientLight = ambientLight)
            Napier.i("Ambient Light $ambientLight", tag = TAG)
        }

        setupNotifications(errorCharacteristic) { errorStatus ->
            _state.value = _state.value.copy(error = errorStatus == 1)
            Napier.i("Error Status $errorStatus", tag = TAG)
        }

        setupNotifications(echoCharacteristic) { echoStatus ->
            _state.value = _state.value.copy(echo = echoStatus == 1)
            Napier.i("Echo Status $echoStatus", tag = TAG)
        }
    }

    fun ambientLightValue(percent: Int) {
//        _ambientLight = percent
        _state.value = _state.value.copy(ambientLight = percent)
    }

    fun ambientLight() {
        viewModelScope.launch {
            try {
//                _state.value = _state.value.copy(ambientLight = _ambientLight)
                ambientLightCharacteristic.write(DataParser.intToByteArray(_state.value.ambientLight))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun power(power: Boolean) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(power = power)
                powerCharacteristic.write(DataParser.intToByteArray(if (_state.value.power) 1 else 0))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun uvLight(isOn: Boolean) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(uv = isOn)
                uvLightCharacteristic.write(DataParser.intToByteArray(if (isOn) 1 else 0))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun indicatorLED(isOn: Boolean) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLedOn = isOn)
                indicatorLEDCharacteristic.write(DataParser.intToByteArray(if (isOn) 1 else 0))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

//    private fun motorSpeedValue(speed: Int) {
//        _state.value = _state.value.copy(motorSpeed = speed)
//    }

    fun motorSpeed(speed: Int) {
        viewModelScope.launch {
            try {
//                echoValueBasedOnMotorSpeed(speed)
                _state.value = _state.value.copy(motorSpeed = speed)
                motorSpeedCharacteristic.write(DataParser.intToByteArray(speed))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    // This logic should implement in firmware
//    private fun echoValueBasedOnMotorSpeed(motorSpeed: Int) {
//        if (motorSpeed > 0) {
//            echoValue(false)
//        }
//        lastSpeed = motorSpeed
//    }

    // This logic should implement in firmware
//    private fun motorSpeedBasedOnEcho(isEchoOn: Boolean) {
//        if (isEchoOn) motorSpeedValue(0) else motorSpeedValue(lastSpeed)
//    }

//    private fun echoValue(isEchoOn: Boolean) {
//        _state.value = _state.value.copy(echo = isEchoOn)
//    }

    fun echo(isEchoOn: Boolean) {
        viewModelScope.launch {
            try {
//                motorSpeedBasedOnEcho(isEchoOn)
                _state.value = _state.value.copy(echo = isEchoOn)
                echoCharacteristic.write(DataParser.intToByteArray(if (isEchoOn) 1 else 0))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun errorState(hasError: Boolean) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(error = hasError)
                errorCharacteristic.write(DataParser.intToByteArray(if (hasError) 1 else 0))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            client.disconnect()
        }
        super.onCleared()
    }

    companion object {
        private const val TAG: String = "MyViewModel"
    }
}
