package com.furev.foodstick

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.furev.foodstick.dataClasses.RestaurantDetails
import com.furev.foodstick.database.CartRestEntity
import com.furev.foodstick.database.FavRestDatabase

class RestDetailRecyclerAdapter (val context: Context, val detailList:ArrayList<RestaurantDetails>, val btnProceed:Button, val parentFragment:FragmentRestDetails):RecyclerView.Adapter<RestDetailRecyclerAdapter.DetailHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_restaurant_detail,parent,false)
        return DetailHolder(view)
    }

    override fun getItemCount(): Int {
        return detailList.size
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: DetailHolder, position: Int) {
        val restdetails = detailList[position]
        holder.name.text = restdetails.name.toString()
        holder.price.text = "Rs. ${restdetails.cost}"
        val cartRestEntity= CartRestEntity(
            restdetails.id,
            restdetails.name,
            restdetails.cost,
            restdetails.restaurant_id
        )

        val checkfav = DBAsyncTaskC(context, cartRestEntity, 1).execute()
        val isfav = checkfav.get()
        if (isfav) {
            holder.addbtn.setBackgroundResource(R.color.colorAccent)
            holder.addbtn.text="Remove"
        } else {
            holder.addbtn.setBackgroundResource(R.color.layoutBackground)
            holder.addbtn.text="Add"
        }




        holder.addbtn.setOnClickListener {

            if (!DBAsyncTaskC(context,cartRestEntity,1).execute().get()) {
                val async = DBAsyncTaskC(context, cartRestEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.addbtn.setBackgroundResource(R.color.colorAccent)
                    holder.addbtn.text="Remove"
                } else {
                    Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async =DBAsyncTaskC(context,cartRestEntity,3).execute()
                val result = async.get()
                if (result){
                    holder.addbtn.setBackgroundResource(R.color.layoutBackground)
                    holder.addbtn.text="Add"
                }
                else{
                    Toast.makeText(context,"Unexpected Error", Toast.LENGTH_SHORT).show()
                }
            }
            val check = DBAsyncTaskC(context,cartRestEntity,4).execute().get()
            if (check){
                btnProceed.visibility = View.VISIBLE
            }
            else{
                btnProceed.visibility= View.GONE
            }

        }
        btnProceed.setOnClickListener {
            parentFragment.findNavController().navigate(FragmentRestDetailsDirections.actionFragmentRestDetailsToFragmentCart())
        }

    }

    class DetailHolder(view: View):RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.txt_restDetail_name)
        val price:TextView = view.findViewById(R.id.txt_restDetail_price)
        val addbtn:Button=view.findViewById(R.id.btn_restDetail_Add)

    }
}
class DBAsyncTaskC(val context: Context, val cartRestEntity: CartRestEntity, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {
    val db = Room.databaseBuilder(context, FavRestDatabase::class.java, "bd-cartrest").fallbackToDestructiveMigration().build()

    override fun doInBackground(vararg params: Void?): Boolean {
        when (mode) {
            1 -> {
                val cart = db.favRestDao().getCartById(cartRestEntity.id.toString())
                db.close()
                return cart != null
            }
            2 -> {
                db.favRestDao().addToCart(cartRestEntity)
                return true

            }
            3 -> {
                db.favRestDao().removeFromCart(cartRestEntity)
                return true

            }
            4 ->{
               val check= db.favRestDao().getAllFromCart()
                return check.isNotEmpty()
            }
        }
        return false
    }


}