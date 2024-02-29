package com.internshala.foodrunner.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.internshala.foodrunner.R
import com.internshala.foodrunner.util.SessionManager

class SplashActivity : AppCompatActivity() {

    /*Array of permissions to be used in the app*/
    val permissionString =
        arrayOf(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET)

    /*Variable for managing the session*/
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        /*Initialising the session*/
        sessionManager = SessionManager(this)

        /*this if else block checks whether the permissions were granted or not
        * If yes, then open the corresponding activity
        * else request for permissions*/
        if (!hasPermissions(this, permissionString)) {
            ActivityCompat.requestPermissions(this, permissionString, 101)
        } else {

            /*The handler delays the opening of the new activity thus displaying the logo for 2000 milliseconds i.e. 2 seconds*/
            Handler().postDelayed({
                openNewActivity()
            }, 2000)
        }

    }

    /*Function to check whether all the permission were granted or not
    * In our application we won't be needing it as Internet is an admin permission and would be granted automatically*/
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        var hasAllPermissions = true
        for (permission in permissions) {
            val res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED) {
                hasAllPermissions = false
            }
        }
        return hasAllPermissions
    }


    /*Function for opening the relevant activity
    * If the user was already logged in, then open the dashboard activity
    * else take the user to the login screen*/
    fun openNewActivity() {
        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /*Android's function to check whether the permissions were granted or not*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Handler().postDelayed({
                        openNewActivity()
                    }, 2000)
                } else {
                    Toast.makeText(
                        this,
                        "Please grant all permissions to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                    this.finish()
                }
                return
            }
            else -> {
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                this.finish()
                return
            }
        }
    }

    /*Lifecycle method. Here the finish() ensures that the activity does not open again when the user presses back button*/
    override fun onPause() {
        super.onPause()
        finish()
    }
}
