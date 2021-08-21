package com.furev.foodstick

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.furev.foodstick.R
import com.furev.foodstick.databinding.FragmentProfileBinding

/**
 * A simple [Fragment] subclass.
 */
class FragmentProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding:FragmentProfileBinding=DataBindingUtil.inflate(inflater,
            R.layout.fragment_profile, container, false)
        val sharedPreferences =context?.getSharedPreferences("kotlinsharedpreference",Context.MODE_PRIVATE)
        val editor=sharedPreferences?.edit() as SharedPreferences.Editor
        binding.txtProfileName.text=sharedPreferences.getString("name","Name")
        binding.txtProfileMobile.text=sharedPreferences.getString("mobile_number","Mobile Number")
        binding.txtProfileEmail.text=sharedPreferences.getString("email","Email")
        binding.txtProfileAddress.text=sharedPreferences.getString("address","Email")
        return binding.root
    }

}
