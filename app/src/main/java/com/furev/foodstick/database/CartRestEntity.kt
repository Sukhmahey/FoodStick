package com.furev.foodstick.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cartRest")
data class CartRestEntity (
    @ColumnInfo(name = "restid")
    @PrimaryKey
    val id:String,
    @ColumnInfo(name = "rest_name")
    val name:String,
    @ColumnInfo(name = "rest_price")
    val price:String,
    @ColumnInfo(name="rest_id")
    val rest_id:String

)