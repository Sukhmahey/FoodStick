package com.furev.foodstick

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.furev.foodstick.dataClasses.FavRestaurantList
import com.furev.foodstick.database.FavRestDatabase
import com.furev.foodstick.database.FavRestEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<FavRestaurantList>
) : RecyclerView.Adapter<FavouriteRecyclerAdapter.DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_favourite, parent, false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.restName.text = restaurant.name
        holder.restPrice.text = restaurant.cost
        holder.restRating.text = restaurant.rating
        Picasso.get().load(restaurant.logo).into(holder.restLogo)
        val favRestEntity =
            FavRestEntity(restaurant.id)
        val checkfav = DBAsyncTaskF(context, favRestEntity, 1).execute()
        val isfav = checkfav.get()
        if (isfav) {
            holder.restfav.setImageResource(R.drawable.ic_heart_fill)
        } else {
            holder.restfav.setImageResource(R.drawable.ic_heart_blank)
        }
        holder.restfav.setOnClickListener {
            if (!DBAsyncTaskF(context,favRestEntity,1).execute().get()) {
                val async = DBAsyncTaskF(context, favRestEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.restfav.setImageResource(R.drawable.ic_heart_fill)
                } else {
                    Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async =DBAsyncTaskF(context,favRestEntity,3).execute()
                val result = async.get()
                if (result){
                    holder.restfav.setImageResource(R.drawable.ic_heart_blank)
                }
                else{
                    Toast.makeText(context,"Unexpected Error",Toast.LENGTH_SHORT).show()
                }
            }

        }



    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restName: TextView = view.findViewById(R.id.txt_restaurant_name)
        val restPrice: TextView = view.findViewById(R.id.txt_restaurant_price)
        val restRating: TextView = view.findViewById(R.id.txt_restaurant_rating)
        val restLogo: ImageView = view.findViewById(R.id.img_restaurant)
        val restfav: ImageView = view.findViewById(R.id.img_restaurant_fav)

    }



}

class DBAsyncTaskF(val context: Context, val favRestEntity: FavRestEntity, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {
    val db = Room.databaseBuilder(context, FavRestDatabase::class.java, "bd-favrest").build()
    override fun doInBackground(vararg params: Void?): Boolean {
        when (mode) {
            1 -> {
                val favRest = db.favRestDao().getFavById(favRestEntity.Id.toString())
                db.close()
                return true
            }
            2 -> {
                db.favRestDao().addFavRest(favRestEntity)
                return true
            }
            3 -> {
                db.favRestDao().removeFavrest(favRestEntity)
                return true
            }
        }
        return false
    }


}