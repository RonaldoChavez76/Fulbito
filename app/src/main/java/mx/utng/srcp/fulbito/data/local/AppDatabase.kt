package mx.utng.srcp.fulbito.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mx.utng.srcp.fulbito.data.local.dao.MatchDao
import mx.utng.srcp.fulbito.data.local.entity.EventEntity
import mx.utng.srcp.fulbito.data.local.entity.MatchEntity
import mx.utng.srcp.fulbito.data.local.entity.PlayerEntity

@Database(entities = [MatchEntity::class, PlayerEntity::class, EventEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fulbito_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
