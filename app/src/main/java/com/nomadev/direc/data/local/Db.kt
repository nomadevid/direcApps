package com.nomadev.direc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PasienEntity::class],
    version = 1
)

abstract class Db : RoomDatabase() {
    abstract fun pasienDao(): PasienDao

    companion object {

        @Volatile
        private var instance: Db? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            Db::class.java,
            "patient_db"
        ).build()
    }
}