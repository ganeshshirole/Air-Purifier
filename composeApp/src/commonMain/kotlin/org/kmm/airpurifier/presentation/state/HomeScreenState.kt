package org.kmm.airpurifier.presentation.state

data class HomeScreenState(
    val isConnected: Boolean = false,
    val aiq: Int = 0,
    val motorSpeed: Int = 0,
    val ambientLight: Int = 0,
    val uv: Boolean = false,
    val filterLife: Int = 0,
    val power: Boolean = false,
    val echo: Boolean = false,
    val isLedOn: Boolean = false,
    val error: Boolean = false,
    val showDialog: Boolean = false,
    val navigate: String = "/"
)
