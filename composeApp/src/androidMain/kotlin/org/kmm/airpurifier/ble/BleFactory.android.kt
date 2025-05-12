import org.kmm.airpurifier.appContext
import org.kmm.airpurifier.ble.client.AndroidClient
import org.kmm.airpurifier.ble.client.Client
import org.kmm.airpurifier.ble.scanner.Scanner

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object BleFactory {
    actual fun provideScanner(): Scanner {
        return Scanner(provideAndroidClient())
    }

    actual fun provideClient(): Client {
        return Client(provideAndroidClient())
    }

    private fun provideAndroidClient(): AndroidClient {
        return AndroidClient(appContext)
    }
}
