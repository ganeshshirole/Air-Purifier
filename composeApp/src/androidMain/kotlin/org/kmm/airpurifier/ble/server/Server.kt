package org.kmm.airpurifier.ble.server

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.kotlin.ble.server.main.ServerBleGatt
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattCharacteristicConfig
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattDescriptorConfig
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattServiceConfig
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattServiceType
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBluetoothGattConnection
import org.kmm.airpurifier.ble.scanner.IoTDevice

@SuppressLint("MissingPermission")
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Server(private val context: Context) {

    private var server: ServerBleGatt? = null

    actual val connections: Flow<Map<IoTDevice, ServerProfile>>
        get() = server?.connections?.map {
            it.mapKeys { IoTDevice(it.key) }.mapValues { it.value.toDomain() }
        } ?: emptyFlow()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    actual suspend fun startServer(
        services: List<BleServerServiceConfig>,
        scope: CoroutineScope,
    ) {

        val config = services.map {
            val characteristics = it.characteristics.map {
                val descritptors = it.descriptors.map {
                    it.uuid
                    ServerBleGattDescriptorConfig(
                        it.uuid,
                        it.permissions.toNativePermissions()
                    )
                }

                ServerBleGattCharacteristicConfig(
                    it.uuid,
                    it.properties.toNativeProperties(),
                    it.permissions.toNativePermissions(),
                    descritptors
                )
            }

            ServerBleGattServiceConfig(
                it.uuid,
                ServerBleGattServiceType.SERVICE_TYPE_PRIMARY,
                characteristics
            )
        }

        server = ServerBleGatt.create(context, scope, *config.toTypedArray())
    }

    actual suspend fun stopServer() {
        server?.stopServer()
    }

    private fun ServerBluetoothGattConnection.toDomain(): ServerProfile {
        return ServerProfile(services)
    }
}
