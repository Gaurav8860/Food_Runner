package com.internshala.foodrunner.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.util.ConnectionManager
import com.internshala.foodrunner.util.LOGIN
import com.internshala.foodrunner.util.SessionManager
import com.internshala.foodrunner.util.Validations
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    /*Declaring all the views present in the activity_login.xml file*/
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegisterYourself: TextView

    /*Variables used in managing the login session*/
    lateinit var sessionManager: SessionManager
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*Initialising the views*/
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegisterYourself = findViewById(R.id.txtRegisterYourself)

        /*Initialising the session variables*/
        sessionManager = SessionManager(this)
        sharedPreferences =
            this.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE)

        /*Clicking on this text takes you to the forgot password activity*/
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        /*Clicking on this text takes you to the forgot password activity*/
        txtRegisterYourself.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        /*Start the login process when the user clicks on the login button*/
        btnLogin.setOnClickListener {

            /*Hide the login button when the process is going on*/
            btnLogin.visibility = View.INVISIBLE

            /*First validate the mobile number and password length*/
            if (Validations.validateMobile(etMobileNumber.text.toString()) && Validations.validatePasswordLength(etPassword.text.toString())) {
                if (ConnectionManager().isNetworkAvailable(this@LoginActivity)) {

                    /*Create the queue for the request*/
                    val queue = Volley.newRequestQueue(this@LoginActivity)

                    /*Create the JSON parameters to be sent during the login process*/
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etMobileNumber.text.toString())
                    jsonParams.put("password", etPassword.text.toString())


                    /*Finally send the json object request*/
                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST, LOGIN, jsonParams,
                        Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val response = data.getJSONObject("data")
                                    sharedPreferences.edit()
                                        .putString("user_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", response.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "user_mobile_number",
                                            response.getString("mobile_number")
                                        )
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_address", response.getString("address"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_email", response.getString("email")).apply()
                                    sessionManager.setLogin(true)
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            DashboardActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    btnLogin.visibility = View.VISIBLE
                                    txtForgotPassword.visibility = View.VISIBLE
                                    btnLogin.visibility = View.VISIBLE
                                    val errorMessage = data.getString("errorMessage")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                btnLogin.visibility = View.VISIBLE
                                txtForgotPassword.visibility = View.VISIBLE
                                txtRegisterYourself.visibility = View.VISIBLE
                                e.printStackTrace()
                            }
                        },
                        Response.ErrorListener {
                            btnLogin.visibility = View.VISIBLE
                            txtForgotPassword.visibility = View.VISIBLE
                            txtRegisterYourself.visibility = View.VISIBLE
                            Log.e("Error::::", "/post request fail! Error: ${it.message}")
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
                    btnLogin.visibility = View.VISIBLE
                    txtForgotPassword.visibility = View.VISIBLE
                    txtRegisterYourself.visibility = View.VISIBLE
                    Toast.makeText(this@LoginActivity, "No internet Connection", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                btnLogin.visibility = View.VISIBLE
                txtForgotPassword.visibility = View.VISIBLE
                txtRegisterYourself.visibility = View.VISIBLE
                Toast.makeText(this@LoginActivity, "Invalid Phone or Password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }
}
