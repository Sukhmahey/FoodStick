package com.furev.foodstick.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavRestEntity::class, CartRestEntity::class],version = 3,exportSchema = true)
abstract class FavRestDatabase :RoomDatabase(){
    abstract fun favRestDao(): FavRestDao
}