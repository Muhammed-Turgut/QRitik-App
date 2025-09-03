package com.RealizeStudio.qritik.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.RealizeStudio.qritik.data.entity.QRsavesItem
import kotlinx.coroutines.flow.Flow


@Dao
interface QRsavesItemDao {
    @Query("SELECT * FROM QRSaves")
     fun getItemAll(): Flow<List<QRsavesItem>>//Tüm verileri listeleyecek yapı

    @Query("SELECT * FROM QRSaves WHERE id = :id") //this fon searcing
    suspend fun getItemById(id: Int): QRsavesItem?

    @Insert
    suspend fun insert(item: QRsavesItem) // veri eklemek için kullandığımız fonksiyon

    @Delete
    suspend fun  delete(item: QRsavesItem) //veri silmek için kullanıdğımız fonksiyon

    @Update
    suspend fun update(item: QRsavesItem)

}