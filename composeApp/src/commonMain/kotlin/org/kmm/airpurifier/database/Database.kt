import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.kmm.airpurifier.database.BLEDevice
import org.kmm.airpurifier.database.BLEDeviceDao

@ConstructedBy(BLEDatabaseConstructor::class)
@Database(
    entities = [BLEDevice::class],
    version = 1
)
abstract class BLEDeviceDatabase: RoomDatabase() {
    abstract fun bleDeviceDao(): BLEDeviceDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object BLEDatabaseConstructor : RoomDatabaseConstructor<BLEDeviceDatabase> {
    override fun initialize(): BLEDeviceDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<BLEDeviceDatabase>
): BLEDeviceDatabase {
    return builder
//        .addMigrations(MIGRATIONS)
        .fallbackToDestructiveMigrationOnDowngrade(false)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}