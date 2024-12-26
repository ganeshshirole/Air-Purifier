package org.kmm.airpurifier.ble.client

import com.benasher44.uuid.Uuid

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ClientServices {

    fun findService(uuid: Uuid): ClientService?
}
