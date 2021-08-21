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
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.furev.foodstick.FragmentCartDirections
import com.furev.foodstick.database.CartRestEntity
import com.furev.foodstick.database.FavRestDatabase
import com.furev.foodstick.databinding.FragmentCartBinding
import com.furev.foodstick.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class FragmentCart : Fragment() {
    lateinit var recyclerRestaurant: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: CartRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentCartBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_cart, container, false)
        val sharedPreferences =
            context?.getSharedPreferences("kotlinsharedpreference", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =
            (sharedPreferences?.edit() ?: this@FragmentCart) as SharedPreferences.Editor
        recyclerAdapter =
            CartRecyclerAdapter(activity as Context)
        recyclerRestaurant = binding.recyclerRestaurents
        layoutManager = LinearLayoutManager(activity)

        recyclerRestaurant.adapter = recyclerAdapter
        recyclerRestaurant.layoutManager = layoutManager
        val Order = DBAsyncTaskCartOrder(
            activity as Context,
            1
        ).execute().get()
        var amount = 0
        for (i in Order) {
            amount += i.price.toInt()
        }
        binding.btnPlaceOrder.text = "Place Order (Rs. ${amount})"
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        binding.btnPlaceOrder.setOnClickListener {
            if (ConnectionManager().checkConnectivity(activity as Context)) {

                val jsonObject = JSONObject()
                for (s in Order) {
                    jsonObject.put("user_id", sharedPreferences?.getString("user_id", null))
                    jsonObject.put("restaurant_id", s.rest_id)
                    jsonObject.put("total_cost", amount.toString())
                    val food = JSONArray()
                    food.put(s.id)
                    jsonObject.put("food", food)

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonObject,
                        Response.Listener {
                            try {
                                val success = it.getJSONObject("data")
                                val check = success.getBoolean("success")

                                if (check) {
                                    this.findNavController().navigate(FragmentCartDirections.actionFragmentCartToFragmentDone())
                                    DBAsyncTaskCartOrder(
                                        activity as Context,
                                        2
                                    ).execute()

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error ",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    activity as Context,
                                    "Unexpected Error",
                                    Toast.LENGTH_SHORT
                                ).show()
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
        }




        return binding.root
    }


}

class DBAsyncTaskCartOrder(val context: Context, val mode: Int) :
    AsyncTask<Void, Void, List<CartRestEntity>>() {
    val db = Room.databaseBuilder(context, FavRestDatabase::class.java, "bd-cartrest")
        .fallbackToDestructiveMigration().build()

    override fun doInBackground(vararg params: Void?): List<CartRestEntity>? {
        val cart = db.favRestDao().getAllFromCart()
        when (mode) {
            1 -> {
                return cart
            }
            2 -> {
                db.favRestDao().clearCart()
                db.close()
            }
        }

        return cart
    }
}