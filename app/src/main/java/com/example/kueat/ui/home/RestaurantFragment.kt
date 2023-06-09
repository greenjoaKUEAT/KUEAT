package com.example.kueat.ui.home

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.R
import com.example.kueat.databinding.FragmentRestaurantBinding
import com.example.kueat.`object`.Menu
import com.example.kueat.`object`.Review
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.abs

class RestaurantFragment : Fragment(),TabLayout.OnTabSelectedListener,View.OnScrollChangeListener{
    var binding:FragmentRestaurantBinding ?=null
    lateinit var tab1 : TabLayout.Tab
    lateinit var tab2 : TabLayout.Tab
    lateinit var tab3 : TabLayout.Tab
    lateinit var MenulayoutManager: LinearLayoutManager
    lateinit var ReviewlayoutManager: LinearLayoutManager
    lateinit var reviewadapter :MyReviewAdapter
    lateinit var menuadapter: MyMenuAdapter
    lateinit var dbMenu : DatabaseReference
    lateinit var dbReview : DatabaseReference
    lateinit var dbULike : DatabaseReference
    val user = Firebase.auth.currentUser
    // 임시 intent
    var rest_id = 1;
    var tabUser = true
    var scrollUser = true
    var isLiked = false
    var likedid:String = ""
    lateinit var item :Task<DataSnapshot>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        initTab()
//        val fragment = childFragmentManager.beginTransaction()
        val naverfragment = NaverFragment()
        val bundle  = Bundle()
        bundle.putString("rest_id",rest_id.toString())
        naverfragment.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.map_frame,naverfragment).commit()

        dbULike = Firebase.database.getReference("KueatDB/LikedRestaurant")
        val key = rest_id.toString() + user!!.uid
        dbULike.child(key).get().addOnSuccessListener {
            binding!!.likebtn.setImageResource(R.drawable.tap_like_filled)
            isLiked = true
        }

        binding!!.apply {
            tablayout.addOnTabSelectedListener(this@RestaurantFragment)
            scrollView.setOnScrollChangeListener(this@RestaurantFragment)
            likebtn.setOnClickListener {
                if(isLiked){ // 좋아요 취소
                    dbULike.child(key).removeValue()
                    binding!!.likebtn.setImageResource(R.drawable.tap_like_cpy)
                    isLiked = false
                }else {// 좋아요
                    dbULike.child(key).push().setValue(Likedrest(rest_id.toString(),user!!.uid,key))
                    binding!!.likebtn.setImageResource(R.drawable.tap_like_filled)
                    isLiked = true
                }
            }
        }
        /*메뉴*/
        MenulayoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        dbMenu = Firebase.database.getReference("KueatDB/Menu")
        var query = dbMenu.orderByChild("restaurant_id").equalTo(rest_id.toString())
        val optionMenu = FirebaseRecyclerOptions.Builder<Menu>()
            .setQuery(query, Menu::class.java).build()
        menuadapter = MyMenuAdapter(optionMenu)

        /*리뷰*/
        ReviewlayoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        dbReview = Firebase.database.getReference("KueatDB/Article")
        query = dbReview.orderByChild("restaurant_id").equalTo(rest_id.toString())
        val optionReview = FirebaseRecyclerOptions.Builder<Review>()
            .setQuery(query, Review::class.java).build()
        reviewadapter = MyReviewAdapter(optionReview)
        reviewadapter.itemClickListener = object :MyReviewAdapter.OnItemClickListener{
            override fun OnItemClick(pos: Int) {
                // Review 창으로 이동, 이동하면서 review id 넘기고 댓글 받아와야함

            }
        }
        binding!!.menurecyclerView.layoutManager = MenulayoutManager
        binding!!.menurecyclerView.adapter = menuadapter

        binding!!.reviewrecyclerView.layoutManager = ReviewlayoutManager
        binding!!.reviewrecyclerView.adapter = reviewadapter

        menuadapter.startListening()
        reviewadapter.startListening()

    }

    private fun initTab() {
        binding!!.apply {
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
                    binding!!.scrollView.scrollToView(binding!!.textRest)
                }
                1 -> {
                    binding!!.scrollView.scrollToView(binding!!.textMenu)
                }
                2 -> {
                    binding!!.scrollView.scrollToView(binding!!.textReview)
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
            if (calculateRectOnScreen(binding!!.scrollView).top >= calculateRectOnScreen(binding!!.textRest).top) {
                binding!!.tablayout.selectTab(tab1)
            }
            if (calculateRectOnScreen(binding!!.scrollView).top >= calculateRectOnScreen(binding!!.textMenu).top) {
                binding!!.tablayout.selectTab(tab2)
            }
            if(calculateRectOnScreen(binding!!.scrollView).top >= calculateRectOnScreen(binding!!.textReview).top){
                binding!!.tablayout.selectTab(tab3)
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
    override fun onStart() {
        super.onStart()
//        menuadapter.startListening()
//        reviewadapter.startListening()
    }
    override fun onStop() {
        super.onStop()
//        menuadapter.stopListening()
//        reviewadapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}