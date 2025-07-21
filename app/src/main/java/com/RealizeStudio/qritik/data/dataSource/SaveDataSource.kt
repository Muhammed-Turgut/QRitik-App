package com.RealizeStudio.qritik.data.dataSource

import com.RealizeStudio.qritik.data.entity.QRsavesItem
import com.RealizeStudio.qritik.room.QRsavesItemDao
import kotlinx.coroutines.flow.Flow

class SaveDataSource (var qRSavesItemDao: QRsavesItemDao) {

    suspend fun save (qrType: String, qrContents: String, date: String){
        val newSave = QRsavesItem(qrType,qrContents,date)
        qRSavesItemDao.insert(newSave)
    }

    suspend fun update (id: Int,qrType: String, qrContents: String, date: String){
        val newSave = QRsavesItem(qrType,qrContents,date)
        qRSavesItemDao.insert(newSave)
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