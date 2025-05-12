package org.kmm.airpurifier.ble.scanner

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.kmm.airpurifier.ble.client.AndroidClient

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Scanner(private val client: AndroidClient) {

    actual fun scan(): Flow<List<IoTDevice>> {

        val result = mutableListOf<IoTDevice>()

        return client.scan().map { IoTDevice(it) }
            .onEach { result += it }
            .map { result }
            .map { it.distinctBy { device -> device.address } }
    }
}
