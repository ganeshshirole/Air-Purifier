import org.kmm.airpurifier.ble.client.Client
import org.kmm.airpurifier.ble.client.IOSClient
import org.kmm.airpurifier.ble.client.IOSClientWrapper
import org.kmm.airpurifier.ble.scanner.Scanner

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object BleFactory {
    actual fun provideScanner(): Scanner {
        return Scanner(provideIOSClientWrapper())
    }

    actual fun provideClient(): Client {
        return Client(provideIOSClientWrapper())
    }

    private fun provideIOSClientWrapper(): IOSClientWrapper {
        return IOSClientWrapper(IOSClient())
    }
}