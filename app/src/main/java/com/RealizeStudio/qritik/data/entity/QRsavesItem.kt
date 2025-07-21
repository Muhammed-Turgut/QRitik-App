package com.RealizeStudio.qritik.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "QRSaves")
data class QRsavesItem(
    @ColumnInfo(name = "QR_Type")
    var QR_Type: String?,

    @ColumnInfo(name = "QR_contents")
    var QR_contents: String?,

    @ColumnInfo(name = "date")
    var date: String?,

) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}