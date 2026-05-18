package com.secureflux.wallet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

class Converters {
    @TypeConverter fun txnStatus(v: String) = TxnStatus.valueOf(v)
    @TypeConverter fun txnStatus(v: TxnStatus) = v.name
    @TypeConverter fun txnType(v: String) = TxnType.valueOf(v)
    @TypeConverter fun txnType(v: TxnType) = v.name
    @TypeConverter fun syncState(v: String) = SyncState.valueOf(v)
    @TypeConverter fun syncState(v: SyncState) = v.name
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE syncState != 'SYNCED' ORDER BY createdAt ASC")
    fun observeUnsynced(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE syncState != 'SYNCED'")
    suspend fun getUnsynced(): List<Transaction>

    /** Idempotency guard: a row already exists for this key -> caller must not re-charge. */
    @Query("SELECT * FROM transactions WHERE idempotencyKey = :key LIMIT 1")
    suspend fun findByIdempotencyKey(key: String): Transaction?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(txn: Transaction): Long

    @Update
    suspend fun update(txn: Transaction)
}

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, "secureflux.db"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
    }
}
