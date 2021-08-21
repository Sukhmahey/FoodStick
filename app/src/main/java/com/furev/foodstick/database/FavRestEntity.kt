package com.furev.foodstick.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favrest")
data class FavRestEntity (
    @ColumnInfo(name = "restid")
    @PrimaryKey
    val Id : String

)