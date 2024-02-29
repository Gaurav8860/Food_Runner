package com.internshala.foodrunner.model

import java.io.Serializable

data class Restaurants(
    val id: Int,
    val name: String,
    val rating: String,
    val costForTwo: Int,
    val imageUrl: String
)