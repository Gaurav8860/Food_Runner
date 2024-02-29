package com.internshala.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.model.FoodItem
import com.internshala.foodrunner.model.OrderDetails
import java.text.SimpleDateFormat
import java.util.*

class OrderHistoryAdapter(
    val context: Context,
    private val orderHistoryList: ArrayList<OrderDetails>
) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderHistoryViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.order_history_custom_row, p0, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    override fun onBindViewHolder(p0: OrderHistoryViewHolder, p1: Int) {
        val orderHistoryObject = orderHistoryList[p1]
        p0.txtResName.text = orderHistoryObject.resName
        p0.txtDate.text = formatDate(orderHistoryObject.orderDate)
        setUpRecycler(p0.recyclerResHistory, orderHistoryObject)
    }

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResName: TextView = view.findViewById(R.id.txtResHistoryResName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerResHistory: RecyclerView = view.findViewById(R.id.recyclerResHistoryItems)
    }

    private fun setUpRecycler(recyclerResHistory: RecyclerView, orderHistoryList: OrderDetails) {
        val foodItemsList = ArrayList<FoodItem>()
        for (i in 0 until orderHistoryList.foodItem.length()) {
            val foodJson = orderHistoryList.foodItem.getJSONObject(i)
            foodItemsList.add(
                FoodItem(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost").toInt()
                )
            )
        }
        val cartItemAdapter = CartItemAdapter(foodItemsList, context)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerResHistory.layoutManager = mLayoutManager
        recyclerResHistory.itemAnimator = DefaultItemAnimator()
        recyclerResHistory.adapter = cartItemAdapter
    }

    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }
}
