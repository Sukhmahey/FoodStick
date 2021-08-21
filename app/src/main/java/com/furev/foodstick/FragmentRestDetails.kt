package com.furev.foodstick

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.furev.foodstick.dataClasses.RestaurantDetails
import com.furev.foodstick.database.CartRestEntity
import com.furev.foodstick.database.FavRestDatabase
import com.furev.foodstick.databinding.FragmentRestDetailsBinding
import com.furev.foodstick.util.ConnectionManager
import org.json.JSONException

class FragmentRestDetails : Fragment() {
    val restaurantdetaillist = ArrayList<RestaurantDetails>()
    lateinit var recyclerRestaurant: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter : RestDetailRecyclerAdapter
    lateinit var url:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRestDetailsBinding =DataBindingUtil.inflate(inflater,R.layout.fragment_rest_details, container, false)
        (activity as MainActivity).showSetUpToolbarUpButton()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Menu"
        val queue = Volley.newRequestQueue(activity as Context)

        arguments?.let {
            val args = FragmentRestDetailsArgs.fromBundle(it)
            url="http://13.235.250.119/v2/restaurants/fetch_result/${args.id}"
        }


        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener {
                    try {
                        val jsonObject = it.getJSONObject("data")
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            val restaurant_details = jsonObject.getJSONArray("data")
                            for (i in 0 until restaurant_details.length()) {
                                val rest = restaurant_details.getJSONObject(i)
                                val restaurant =
                                    RestaurantDetails(
                                        rest.getString("id"),
                                        rest.getString("name"),
                                        rest.getString("cost_for_one"),
                                        rest.getString("restaurant_id")

                                    )
                                restaurantdetaillist.add(restaurant)

                            }
                            recyclerAdapter =
                                RestDetailRecyclerAdapter(activity as Context, restaurantdetaillist,binding.btnAddToCart,this)
                            recyclerRestaurant = binding.recyclerRestaurents
                            layoutManager = LinearLayoutManager(activity)

                            recyclerRestaurant.adapter = recyclerAdapter
                            recyclerRestaurant.layoutManager = layoutManager

                        } else {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()

                    }
                },
                Response.ErrorListener {
                    Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show()

                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["content-type"] = "application/json"
                    headers["token"] = "9bf534118365f1"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }

        else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Failed")
            dialog.setPositiveButton("Open Setting"){text,listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text,listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        val check = DBAsyncTaskCartcheck(activity as Context).execute().get()
        if (check.isNotEmpty()){
            binding.btnAddToCart.visibility=View.VISIBLE
        }else{
            binding.btnAddToCart.visibility=View.GONE
        }
        return binding.root

    }


}
class DBAsyncTaskCartcheck(val context: Context) :
    AsyncTask<Void, Void, List<CartRestEntity>>() {
    val db = Room.databaseBuilder(context, FavRestDatabase::class.java, "bd-cartrest").fallbackToDestructiveMigration().build()
    override fun doInBackground(vararg params: Void?): List<CartRestEntity>? {
        val cart = db.favRestDao().getAllFromCart()
        return cart
    }
}