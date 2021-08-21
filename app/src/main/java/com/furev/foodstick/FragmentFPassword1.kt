package com.furev.foodstick

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.furev.foodstick.FragmentFPassword1Directions

import com.furev.foodstick.databinding.FragmentFPassword1Binding
import com.furev.foodstick.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class FragmentFPassword1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentFPassword1Binding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_f_password1, container, false)
        (activity as MainActivity).hideSetUpToolbar()

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        binding.btnFPassNext.setOnClickListener {

            if (ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObject: JSONObject = JSONObject()
            jsonObject.put("mobile_number", binding.etFPassPhone.text.toString())
            jsonObject.put("email", binding.etFpassEmail.text.toString())

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                Response.Listener {
                    try {
                        val successObject = it.getJSONObject("data")
                        val success = successObject.getBoolean("success")
                        if (success) {
                            val firstTry = successObject.getBoolean("first_try")
                             if (firstTry) {
                                Toast.makeText(context,"Confirm from Email Address",Toast.LENGTH_SHORT).show()
                                 val action =
                                     FragmentFPassword1Directions.actionFragmentFPassword1ToFragmentFPassword2(
                                         binding.etFPassPhone.text.toString()
                                     )
                                 findNavController().navigate(action)

                            }
                            else{
                                 val action =
                                     FragmentFPassword1Directions.actionFragmentFPassword1ToFragmentFPassword2(
                                         binding.etFPassPhone.text.toString()
                                     )
                                 findNavController().navigate(action)
                                Toast.makeText(context,"Password Can't change more than once",Toast.LENGTH_SHORT).show()
                            }

                        }
                        else {
                            Toast.makeText(
                                context,
                                "Incorrect Details",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                        catch (e: JSONException) {
                        Toast.makeText(context, "${e.toString()}", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show()
                }
            ) {
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
                dialog.setPositiveButton("Open Setting"){ text,listner ->
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

        return binding.root


    }
}
