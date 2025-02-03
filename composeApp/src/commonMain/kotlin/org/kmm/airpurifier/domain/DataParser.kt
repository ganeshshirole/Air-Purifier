package org.kmm.airpurifier.domain

object DataParser {
    fun ByteArray.toDisplayString(): String {
        return this.joinToString(".") {
            it.toUByte().toString()
        }
    }

    fun byteArrayToInt(bytes: ByteArray): Int {
        // Assuming the byte array is in little-endian format and contains exactly 2 bytes (like in the AIQ example)
        return when (bytes.size) {
            2 -> {
                // Convert 2-byte array to Int (for AIQ characteristic with UInt16 size)
                ((bytes[1].toInt() and 0xFF) shl 8) or (bytes[0].toInt() and 0xFF)
            }
            4 -> {
                // Convert 4-byte array to Int (for larger Int32 values)
                ((bytes[3].toInt() and 0xFF) shl 24) or
                        ((bytes[2].toInt() and 0xFF) shl 16) or
                        ((bytes[1].toInt() and 0xFF) shl 8) or
                        (bytes[0].toInt() and 0xFF)
            }
            else -> throw IllegalArgumentException("Invalid byte array size")
        }
    }

    // Convert an Int (UInt16) to a ByteArray
    fun intToByteArray(value: Int): ByteArray {
        require(value in 0..0xFFFF) { "Value must be between 0 and 65535 for UInt16." }
        return byteArrayOf(
            (value and 0xFF).toByte(),         // Least significant byte
            ((value shr 8) and 0xFF).toByte()  // Most significant byte
        )
    }
}