import org.kmm.airpurifier.appContext
import org.kmm.airpurifier.ble.advertisement.Advertiser
import org.kmm.airpurifier.ble.client.Client
import org.kmm.airpurifier.ble.scanner.Scanner
import org.kmm.airpurifier.ble.server.Server

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object BleFactory {
    actual fun provideScanner(): Scanner {
        return Scanner(appContext)
    }

    actual fun provideAdvertiser(): Advertiser {
        return Advertiser(appContext)
    }

    actual fun provideClient(): Client {
        return Client(appContext)
    }

    actual fun provideServer(): Server {
        return Server(appContext)
    }
}
