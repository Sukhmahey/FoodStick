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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.furev.foodstick.FragmentFPassword2Args
import com.furev.foodstick.FragmentFPassword2Directions

import com.furev.foodstick.databinding.FragmentFPassword2Binding
import com.furev.foodstick.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class FragmentFPassword2 : Fragment() {
    private lateinit var args: FragmentFPassword2Args

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentFPassword2Binding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_f_password2, container, false)


        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

        val sharedPreferences =
            context?.getSharedPreferences("kotlinsharedpreference", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =
            (sharedPreferences?.edit() ?: this@FragmentFPassword2) as SharedPreferences.Editor

        arguments?.let {
            args=
                FragmentFPassword2Args.fromBundle(
                    it
                )

        }


        binding.btnRegister2Submit.setOnClickListener {
            if (binding.etRegister2NewPass.text.toString() == binding.etRegister2ConfirmPass.text.toString()){
            if (ConnectionManager().checkConnectivity(activity as Context)) {
                val jsonObject: JSONObject = JSONObject()
                jsonObject.put("mobile_number", args.mobileNumber)
                jsonObject.put("password", binding.etRegister2NewPass.text.toString())
                jsonObject.put("otp", binding.etRegister2Otp.text.toString())

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
                    url,
                    jsonObject,
                    Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                findNavController().navigate(FragmentFPassword2Directions.actionFragmentFPassword2ToLoginFragment())
                                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                               editor.clear()
                                editor.commit()
                            } else {
                                Toast.makeText(context, "Incorrect details", Toast.LENGTH_SHORT)
                                    .show()
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
        }else{
                Toast.makeText(context,"Password and Confirm Password are not same",Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

}
