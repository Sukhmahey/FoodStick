package com.furev.foodstick

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.furev.foodstick.FragmentLauncherDirections
import com.furev.foodstick.databinding.FragmentLauncherBinding
import com.furev.foodstick.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class FragmentLauncher : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentLauncherBinding =DataBindingUtil.inflate(inflater,
            R.layout.fragment_launcher, container, false)

        val sharedPreferences =context?.getSharedPreferences("kotlinsharedpreference", Context.MODE_PRIVATE)
        val editor=(sharedPreferences?.edit()?:this@FragmentLauncher) as SharedPreferences.Editor
        val timer = object : CountDownTimer(
            COUNTDOWN_TIME,
            ONE_SECOND
        ) {
            override fun onFinish() {
                if (sharedPreferences!=null){
                if (ConnectionManager().checkConnectivity(context = activity as Context)){
                    val queue = Volley.newRequestQueue(activity as Context)
                    val url = "http://13.235.250.119/v2/login/fetch_result"
                    val jsonObject = JSONObject()
                    jsonObject.put("mobile_number",sharedPreferences?.getString("mobile_number","null") )
                    jsonObject.put("password",sharedPreferences?.getString("password","null"))

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonObject,
                        Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val successJsonObject = data.getBoolean("success")

                                if (successJsonObject) {
                                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT)
                                        .show()
                                    findNavController().navigate(FragmentLauncherDirections.actionLauncherFragmentToFragmentRestaurants())
                                    (activity as MainActivity).changeheader()

                                }
                                else {

                                    findNavController().navigate(FragmentLauncherDirections.actionLauncherFragmentToLoginFragment())

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


                }else{
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
            }
                else{
                    findNavController().navigate(FragmentLauncherDirections.actionLauncherFragmentToLoginFragment())
                }
            }
            override fun onTick(millisUntilFinished: Long) {

            }
        }
            timer.start()

        return binding.root
    }


    companion object {
        private const val ONE_SECOND =1000L
        private const val COUNTDOWN_TIME = 1000L
    }

}
