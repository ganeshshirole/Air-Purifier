import org.kmm.airpurifier.ble.advertisement.Advertiser
import org.kmm.airpurifier.ble.advertisement.IOSServer
import org.kmm.airpurifier.ble.advertisement.IOSServerWrapper
import org.kmm.airpurifier.ble.client.Client
import org.kmm.airpurifier.ble.client.IOSClient
import org.kmm.airpurifier.ble.client.IOSClientWrapper
import org.kmm.airpurifier.ble.scanner.Scanner
import org.kmm.airpurifier.ble.server.NotificationsRecords
import org.kmm.airpurifier.ble.server.Server

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object BleFactory {
    actual fun provideScanner(): Scanner {
        return Scanner(provideIOSClientWrapper())
    }

    actual fun provideAdvertiser(): Advertiser {
        return Advertiser(provideIOSServerWrapper())
    }

    actual fun provideClient(): Client {
        return Client(provideIOSClientWrapper())
    }

    actual fun provideServer(): Server {
        return Server(provideIOSServerWrapper())
    }

    private fun provideNotificationRecords(): NotificationsRecords {
        return NotificationsRecords()
    }

    private fun provideIOSServerWrapper(): IOSServerWrapper {
        return IOSServerWrapper(IOSServer(provideNotificationRecords()))
    }

    private fun provideIOSClientWrapper(): IOSClientWrapper {
        return IOSClientWrapper(IOSClient())
    }
}