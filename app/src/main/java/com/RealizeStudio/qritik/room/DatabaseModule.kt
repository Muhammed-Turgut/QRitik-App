package com.RealizeStudio.qritik.room

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQRsavesDatabase(@ApplicationContext context: Context): QRsavesDatabase {
        return Room.databaseBuilder(
            context,
            QRsavesDatabase::class.java,
            "qr_saves_database"
        ).build()
    }

    @Provides
    fun provideQRsavesItemDao(database: QRsavesDatabase): QRsavesItemDao {
        return database.qrSavesItemDao()
    }
}