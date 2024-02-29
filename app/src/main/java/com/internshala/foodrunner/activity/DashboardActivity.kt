package com.internshala.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.RestaurantMenuAdapter
import com.internshala.foodrunner.fragment.*
import com.internshala.foodrunner.fragment.RestaurantFragment.Companion.resId
import com.internshala.foodrunner.util.DrawerLocker
import com.internshala.foodrunner.util.SessionManager

class DashboardActivity : AppCompatActivity(), DrawerLocker {

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled)
            DrawerLayout.LOCK_MODE_UNLOCKED
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED

        drawerLayout.setDrawerLockMode(lockMode)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = enabled
    }

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var previousMenuItem: MenuItem? = null
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var sessionManager: SessionManager
    private lateinit var sharedPrefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this@DashboardActivity)
        sharedPrefs = this@DashboardActivity.getSharedPreferences(
            sessionManager.PREF_NAME,
            sessionManager.PRIVATE_MODE
        )

        /*Created by us to handle the initialisation of variables*/
        init()

        /*This method is also user created to setup the toolbar*/
        setupToolbar()

        /*User created method to handle the action bar drawer toogle*/
        setupActionBarToggle()

        /*This is method is created to display the home fragment inside the activity by default*/
        displayHome()

        /*Below we handle the click listeners of the menu items inside the navigation drawer*/
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->

            /*Unchecking the previous menu item when a new item is clicked*/
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            /*Highlighting the new menu item, the one which is clicked*/
            item.isCheckable = true
            item.isChecked = true

            /*This sets the value of previous menu item as the current one*/
            previousMenuItem = item


            /*The closing of navigation drawer is delayed to make the transition smooth
            * We delay it by 0.1 second*/
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 100)

            /*The fragment transaction takes care of the different fragments which will be opened and closed*/
            val fragmentTransaction = supportFragmentManager.beginTransaction()

            /*Getting the id of the clicked item to identify which fragment to display*/
            when (item.itemId) {

                /*Opening the home fragment*/
                R.id.home -> {
                    val homeFragment = HomeFragment()
                    fragmentTransaction.replace(R.id.frame, homeFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "All Restaurants"
                }

                /*Opening the profile fragment*/
                R.id.myProfile -> {
                    val profileFragment = ProfileFragment()
                    fragmentTransaction.replace(R.id.frame, profileFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "My profile"
                }

                /*Opening the Order history fragment*/
                R.id.order_history -> {
                    val orderHistoryFragment = OrderHistoryFragment()
                    fragmentTransaction.replace(R.id.frame, orderHistoryFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "My Previous Orders"
                }

                /*Opening the favorites fragment*/
                R.id.favRes -> {
                    val favFragment = FavouritesFragment()
                    fragmentTransaction.replace(R.id.frame, favFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "Favorite Restaurants"
                }

                /*Opening the frequently asked questions i.e. FAQ fragment*/
                R.id.faqs -> {
                    val faqFragment = FAQFragment()
                    fragmentTransaction.replace(R.id.frame, faqFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                }

                /*Exiting the application*/
                R.id.logout -> {

                    /*Creating a confirmation dialog*/
                    val builder = AlertDialog.Builder(this@DashboardActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            sessionManager.setLogin(false)
                            sharedPrefs.edit().clear().apply()
                            startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                            Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                            ActivityCompat.finishAffinity(this)
                        }
                        .setNegativeButton("No") { _, _ ->
                            displayHome()
                        }
                        .create()
                        .show()

                }

            }
            return@setNavigationItemSelectedListener true
        }

        /*This is how we can add header to the Navigation drawer using Kotlin.
        * Using the XML file to add the drawer header is also correct
        * However, we wanted to show you another and lengthy way of doing it.
        * The method of using XML to add drawer layout is a better way*/
        val convertView = LayoutInflater.from(this@DashboardActivity).inflate(R.layout.drawer_header, null)
        val userName: TextView = convertView.findViewById(R.id.txtDrawerText)
        val userPhone: TextView = convertView.findViewById(R.id.txtDrawerSecondaryText)
        val appIcon: ImageView = convertView.findViewById(R.id.imgDrawerImage)
        userName.text = sharedPrefs.getString("user_name", null)
        val phoneText = "+91-${sharedPrefs.getString("user_mobile_number", null)}"
        userPhone.text = phoneText
        navigationView.addHeaderView(convertView)


        /*Here we have also added clicks to the views present inside the navigation drawer*/
        userName.setOnClickListener {
            val profileFragment = ProfileFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, profileFragment)
            transaction.commit()
            supportActionBar?.title = "My profile"
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 50)
        }

        appIcon.setOnClickListener {
            val profileFragment = ProfileFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, profileFragment)
            transaction.commit()
            supportActionBar?.title = "My profile"
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 50)
        }

    }

    /*Since, there are a lot of ways from which Home fragment will open therefore it is better to make a
    * separate method for it.*/
    private fun displayHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    private fun setupActionBarToggle() {
        actionBarDrawerToggle = object :
            ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                val pendingRunnable = Runnable {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }

                /*delaying the closing of the navigation drawer for that the motion looks smooth*/
                Handler().postDelayed(pendingRunnable, 50)
            }
        }
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        toolbar.setTitleTextAppearance(this, R.style.PoppinsTextAppearance)
    }

    /*Initialising the views*/
    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
    }


    /*Setting up the opening of navigation drawer*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val f = supportFragmentManager.findFragmentById(R.id.frame)
        when (id) {
            android.R.id.home -> {
                if (f is RestaurantFragment) {
                    onBackPressed()
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /*Adding custom routes from different fragments when we press the back button*/
    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.frame)
        when (f) {
            is HomeFragment -> {
                Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                super.onBackPressed()
            }
            is RestaurantFragment -> {
                if (!RestaurantMenuAdapter.isCartEmpty) {
                    val builder = AlertDialog.Builder(this@DashboardActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Going back will reset cart items. Do you still want to proceed?")
                        .setPositiveButton("Yes") { _, _ ->
                            val clearCart =
                                CartActivity.ClearDBAsync(applicationContext, resId.toString()).execute().get()
                            displayHome()
                            RestaurantMenuAdapter.isCartEmpty = true
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()
                        .show()
                } else {
                    displayHome()
                }
            }
            else -> displayHome()
        }
    }

}
