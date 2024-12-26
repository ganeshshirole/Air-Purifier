import org.kmm.airpurifier.ble.advertisement.Advertiser
import org.kmm.airpurifier.ble.client.Client
import org.kmm.airpurifier.ble.scanner.Scanner
import org.kmm.airpurifier.ble.server.Server


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object BleFactory {

    fun provideScanner(): Scanner

    fun provideAdvertiser(): Advertiser

    fun provideClient(): Client

    fun provideServer(): Server
}