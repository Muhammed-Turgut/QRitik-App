package com.RealizeStudio.qritik.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.RealizeStudio.qritik.data.entity.QRsavesItem


@Database(entities = [QRsavesItem::class], version = 1, exportSchema = false)
abstract class QRsavesDatabase : RoomDatabase() {
    abstract fun qrSavesItemDao(): QRsavesItemDao

    companion object {
        @Volatile
        private var INSTANCE: QRsavesDatabase? = null

        fun getInstance(context: Context): QRsavesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QRsavesDatabase::class.java,
                    "qr_saves_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}