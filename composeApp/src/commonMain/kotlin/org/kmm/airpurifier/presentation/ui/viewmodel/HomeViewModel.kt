package org.kmm.airpurifier.presentation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.kmm.airpurifier.ble.client.Client
import org.kmm.airpurifier.ble.client.ClientCharacteristic
import org.kmm.airpurifier.ble.client.ClientService
import org.kmm.airpurifier.data.model.BLEDevice
import org.kmm.airpurifier.domain.DataParser
import org.kmm.airpurifier.domain.DataParser.toDisplayString
import org.kmm.airpurifier.domain.repository.DeviceRepository
import org.kmm.airpurifier.presentation.intent.HomeScreenIntent
import org.kmm.airpurifier.presentation.state.HomeScreenState
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

class HomeViewModel(private val client: Client, private val deviceRepository: DeviceRepository) : ViewModel() {

    var state by mutableStateOf(HomeScreenState())
        private set

    private lateinit var powerCharacteristic: ClientCharacteristic
    private lateinit var motorSpeedCharacteristic: ClientCharacteristic
    private lateinit var uvLightCharacteristic: ClientCharacteristic
    private lateinit var ambientLightCharacteristic: ClientCharacteristic
    private lateinit var indicatorLEDCharacteristic: ClientCharacteristic
    private lateinit var errorCharacteristic: ClientCharacteristic
    private lateinit var echoCharacteristic: ClientCharacteristic

    fun handleIntent(intent: HomeScreenIntent) {
        when (intent) {
            is HomeScreenIntent.CONNECT -> connectToDevice()
            is HomeScreenIntent.AMBIENTLIGHT -> ambientLight()
            is HomeScreenIntent.AmbientLightValue -> ambientLightValue(intent.lightValue)
            is HomeScreenIntent.Echo -> echo(intent.isOn)
            is HomeScreenIntent.IndicatorLed -> indicatorLED(intent.isOn)
            is HomeScreenIntent.MotorSpeed -> motorSpeed(intent.speed)
            is HomeScreenIntent.Power -> power(intent.isOn)
            is HomeScreenIntent.UVLight -> uvLight(intent.isOn)
            is HomeScreenIntent.ShowDialog -> showDialog(intent.isShow)
        }
    }

    private fun showDialog(show: Boolean) {
        state = state.copy(showDialog = show)
    }

    private fun connectToDevice(devName: String = "", devAddress: String = "") {
        var name: String? = null
        var address: String? = null
        viewModelScope.launch {
            if (devAddress.isEmpty()) {
                val bleDevice = deviceRepository.getBLEDevice()
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
                client.connectionStatus(viewModelScope) { isConnected ->
                    Napier.i("BLE Connection $isConnected", tag = TAG)
                    viewModelScope.launch {
                        state = state.copy(isConnected = isConnected)
                        if (isConnected) {
                            val timestamp =
                                Clock.System.now().toEpochMilliseconds()
                            deviceRepository.insertWithLimit(BLEDevice(name!!, address!!, timestamp))
                            val count = deviceRepository.count()
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
                        state = state.copy(filterLife = DataParser.byteArrayToInt(it))
                    }
                    .launchIn(viewModelScope)


                characteristicAQI.getNotifications()
                    .onEach {
                        Napier.i("AQI ${it.toDisplayString()}", tag = TAG)
                        state = state.copy(
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
            state = state.copy(motorSpeed = motorSpeed)
            Napier.i("Motor Speed $motorSpeed", tag = TAG)
        }

        setupNotifications(powerCharacteristic) { power ->
            state = state.copy(power = power == 1)
            Napier.i("Power Status $power", tag = TAG)
        }

        setupNotifications(uvLightCharacteristic) { uv ->
            state = state.copy(uv = uv == 1)
            Napier.i("UV Light Status $uv", tag = TAG)
        }

        setupNotifications(indicatorLEDCharacteristic) { ledStatus ->
            state = state.copy(isLedOn = ledStatus == 1)
            Napier.i("Indicator LED Status $ledStatus", tag = TAG)
        }

        setupNotifications(ambientLightCharacteristic) { ambientLight ->
            state = state.copy(ambientLight = ambientLight)
            Napier.i("Ambient Light $ambientLight", tag = TAG)
        }

        setupNotifications(errorCharacteristic) { errorStatus ->
            state = state.copy(error = errorStatus == 1)
            Napier.i("Error Status $errorStatus", tag = TAG)
        }

        setupNotifications(echoCharacteristic) { echoStatus ->
            state = state.copy(echo = echoStatus == 1)
            Napier.i("Echo Status $echoStatus", tag = TAG)
        }
    }

    private fun ambientLightValue(percent: Int) {
//        _ambientLight = percent
        state = state.copy(ambientLight = percent)
    }

    private fun ambientLight() {
        viewModelScope.launch {
            try {
//                state = state.copy(ambientLight = _ambientLight)
                ambientLightCharacteristic.write(DataParser.intToByteArray(state.ambientLight))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun power(power: Boolean) {
        viewModelScope.launch {
            try {
                state = state.copy(power = power)
                powerCharacteristic.write(DataParser.intToByteArray(if (state.power) 1 else 0))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun uvLight(isOn: Boolean) {
        viewModelScope.launch {
            try {
                state = state.copy(uv = isOn)
                uvLightCharacteristic.write(DataParser.intToByteArray(if (isOn) 1 else 0))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun indicatorLED(isOn: Boolean) {
        viewModelScope.launch {
            try {
                state = state.copy(isLedOn = isOn)
                indicatorLEDCharacteristic.write(DataParser.intToByteArray(if (isOn) 1 else 0))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

//    private fun motorSpeedValue(speed: Int) {
//        state = state.copy(motorSpeed = speed)
//    }

    private fun motorSpeed(speed: Int) {
        viewModelScope.launch {
            try {
//                echoValueBasedOnMotorSpeed(speed)
                state = state.copy(motorSpeed = speed)
                motorSpeedCharacteristic.write(DataParser.intToByteArray(speed))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun echo(isEchoOn: Boolean) {
        viewModelScope.launch {
            try {
//                motorSpeedBasedOnEcho(isEchoOn)
                state = state.copy(echo = isEchoOn)
                echoCharacteristic.write(DataParser.intToByteArray(if (isEchoOn) 1 else 0))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun errorState(hasError: Boolean) {
        viewModelScope.launch {
            try {
                state = state.copy(error = hasError)
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
