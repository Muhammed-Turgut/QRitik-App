package com.RealizeStudio.qritik.data.repo

import com.RealizeStudio.qritik.data.dataSource.SaveDataSource
import com.RealizeStudio.qritik.data.entity.QRsavesItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveRepository @Inject constructor(var saveDataSource: SaveDataSource) {

    suspend fun save (qrType: String, qrContents: String, date: String){
        saveDataSource.save(qrType,qrContents,date)

    }

    suspend fun update (id: Int,qrType: String, qrContents: String, date: String){
        saveDataSource.update(id,qrType,qrContents,date)

    }

    suspend fun getAllSaves () : Flow<List<QRsavesItem>>{
       return saveDataSource.getAllSaves()
    }

    suspend fun delete(id: Int) {
       saveDataSource.delete(id)
    }

}