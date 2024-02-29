package com.internshala.foodrunner.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.internshala.foodrunner.R
import com.internshala.foodrunner.util.DrawerLocker

/**
 * A simple [Fragment] subclass.
 */
class FAQFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        (activity as DrawerLocker).setDrawerEnabled(true)
        return view
    }


}
