package com.example.kueat.ui.home

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.kueat.R
import com.example.kueat.databinding.FragmentRestaurantBinding
import com.example.kueat.`object`.Menu
import com.example.kueat.`object`.Article
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.NonDisposableHandle.parent
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
    lateinit var dbRest : DatabaseReference
    lateinit var dbMenu : DatabaseReference
    lateinit var dbReview : DatabaseReference
    lateinit var dbULike : DatabaseReference
    val user = Firebase.auth.currentUser
    // 임시 intent
    var rest_id = 1;
    var tabUser = true
    var scrollUser = true
    var isLiked = false
    lateinit var item :Task<DataSnapshot>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rest_id = arguments?.getString("rest_id")!!.toInt()
        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        initRecycler()
    }

    private fun initLayout() {
        initTab()
        dbRest = Firebase.database.getReference("KueatDB/Restaurant")
        dbRest.child(rest_id.toString()).get().addOnSuccessListener {
            val map = it.getValue() as HashMap<String,Any>
            binding!!.textRest.text = map.get("name").toString()
            val photo = map.get("photo").toString()
            var firebaseStorage = FirebaseStorage.getInstance()
            var firebaseStorageRef = firebaseStorage.getReference(photo)
            var url = firebaseStorageRef.downloadUrl.addOnSuccessListener {
                Glide.with(requireContext()).load(it).dontAnimate().into(binding!!.img1)
                Glide.with(requireContext()).load(it).dontAnimate().into(binding!!.img2)
                Glide.with(requireContext()).load(it).dontAnimate().into(binding!!.img3)
                Glide.with(requireContext()).load(it).dontAnimate().into(binding!!.img4)
                Log.d("qwerty123", it.toString());
            }
        }

//        val fragment = childFragmentManager.beginTransaction()
        val naverfragment = NaverFragment()
        val bundle  = Bundle()
        bundle.putString("rest_id",rest_id.toString())
        naverfragment.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.map_frame,naverfragment).commit()

        dbULike = Firebase.database.getReference("KueatDB/LikedRestaurant")
        val key = rest_id.toString() + user!!.uid
        dbULike.child(key).get().addOnSuccessListener {
            if(it.exists()) {
                binding!!.likebtn.setImageResource(R.drawable.tap_like_filled)
                isLiked = true
            }
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
                    dbULike.child(key).setValue(Likedrest(rest_id.toString(),user!!.uid,key))
                    binding!!.likebtn.setImageResource(R.drawable.tap_like_filled)
                    isLiked = true
                }
            }
        }


        /*리뷰*/
        binding!!.apply {
            addReview.setOnClickListener {
                val bundle  = Bundle()
                bundle.putString("rest_id",rest_id.toString())
                val reviewWriteFragment = WriteReviewFragment()
                reviewWriteFragment.arguments = bundle
                parentFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.rest_frm,reviewWriteFragment).commit()
            }
        }

    }
    fun initRecycler(){
        /*메뉴*/
        MenulayoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        dbMenu = Firebase.database.getReference("KueatDB/Menu")
        var query = dbMenu.orderByChild("restaurant_id").equalTo(rest_id.toString())
        val optionMenu = FirebaseRecyclerOptions.Builder<Menu>()
            .setQuery(query, Menu::class.java).build()
        menuadapter = MyMenuAdapter(optionMenu)

        /*리뷰*/
        ReviewlayoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        dbReview = Firebase.database.getReference("KueatDB/Article/0")
        query = dbReview.orderByChild("restaurant_id").equalTo(rest_id.toDouble())

        val optionReview = FirebaseRecyclerOptions.Builder<Article>()
            .setQuery(query, Article::class.java).build()
        reviewadapter = MyReviewAdapter(optionReview)
        reviewadapter.itemClickListener = object :MyReviewAdapter.OnItemClickListener{
            override fun OnItemClick(pos: Int) {
                // Review 창으로 이동, 이동하면서 review id 넘기고 댓글 받아와야함
                val bundle  = Bundle()
                bundle.putString("review_id",reviewadapter.getItem(pos).article_id.toString())
                val reviewFragment = ReviewFragment()
                reviewFragment.arguments = bundle
                parentFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.rest_frm,reviewFragment).commit()
            }
        }
        binding!!.menurecyclerView.layoutManager = MenulayoutManager
        binding!!.menurecyclerView.adapter = menuadapter

        binding!!.reviewrecyclerView.layoutManager = ReviewlayoutManager
        binding!!.reviewrecyclerView.adapter = reviewadapter

//        menuadapter.startListening()
//        reviewadapter.startListening()
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
                    binding!!.scrollView.scrollToView(binding!!.imageLayout)
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
            if (calculateRectOnScreen(binding!!.scrollView).top >= calculateRectOnScreen(binding!!.imageLayout).top) {
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

    fun NestedScrollView.computeDistanceToView(view: View): Int {
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
    fun NestedScrollView.scrollToView(view: View) {
        val y = computeDistanceToView(view)
        this.scrollTo(0, y)
    }
    override fun onStart() {
        super.onStart()
        initRecycler()
        menuadapter.startListening()
        reviewadapter.startListening()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        menuadapter.stopListening()
        reviewadapter.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}