package com.furev.foodstick

import android.annotation.SuppressLint
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.furev.foodstick.FragmentRegisterDirections

import com.furev.foodstick.databinding.FragmentRegisterBinding
import com.furev.foodstick.util.ConnectionManager
import org.json.JSONException

import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class FragmentRegister : Fragment() {

    private val sharedPrefFile = "kotlinsharedpreference"
    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRegisterBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_register, container, false)

        (activity as MainActivity).showSetUpToolbar()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Register Yourself"


        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/register/fetch_result"



        binding.btnRegisterRegister.setOnClickListener {
            val sharedPreferences = context?.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
            val editor:SharedPreferences.Editor = (sharedPreferences?.edit() ?: this@FragmentRegister) as SharedPreferences.Editor

            if (ConnectionManager().checkConnectivity(activity as Context)){
            val name = binding.etRegisterName.text.toString()
            val mobile_number = binding.etRegisterPhone.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val address = binding.etRegisterAddress.text.toString()
            val email = binding.etRegisterEmail.text.toString()

            val jsonArrayObject: JSONObject = JSONObject()
            jsonArrayObject.put("name", "$name")
            jsonArrayObject.put("mobile_number", "${mobile_number}")
            jsonArrayObject.put("password", "$password")
            jsonArrayObject.put("address", "$address")
            jsonArrayObject.put("email", "$email")


            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonArrayObject,
                Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val successJsonObject =data.getBoolean("success")

                        if (successJsonObject) {
                            Toast.makeText(context, "Register Successful", Toast.LENGTH_SHORT)
                                .show()
                            val userData =data.getJSONObject("data")
                            findNavController().navigate(FragmentRegisterDirections.actionFragmentRegisterToLoginFragment())

                            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view?.windowToken, 0)
                        } else {
                            Toast.makeText(context, "Incorrect Details", Toast.LENGTH_SHORT).show()
                            //Toast.makeText(context, "${it.toString()}", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: JSONException){
                        Toast.makeText(activity as Context,"Unexpected Error",Toast.LENGTH_SHORT).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(context, "${it.toString()}", Toast.LENGTH_SHORT).show()
                    println("${it.toString()}")
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





        return binding.root
    }

}
