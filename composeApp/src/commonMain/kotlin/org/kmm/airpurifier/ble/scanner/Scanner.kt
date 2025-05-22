package org.kmm.airpurifier.ble.scanner

import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Scanner {
    fun scan(): Flow<List<IoTDevice>>
    fun stopScan()
}
