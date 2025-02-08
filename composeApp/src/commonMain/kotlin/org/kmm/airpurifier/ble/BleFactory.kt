import org.kmm.airpurifier.ble.client.Client
import org.kmm.airpurifier.ble.scanner.Scanner


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object BleFactory {

    fun provideScanner(): Scanner

    fun provideClient(): Client
}