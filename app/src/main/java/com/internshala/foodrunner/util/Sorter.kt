package com.internshala.foodrunner.util

import com.internshala.foodrunner.model.Restaurants

class Sorter {
    companion object {
        var costComparator = Comparator<Restaurants> { res1, res2 ->
            val costOne = res1.costForTwo
            val costTwo = res2.costForTwo
            if (costOne.compareTo(costTwo) == 0) {
                ratingComparator.compare(res1, res2)
            } else {
                costOne.compareTo(costTwo)
            }
        }

        var ratingComparator = Comparator<Restaurants> { res1, res2 ->
            val ratingOne = res1.rating
            val ratingTwo = res2.rating
            if (ratingOne.compareTo(ratingTwo) == 0) {
                val costOne = res1.costForTwo
                val costTwo = res2.costForTwo
                costOne.compareTo(costTwo)
            } else {
                ratingOne.compareTo(ratingTwo)
            }
        }
    }

}