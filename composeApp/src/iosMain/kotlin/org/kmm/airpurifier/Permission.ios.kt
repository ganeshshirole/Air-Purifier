package org.kmm.airpurifier

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.darwin.NSObject

@Composable
actual fun RequestPermissions(content: @Composable () -> Unit) {

    LaunchedEffect(Unit) {
        val permissionsHandler = PermissionsHandler()
        permissionsHandler.requestLocationPermission()
    }

    content()
}

class PermissionsHandler : NSObject(), CLLocationManagerDelegateProtocol {
    private val locationManager = CLLocationManager()

    init {
        locationManager.delegate = this
    }

    fun requestLocationPermission() {
        // Request permission based on authorization status
        val status = CLLocationManager.authorizationStatus()
        when (status) {
            kCLAuthorizationStatusNotDetermined -> {
                locationManager.requestWhenInUseAuthorization()
            }
            kCLAuthorizationStatusAuthorizedWhenInUse, kCLAuthorizationStatusAuthorizedAlways -> {
                // Start using location or proceed with BLE scanning
                locationManager.startUpdatingLocation()
            }
            else -> {
                println("Location permission denied or restricted")
            }
        }
    }

    // Implement the delegate callback to handle status changes
    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
        when (didChangeAuthorizationStatus) {
            kCLAuthorizationStatusAuthorizedWhenInUse, kCLAuthorizationStatusAuthorizedAlways -> {
                println("Location access granted")
                locationManager.startUpdatingLocation()
            }
            kCLAuthorizationStatusDenied, kCLAuthorizationStatusRestricted -> {
                println("Location access denied")
            }
            else -> println("Location authorization not determined yet.")
        }
    }
}