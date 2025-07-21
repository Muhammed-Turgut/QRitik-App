package com.RealizeStudio.qritik.room

import android.content.Context
import androidx.room.Room


object DatabaseProvider {
    @Volatile
    private var INSTANCE: QRsavesDatabase? = null

    fun getDatabase(context: Context): QRsavesDatabase {
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
