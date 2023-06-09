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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.R
import com.example.kueat.databinding.FragmentRestaurantBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.abs

class RestaurantFragment : Fragment(),TabLayout.OnTabSelectedListener,View.OnScrollChangeListener{
    lateinit var binding:FragmentRestaurantBinding
    lateinit var tab1 : TabLayout.Tab
    lateinit var tab2 : TabLayout.Tab
    lateinit var tab3 : TabLayout.Tab
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter :MyReviewAdapter
    lateinit var rdb : DatabaseReference
    // 임시 intent
    var rest_id = 1;
    var tabUser = true
    var scrollUser = true
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
        val fragment = childFragmentManager.beginTransaction()
        val naverfragment = NaverFragment()
        val bundle  = Bundle()

        bundle.putString("rest_id",rest_id.toString())
        naverfragment.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.map_frame,naverfragment).commit()
        binding.apply {
            tablayout.addOnTabSelectedListener(this@RestaurantFragment)
            scrollView.setOnScrollChangeListener(this@RestaurantFragment)
        }
        layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        rdb = Firebase.database.getReference("KueatDB/Article")
        val query = rdb.limitToLast(50)
        val option = FirebaseRecyclerOptions.Builder<Review>()
            .setQuery(query,Review::class.java).build()
        adapter = MyReviewAdapter(option)
        adapter.itemClickListener = object :MyReviewAdapter.OnItemClickListener{
            override fun OnItemClick(pos: Int) {
                // Review 창으로 이동, 이동하면서 review id 넘기고 댓글 받아와야함

            }
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
        if(tabUser) {
            scrollUser = false
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
            scrollUser = true
        }

        // do nothing
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
        if(scrollUser) {
            tabUser = false
            if (calculateRectOnScreen(binding.scrollView).top >= calculateRectOnScreen(binding.textRest).top) {
                binding.tablayout.selectTab(tab1)
            }
            if (calculateRectOnScreen(binding.scrollView).top >= calculateRectOnScreen(binding.textMenu).top) {
                binding.tablayout.selectTab(tab2)
            }
            if(calculateRectOnScreen(binding.scrollView).top >= calculateRectOnScreen(binding.textReview).top){
                binding.tablayout.selectTab(tab3)
            }

            tabUser = true
        }
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