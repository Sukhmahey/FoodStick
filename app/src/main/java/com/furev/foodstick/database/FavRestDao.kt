package com.furev.foodstick.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavRestDao {

    @Insert
    fun addFavRest(favRest : FavRestEntity)

    @Delete
    fun removeFavrest(favRest: FavRestEntity)

    @Query("select * from favrest where restid = :id")
    fun getFavById(id:String): FavRestEntity

    @Query("select * from favrest")
    fun getAllFav():List<FavRestEntity>

    @Insert
    fun addToCart(cart: CartRestEntity)

    @Query("select * from cartrest where restid = :id")
    fun getCartById(id:String): CartRestEntity


    @Query("select * from cartRest")
    fun getAllFromCart():List<CartRestEntity>

    @Query("delete from cartRest")
    fun clearCart()

    @Delete
    fun removeFromCart(cart: CartRestEntity)
}