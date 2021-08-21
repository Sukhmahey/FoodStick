package com.furev.foodstick.dataClasses

import com.furev.foodstick.dataClasses.Dishes

data class OrderHistory (
    val order_id:String,
    val rest_name:String,
    val total_cost:String,
    val date:String,
    val dishes:ArrayList<Dishes>
)