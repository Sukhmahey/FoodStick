package com.furev.foodstick

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.furev.foodstick.FragmentDoneDirections

import com.furev.foodstick.R
import com.furev.foodstick.databinding.FragmentDoneBinding


class FragmentDone : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding:FragmentDoneBinding=DataBindingUtil.inflate(inflater,
            R.layout.fragment_done, container, false)

        binding.btnDone.setOnClickListener {
            this.findNavController().navigate(FragmentDoneDirections.actionFragmentDoneToFragmentRestaurants())
        }
        return binding.root
    }


}