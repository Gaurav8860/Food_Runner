package com.internshala.foodrunner.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.util.ConnectionManager
import com.internshala.foodrunner.util.REGISTER
import com.internshala.foodrunner.util.SessionManager
import com.internshala.foodrunner.util.Validations
import org.json.JSONObject
import java.lang.Exception

/* The registration activity is responsible for registering the users to the app
* This will send the fields to server and the user will get registered if all the fields were correct.
* The user receives response in the form of JSON
* If the login is true, the user is navigated to the dashboard else appropriate error message is displayed*/

class RegisterActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var btnRegister: Button
    lateinit var etName: EditText
    lateinit var etPhoneNumber: EditText
    lateinit var etPassword: EditText
    lateinit var etEmail: EditText
    lateinit var etAddress: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var progressBar: ProgressBar
    lateinit var rlRegister: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextAppearance(this, R.style.PoppinsTextAppearance)
        sessionManager = SessionManager(this@RegisterActivity)
        sharedPreferences = this@RegisterActivity.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE)
        rlRegister = findViewById(R.id.rlRegister)
        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etAddress = findViewById(R.id.etAddress)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        rlRegister.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE


        btnRegister.setOnClickListener {
            rlRegister.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            if (Validations.validateNameLength(etName.text.toString())) {
                etName.error = null
                if (Validations.validateEmail(etEmail.text.toString())) {
                    etEmail.error = null
                    if (Validations.validateMobile(etPhoneNumber.text.toString())) {
                        etPhoneNumber.error = null
                        if (Validations.validatePasswordLength(etPassword.text.toString())) {
                            etPassword.error = null
                            if (Validations.matchPassword(
                                    etPassword.text.toString(),
                                    etConfirmPassword.text.toString()
                                )
                            ) {
                                etPassword.error = null
                                etConfirmPassword.error = null
                                if (ConnectionManager().isNetworkAvailable(this@RegisterActivity)) {
                                    sendRegisterRequest(
                                        etName.text.toString(),
                                        etPhoneNumber.text.toString(),
                                        etAddress.text.toString(),
                                        etPassword.text.toString(),
                                        etEmail.text.toString()
                                    )
                                } else {
                                    rlRegister.visibility = View.VISIBLE
                                    progressBar.visibility = View.INVISIBLE
                                    Toast.makeText(this@RegisterActivity, "No Internet Connection", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                rlRegister.visibility = View.VISIBLE
                                progressBar.visibility = View.INVISIBLE
                                etPassword.error = "Passwords don't match"
                                etConfirmPassword.error = "Passwords don't match"
                                Toast.makeText(this@RegisterActivity, "Passwords don't match", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            rlRegister.visibility = View.VISIBLE
                            progressBar.visibility = View.INVISIBLE
                            etPassword.error = "Password should be more than or equal 4 digits"
                            Toast.makeText(
                                this@RegisterActivity,
                                "Password should be more than or equal 4 digits",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        rlRegister.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                        etPhoneNumber.error = "Invalid Mobile number"
                        Toast.makeText(this@RegisterActivity, "Invalid Mobile number", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    rlRegister.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    etEmail.error = "Invalid Email"
                    Toast.makeText(this@RegisterActivity, "Invalid Email", Toast.LENGTH_SHORT).show()
                }
            } else {
                rlRegister.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                etName.error = "Invalid Name"
                Toast.makeText(this@RegisterActivity, "Invalid Name", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendRegisterRequest(name: String, phone: String, address: String, password: String, email: String) {

        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("mobile_number", phone)
        jsonParams.put("password", password)
        jsonParams.put("address", address)
        jsonParams.put("email", email)

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            REGISTER,
            jsonParams,
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
                                this@RegisterActivity,
                                DashboardActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        rlRegister.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                        val errorMessage = data.getString("errorMessage")
                        Toast.makeText(
                            this@RegisterActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception){
                    rlRegister.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()
                rlRegister.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
            }
        ){
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

    override fun onSupportNavigateUp(): Boolean {
        Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
        onBackPressed()
        return true
    }
}
