import org.kmm.airpurifier.ble.client.Client
import org.kmm.airpurifier.ble.client.IOSClient
import org.kmm.airpurifier.ble.scanner.Scanner

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object BleFactory {
    actual fun provideScanner(): Scanner {
        return Scanner(provideIOSClient())
    }

    actual fun provideClient(): Client {
        return Client(provideIOSClient())
    }

    private fun provideIOSClient(): IOSClient {
        return IOSClient()
    }
}