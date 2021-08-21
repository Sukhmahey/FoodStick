package com.furev.foodstick

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.furev.foodstick.database.CartRestEntity
import com.furev.foodstick.database.FavRestDatabase

class CartRecyclerAdapter(val context: Context) :RecyclerView.Adapter<CartRecyclerAdapter.CartHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_cart,parent,false)
        return CartHolder(view)
    }

    override fun getItemCount(): Int {
        val size=DBAsyncTaskCart(context).execute().get()
        return size.size
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val Cart = DBAsyncTaskCart(context).execute().get()
        val CartRest = Cart[position]
        holder.name.text= CartRest.name
        holder.price.text="Rs.${CartRest.price}"
    }

    class CartHolder(view: View):RecyclerView.ViewHolder(view){
        val name:TextView = view.findViewById(R.id.dish_name)
        val price:TextView = view.findViewById(R.id.dish_price)

    }
}
class DBAsyncTaskCart(val context: Context) :
    AsyncTask<Void, Void, List<CartRestEntity>>() {
    val db = Room.databaseBuilder(context, FavRestDatabase::class.java, "bd-cartrest").fallbackToDestructiveMigration().build()
    override fun doInBackground(vararg params: Void?): List<CartRestEntity>? {
        val cart = db.favRestDao().getAllFromCart()
        return cart
        }
    }


