package com.internshala.foodrunner.fragment


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.internshala.foodrunner.R
import com.internshala.foodrunner.activity.CartActivity
import com.internshala.foodrunner.adapter.RestaurantMenuAdapter
import com.internshala.foodrunner.database.OrderEntity
import com.internshala.foodrunner.database.RestaurantDatabase
import com.internshala.foodrunner.model.FoodItem
import com.internshala.foodrunner.util.ConnectionManager
import com.internshala.foodrunner.util.DrawerLocker
import com.internshala.foodrunner.util.FETCH_RESTAURANTS

/**
 * A simple [Fragment] subclass.
 */
class RestaurantFragment : Fragment() {

    private lateinit var recyclerMenu: RecyclerView
    private lateinit var restaurantMenuAdapter: RestaurantMenuAdapter
    private var menuList = arrayListOf<FoodItem>()
    private lateinit var rlLoading: RelativeLayout
    private var orderList = arrayListOf<FoodItem>()
    lateinit var sharedPreferences: SharedPreferences

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var goToCart: Button
        var resId: Int? = 0
        var resName: String? = ""
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)
        sharedPreferences =
            activity?.getSharedPreferences("FoodApp", Context.MODE_PRIVATE) as SharedPreferences
        rlLoading = view?.findViewById(R.id.rlLoading) as RelativeLayout
        rlLoading.visibility = View.VISIBLE
        resId = arguments?.getInt("id", 0)
        resName = arguments?.getString("name", "")
        (activity as DrawerLocker).setDrawerEnabled(false)
        setHasOptionsMenu(true)
        goToCart = view.findViewById(R.id.btnGoToCart) as Button
        goToCart.visibility = View.GONE
        goToCart.setOnClickListener {
            proceedToCart()
        }
        setUpRestaurantMenu(view)
        return view
    }


    private fun setUpRestaurantMenu(view: View) {

        recyclerMenu = view.findViewById(R.id.recyclerMenuItems)
        if (ConnectionManager().isNetworkAvailable(activity as Context)) {

            val queue = Volley.newRequestQueue(activity as Context)

            val jsonObjectRequest = object :
                JsonObjectRequest(Method.GET, FETCH_RESTAURANTS + resId, null, Response.Listener {
                    rlLoading.visibility = View.GONE

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val menuObject = resArray.getJSONObject(i)
                                val foodItem = FoodItem(
                                    menuObject.getString("id"),
                                    menuObject.getString("name"),
                                    menuObject.getString("cost_for_one").toInt()
                                )
                                menuList.add(foodItem)
                                restaurantMenuAdapter = RestaurantMenuAdapter(
                                    activity as Context,
                                    menuList,
                                    object : RestaurantMenuAdapter.OnItemClickListener {
                                        override fun onAddItemClick(foodItem: FoodItem) {
                                            orderList.add(foodItem)
                                            if (orderList.size > 0) {
                                                goToCart.visibility = View.VISIBLE
                                                RestaurantMenuAdapter.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveItemClick(foodItem: FoodItem) {
                                            orderList.remove(foodItem)
                                            if (orderList.isEmpty()) {
                                                goToCart.visibility = View.GONE
                                                RestaurantMenuAdapter.isCartEmpty = true
                                            }
                                        }
                                    })
                                val mLayoutManager = LinearLayoutManager(activity)
                                recyclerMenu.layoutManager = mLayoutManager
                                recyclerMenu.itemAnimator = DefaultItemAnimator()
                                recyclerMenu.adapter = restaurantMenuAdapter
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
        } else {
            Toast.makeText(activity as Context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }


    private fun proceedToCart() {

        /*Here we see the implementation of Gson.
        * Whenever we want to convert the custom data types into simple data types
        * which can be transferred across for utility purposes, we will use Gson*/
        val gson = Gson()

        /*With the below code, we convert the list of order items into simple string which can be easily stored in DB*/
        val foodItems = gson.toJson(orderList)

        val async = ItemsOfCart(activity as Context, resId.toString(), foodItems, 1).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putInt("resId", resId as Int)
            data.putString("resName", resName)
            val intent = Intent(activity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        } else {
            Toast.makeText((activity as Context), "Some unexpected error", Toast.LENGTH_SHORT)
                .show()
        }

    }


    class ItemsOfCart(
        context: Context,
        val restaurantId: String,
        val foodItems: String,
        val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()


        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }

            return false
        }

    }

}
