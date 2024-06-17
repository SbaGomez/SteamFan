package com.ar.sebastiangomez.steam.data.dbLocal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Database(entities = [GameDetailLocal::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun gameDetailsDao(): GameDetailDAO

    companion object {
        @Volatile // se puede acceder desde multiples hilos de ejecucion
        private var _instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase = _instance ?: synchronized(this) {
            _instance ?: buildDatabase(context)
        }

        private fun buildDatabase(context: Context): AppDataBase {
            return Room.databaseBuilder(context, AppDataBase::class.java, "AppDataBase")
                .fallbackToDestructiveMigration() // esto en produccion no es recomendable
                .build()
        }

        suspend fun clean(context: Context) = coroutineScope {
            launch(Dispatchers.IO) {
                getInstance(context).clearAllTables()
            }
        }
    }
}