package com.RealizeStudio.qritik.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.RealizeStudio.qritik.data.entity.QRsavesItem

@Database(entities = [QRsavesItem::class], version = 1, exportSchema = false)
abstract class QRsavesDatabase : RoomDatabase() {
    abstract fun qrSavesItemDao(): QRsavesItemDao
}