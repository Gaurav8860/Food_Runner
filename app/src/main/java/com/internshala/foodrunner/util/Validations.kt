package com.internshala.foodrunner.util

import android.util.Patterns

/*Another utility class similar to Constants which is used to just peform some constant functions
* Here we can create different functions for performing different validations used in our app*/

object Validations {
    fun validateMobile(mobile: String): Boolean {
        return mobile.length == 10
    }

    fun validatePasswordLength(password: String): Boolean {
        return password.length >= 4
    }

    fun validateNameLength(name: String): Boolean {
        return name.length >= 3
    }

    fun matchPassword(pass: String, confirmPass: String): Boolean {
        return pass == confirmPass
    }

    fun validateEmail(email: String): Boolean {
        return (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }
}