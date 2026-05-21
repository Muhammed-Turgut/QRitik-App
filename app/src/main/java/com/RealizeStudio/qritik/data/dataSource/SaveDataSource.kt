package com.RealizeStudio.qritik.data.dataSource

import com.RealizeStudio.qritik.data.entity.QRsavesItem
import com.RealizeStudio.qritik.room.QRsavesItemDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveDataSource @Inject constructor (var qRSavesItemDao: QRsavesItemDao) {

    suspend fun save (qrType: String, qrContents: String, date: String, isCreated: Boolean = false, isFavorite: Boolean = false){
        val newSave = QRsavesItem(qrType, qrContents, date, isCreated, isFavorite)
        qRSavesItemDao.insert(newSave)
    }

    suspend fun update (id: Int, qrType: String, qrContents: String, date: String, isCreated: Boolean = false, isFavorite: Boolean = false){
        val item = qRSavesItemDao.getItemById(id)
        item?.let {
            it.QR_Type = qrType
            it.QR_contents = qrContents
            it.date = date
            it.isCreated = isCreated
            it.isFavorite = isFavorite
            qRSavesItemDao.update(it)
        }
    }

    suspend fun getAllSaves () : Flow<List<QRsavesItem>>{
        return qRSavesItemDao.getItemAll()
    }

    suspend fun delete(id: Int) {
        val item = qRSavesItemDao.getItemById(id)
        item?.let {
            qRSavesItemDao.delete(it)
        }
    }



}