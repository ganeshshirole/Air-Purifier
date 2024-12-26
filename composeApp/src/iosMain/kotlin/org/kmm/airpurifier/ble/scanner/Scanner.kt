package org.kmm.airpurifier.ble.scanner

import kotlinx.coroutines.flow.Flow
import org.kmm.airpurifier.ble.client.IOSClientWrapper

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Scanner(private val client: IOSClientWrapper) {

    actual fun scan(): Flow<List<IoTDevice>> = client.value.scan()
}
