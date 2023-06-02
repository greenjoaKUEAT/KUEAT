package com.example.kueat.ui.home

import android.graphics.Rect
import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.example.kueat.R
import com.example.kueat.databinding.FragmentRestaurantBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlin.math.abs

class RestaurantFragment : Fragment(),TabLayout.OnTabSelectedListener,View.OnScrollChangeListener{
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
            tablayout.addOnTabSelectedListener(this@RestaurantFragment)
            scrollView.setOnScrollChangeListener(this@RestaurantFragment)
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

    override fun onTabSelected(tab: TabLayout.Tab?) {

        when (tab?.position) {
            0 -> {
                binding.scrollView.scrollToView(binding.textRest)
            }
            1 -> {
                binding.scrollView.scrollToView(binding.textMenu)
            }
            2 -> {
                binding.scrollView.scrollToView(binding.textReview)
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        //do nothing
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        //do nothing
    }

    override fun onScrollChange(
        v: View?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
       // scroll 이동 시 tab 전환 구현

    }

    fun ScrollView.computeDistanceToView(view: View): Int {
        return abs(calculateRectOnScreen(this).top - (this.scrollY + calculateRectOnScreen(view).top))
    }

    fun calculateRectOnScreen(view: View): Rect {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return Rect(
            location[0],
            location[1],
            location[0] + view.measuredWidth,
            location[1] + view.measuredHeight
        )
    }
    fun ScrollView.scrollToView(view: View) {
        val y = computeDistanceToView(view)
        this.scrollTo(0, y)
    }
}