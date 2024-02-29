package com.internshala.foodrunner.model

import org.json.JSONArray

data class OrderDetails(
    val orderId: Int,
    val resName: String,
    val orderDate: String,
    val foodItem: JSONArray
)