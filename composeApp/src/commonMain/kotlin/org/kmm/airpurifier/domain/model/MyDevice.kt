package org.kmm.airpurifier.domain.model

data class MyDevice(
    val name: String,
    val address: String,
    val isSavedDevice: Boolean = false,
    var title: String? = null,
)