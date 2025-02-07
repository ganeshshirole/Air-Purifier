package org.kmm.airpurifier.presentation.intent

sealed class HomeScreenIntent {
    data object CONNECT : HomeScreenIntent()
    data class IndicatorLed(val isOn: Boolean) : HomeScreenIntent()
    data class Power(val isOn: Boolean) : HomeScreenIntent()
    data class Echo(val isOn: Boolean) : HomeScreenIntent()
    data class MotorSpeed(val speed: Int) : HomeScreenIntent()
    data class AmbientLightValue(val lightValue: Int) : HomeScreenIntent()
    data object AMBIENTLIGHT : HomeScreenIntent()
    data class UVLight(val isOn: Boolean): HomeScreenIntent()
    data class ShowDialog(val isShow: Boolean) : HomeScreenIntent()
}
