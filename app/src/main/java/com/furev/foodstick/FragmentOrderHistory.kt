package com.furev.foodstick

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.furev.foodstick.dataClasses.Dishes
import com.furev.foodstick.dataClasses.OrderHistory
import com.furev.foodstick.databinding.FragmentOrderHistoryBinding
import com.furev.foodstick.util.ConnectionManager
import org.json.JSONException

class FragmentOrderHistory : Fragment() {
    val list = ArrayList<OrderHistory>()
    val dishlist = ArrayList<Dishes>()
    lateinit var recyclerRestaurant: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter : HistoryRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentOrderHistoryBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_order_history, container, false)

        (activity as MainActivity).showSetUpToolbar()
        val sharedPreferences =
            context?.getSharedPreferences("kotlinsharedpreference", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =
            (sharedPreferences?.edit() ?: this@FragmentOrderHistory) as SharedPreferences.Editor
        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/${sharedPreferences?.getString(
            "user_id",
            null
        )}"



        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {
                    try {
                        val success = it.getJSONObject("data")
                        val check = success.getBoolean("success")

                        if (check) {
                            val Order = success.getJSONArray("data")
                            for ( i in 0 until Order.length()){
                                val orderHist = Order.getJSONObject(i)
                                val dishesArray=orderHist.getJSONArray("food_items")
                                var dish : Dishes
                                for (j in 0 until dishesArray.length()){
                                    val dishHist = dishesArray.getJSONObject(j)
                                    dish =
                                        Dishes(
                                            dishHist.getString("food_item_id"),
                                            dishHist.getString("name"),
                                            dishHist.getString("cost")
                                        )
                                    dishlist.add(dish)
                                }
                                val rest =
                                    OrderHistory(
                                        orderHist.getString("order_id"),
                                        orderHist.getString("restaurant_name"),
                                        orderHist.getString("total_cost"),
                                        orderHist.getString("order_placed_at"),
                                        dishlist
                                    )
                                list.add(rest)

                            }
                            recyclerAdapter =
                                HistoryRecyclerAdapter(
                                    activity as Context,
                                    list
                                )
                            recyclerRestaurant = binding.recyclerRestaurents
                            layoutManager = LinearLayoutManager(activity)

                            recyclerRestaurant.adapter = recyclerAdapter
                            recyclerRestaurant.layoutManager = layoutManager
                        } else {
                            Toast.makeText(
                                context,
                                "${it} ",
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
}


