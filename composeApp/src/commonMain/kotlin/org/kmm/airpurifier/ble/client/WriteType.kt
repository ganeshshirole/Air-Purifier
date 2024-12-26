package org.kmm.airpurifier.ble.client

/**
 * Defines available BLE write types.
 *
 * @property value Native Android value.
 */
enum class WriteType {
    /**
     * Write characteristic, requesting acknowledgement by the remote device.
     */
    DEFAULT,

    /**
     * Write characteristic without requiring a response by the remote device.
     */
    NO_RESPONSE,
}
