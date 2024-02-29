package com.internshala.foodrunner.util

/*This is a new type of file.
* In order to put all the links in one place we can create a new constants file
* In this file we do not create a class as it takes up more spac. Further, we would not have any special methods in it.
* Hence in Kotlin we can create a file directly and place some constant values in it.
* */


const val NETWORK_IP = "http://13.235.250.119/v2"
const val REGISTER = "$NETWORK_IP/register/fetch_result"
const val LOGIN = "$NETWORK_IP/login/fetch_result"
const val FORGOT_PASSWORD = "$NETWORK_IP/forgot_password/fetch_result"
const val RESET_PASSWORD = "$NETWORK_IP/reset_password/fetch_result"
const val FETCH_RESTAURANTS = "$NETWORK_IP/restaurants/fetch_result/"
const val PLACE_ORDER = "$NETWORK_IP/place_order/fetch_result"
const val FETCH_PREVIOUS_ORDERS = "$NETWORK_IP/orders/fetch_result/"
