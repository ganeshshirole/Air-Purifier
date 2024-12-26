package org.kmm.airpurifier.ble.client

import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices

internal fun ClientBleGattServices.toCrossplatform(): ClientServices {
    return ClientServices(this)
}
