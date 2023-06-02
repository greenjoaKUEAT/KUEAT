package com.example.kueat.ui.home

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kueat.R
import com.example.kueat.databinding.FragmentRestaurantBinding
import com.google.android.material.tabs.TabLayout

class RestaurantFragment : Fragment() {
    lateinit var binding:FragmentRestaurantBinding
    lateinit var tab1 : TabLayout.Tab
    lateinit var tab2 : TabLayout.Tab
    lateinit var tab3 : TabLayout.Tab
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        initTab()
        binding.apply {

        }

    }

    private fun initTab() {
        binding.apply {
            tab1 = tablayout.newTab()
            tab2 = tablayout.newTab()
            tab3 = tablayout.newTab()
            tab1.text = "정보"
            tab2.text = "메뉴"
            tab3.text = "리뷰"
            tablayout.addTab(tab1)
            tablayout.addTab(tab2)
            tablayout.addTab(tab3)
        }
    }

}