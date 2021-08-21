package com.furev.foodstick

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.furev.foodstick.dataClasses.FavRestaurantList
import com.furev.foodstick.database.FavRestDatabase
import com.furev.foodstick.database.FavRestEntity
import com.furev.foodstick.databinding.FragmentFavouriteBinding
import com.furev.foodstick.util.ConnectionManager
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class FragmentFavourite : Fragment() {
    val restaurantlist = arrayListOf<FavRestaurantList>()
    lateinit var recyclerRestaurant: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: FavouriteRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentFavouriteBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_favourite, container, false)

        val sharedPreferences =
            context?.getSharedPreferences("kotlinsharedpreference", Context.MODE_PRIVATE)
        val editor = (sharedPreferences?.edit() ?: this) as SharedPreferences.Editor
        (activity as MainActivity).showSetUpToolbar()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Favourites"


        val favList = FavouriteRestaurants(
            activity as Context
        ).execute().get()

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"


        val queue = Volley.newRequestQueue(activity as Context)


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
                                for (i in favList) {
                                    if (i.Id == rest.getString("id")) {
                                        val restaurant =
                                            FavRestaurantList(
                                                rest.getString("id"),
                                                rest.getString("name"),
                                                rest.getString("rating"),
                                                rest.getString("cost_for_one"),
                                                rest.getString("image_url")
                                            )
                                        restaurantlist.add(restaurant)
                                    }
                                }

                            }
                            if (restaurantlist.size == 0){
                                Toast.makeText(context,"No Favourite Restaurant",Toast.LENGTH_SHORT).show()
                            }
                            recyclerAdapter =
                                FavouriteRecyclerAdapter(
                                    activity as Context,
                                    restaurantlist
                                )
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

        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Failed")
            dialog.setPositiveButton("Open Setting") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }



        return binding.root
    }

    class FavouriteRestaurants(val context: Context) :
        AsyncTask<Void, Void, List<FavRestEntity>>() {
        override fun doInBackground(vararg params: Void?): List<FavRestEntity> {
            val db = Room.databaseBuilder(context, FavRestDatabase::class.java, "bd-favrest").build()
            return db.favRestDao().getAllFav()
        }

    }


}
