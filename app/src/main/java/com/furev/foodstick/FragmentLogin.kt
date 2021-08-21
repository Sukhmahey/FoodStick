package com.furev.foodstick

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.furev.foodstick.FragmentLoginDirections
import com.furev.foodstick.databinding.FragmentLoginBinding
import com.furev.foodstick.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class FragmentLogin : Fragment() {
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentLoginBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_login, container, false)
        binding.btnLoginLogin.setOnClickListener {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/login/fetch_result"

            val sharedPreferences =
                context?.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = (sharedPreferences?.edit()?:this@FragmentLogin) as SharedPreferences.Editor

            if (ConnectionManager().checkConnectivity(activity as Context)) {

                val jsonObject = JSONObject()
                jsonObject.put("mobile_number", binding.etLoginPhone.text.toString())
                jsonObject.put("password", binding.etLoginPassword.text.toString())

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
                    url,
                    jsonObject,
                    Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val successJsonObject = data.getBoolean("success")

                            if (successJsonObject) {
                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT)
                                    .show()
                                val userData = data.getJSONObject("data")
                                editor.putString("user_id", userData.getString("user_id"))
                                editor.putString("name", userData.getString("name"))
                                editor.putString("email", userData.getString("email"))
                                editor.putString(
                                    "password",
                                    binding.etLoginPassword.text.toString()
                                )
                                editor.putString(
                                    "mobile_number",
                                    userData.getString("mobile_number")
                                )
                                editor.putString("address", userData.getString("address"))
                                editor.commit()

                                (activity as MainActivity).changeheader()

                                findNavController().navigate(FragmentLoginDirections.actionLoginFragmentToFragmentRestaurants())
                                val imm =
                                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                            }
                             else {
                                Toast.makeText(context, "Incorrect Details or User Already Exist ", Toast.LENGTH_SHORT).show()

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
            }


        binding.btnLoginSignUp.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_fragmentRegister)

        }
        binding.txtLoginForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_fragmentFPassword1)

        }


        return binding.root
    }

}
