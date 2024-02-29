package com.internshala.foodrunner.fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.OrderHistoryAdapter
import com.internshala.foodrunner.model.OrderDetails
import com.internshala.foodrunner.util.DrawerLocker
import com.internshala.foodrunner.util.FETCH_PREVIOUS_ORDERS

/**
 * A simple [Fragment] subclass.
 */
class OrderHistoryFragment : Fragment() {

    private lateinit var recyclerOrderHistory: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private var orderHistoryList = ArrayList<OrderDetails>()
    private lateinit var llHasOrders: LinearLayout
    private lateinit var rlNoOrders: RelativeLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rlLoading: RelativeLayout
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        (activity as DrawerLocker).setDrawerEnabled(true)
        llHasOrders = view.findViewById(R.id.llHasOrders)
        rlNoOrders = view.findViewById(R.id.rlNoOrders)
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        rlLoading = view?.findViewById(R.id.rlLoading) as RelativeLayout
        rlLoading.visibility = View.VISIBLE
        sharedPreferences =
            (activity as Context).getSharedPreferences("FoodApp", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null) as String
        sendServerRequest(userId)
        return view
    }

    private fun sendServerRequest(userId: String) {
        val queue = Volley.newRequestQueue(activity as Context)

        val jsonObjectRequest = object :
            JsonObjectRequest(Method.GET, FETCH_PREVIOUS_ORDERS + userId, null, Response.Listener {
                rlLoading.visibility = View.GONE
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        if (resArray.length() == 0) {
                            llHasOrders.visibility = View.GONE
                            rlNoOrders.visibility = View.VISIBLE
                        } else {
                            for (i in 0 until resArray.length()) {
                                val orderObject = resArray.getJSONObject(i)
                                val foodItems = orderObject.getJSONArray("food_items")
                                val orderDetails = OrderDetails(
                                    orderObject.getInt("order_id"),
                                    orderObject.getString("restaurant_name"),
                                    orderObject.getString("order_placed_at"),
                                    foodItems
                                )
                                orderHistoryList.add(orderDetails)
                                if (orderHistoryList.isEmpty()) {
                                    llHasOrders.visibility = View.GONE
                                    rlNoOrders.visibility = View.VISIBLE
                                } else {
                                    llHasOrders.visibility = View.VISIBLE
                                    rlNoOrders.visibility = View.GONE
                                    if (activity != null) {
                                        orderHistoryAdapter = OrderHistoryAdapter(
                                            activity as Context,
                                            orderHistoryList
                                        )
                                        val mLayoutManager =
                                            LinearLayoutManager(activity as Context)
                                        recyclerOrderHistory.layoutManager = mLayoutManager
                                        recyclerOrderHistory.itemAnimator = DefaultItemAnimator()
                                        recyclerOrderHistory.adapter = orderHistoryAdapter
                                    } else {
                                        queue.cancelAll(this::class.java.simpleName)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"

                /*The below used token will not work, kindly use the token provided to you in the training*/
                headers["token"] = "9bf534118365f1"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}
