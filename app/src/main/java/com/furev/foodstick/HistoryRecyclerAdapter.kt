package com.furev.foodstick

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furev.foodstick.dataClasses.OrderHistory

class HistoryRecyclerAdapter(val context: Context, val historylist:ArrayList<OrderHistory> ) :RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_order_history,parent,false)
        return HistoryHolder(view)
    }



    override fun getItemCount(): Int {
        return historylist.size
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        val history = historylist[position]
        val dishes =history.dishes
        holder.rest_name.text=history.rest_name
        holder.rest_date.text=history.date
        val recyclerAdapter = DishesAdapter(context,historylist)
        val recyclerView = holder.recyclerView
        val layoutManager = LinearLayoutManager(context)
        recyclerView.adapter=recyclerAdapter
        recyclerView.layoutManager=layoutManager

    }

    class HistoryHolder(view: View):RecyclerView.ViewHolder(view){
        val rest_name:TextView = view.findViewById(R.id.history_Rest_name)
        val rest_date:TextView = view.findViewById(R.id.history_Rest_date)
        val recyclerView:RecyclerView=view.findViewById(R.id.recycler_restaurents_history)

    }
}
class DishesAdapter(val context: Context,val historylist:ArrayList<OrderHistory>):RecyclerView.Adapter<DishesAdapter.DishHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.row_dishes,parent,false)
        return DishHolder(view)

    }

    override fun getItemCount(): Int {
        return historylist.size
    }

    override fun onBindViewHolder(holder: DishHolder, position: Int) {
        val history = historylist[position]
        val dishes =history.dishes
        for (i in dishes){
            holder.dish_name.text= i.name
            holder.dish_price.text= i.cost
        }
    }
    class DishHolder(view:View):RecyclerView.ViewHolder(view){
        val dish_name:TextView = view.findViewById(R.id.history_dish_name)
        val dish_price:TextView = view.findViewById(R.id.history_dish_price)

    }
}