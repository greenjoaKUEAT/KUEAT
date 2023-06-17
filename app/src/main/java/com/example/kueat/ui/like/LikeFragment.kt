package com.example.kueat.ui.like

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.MainActivity
import com.example.kueat.R
import com.example.kueat.databinding.FragmentLikeBinding
import com.example.kueat.`object`.Restaurant
import com.example.kueat.`object`.location
import com.example.kueat.ui.home.HomeFragment
import com.example.kueat.ui.home.RestaurantFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class LikeFragment : Fragment() {

    lateinit var binding: FragmentLikeBinding
    var likeArr:ArrayList<Long> = arrayListOf()
    var restaurantArr:ArrayList<Restaurant> = arrayListOf()
    lateinit var likeAdapter: MyLikeAdapter

    lateinit var dbLikeRestaurant: DatabaseReference
    lateinit var dbRestaurant: DatabaseReference
    private val mainscope = CoroutineScope(Dispatchers.Main)
    var user = Firebase.auth.currentUser!!
    val uid = user.uid
    val TAG = "likeFragment"
    var Loc=""
    var Menu=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            Loc = it.getString("Loc").toString()
            Menu = it.getString("Menu")!!
        }
        dbLikeRestaurant = Firebase.database.getReference("KueatDB/LikedRestaurant")
        dbRestaurant = Firebase.database.getReference("KueatDB/Restaurant")
        initRecycler()
        initLikeRestaurant()
        initBtn()
    }

    private fun initBtn() {
        binding.backHomeButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("Loc", Loc)
            bundle.putString("Menu", Menu)
            val homeFragment = HomeFragment()
            homeFragment.arguments = bundle
            (activity as MainActivity).changeBottomIconHome()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main_frm, homeFragment).commit()
        }
    }

    private fun initRecycler() {
        binding.recyclerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        likeAdapter = MyLikeAdapter(restaurantArr, requireActivity())
        likeAdapter.itemClickListener=object:MyLikeAdapter.OnItemClickListener{
            override fun onItemClick(holder: MyLikeAdapter.ViewHolder, position: Int) {
                binding.apply {
                    val bundle = Bundle()
                    bundle.putLong("restaurant",likeAdapter.items[position].restaurant_id)
                    val restaurantFragment = RestaurantFragment()
                    restaurantFragment.arguments = bundle
                    (activity as MainActivity).changeBottomIconHome()
                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main_frm, restaurantFragment).commit()
                }
            }
        }

        binding.recyclerview.adapter = likeAdapter

    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
    private fun initLikeRestaurant() {
        mainscope.launch {
            showProgressBar()
            withContext(Dispatchers.IO) {
                dbLikeRestaurant.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (data in it.result.children) {
                            Log.e(
                                TAG,
                                "ValueEventListener-onDataChange : ${data.value}",
                            )
                            val datas = data.value as Map<String, String>
                            if (datas["uid"].equals(uid)) {
                                Log.e(
                                    TAG,
                                    "ValueEventListener-onDataChange : ${datas["uid"]}&$uid",
                                )
                                datas["restaurant_id"]?.let { it1 -> likeArr.add(it1.toLong()) }
                            }
                        }
                    }
                }.await()
                withContext(Dispatchers.IO) {
                    dbRestaurant.get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            for (data in it.result.children) {
                                Log.e(
                                    TAG,
                                    "ValueEventListener-onDataChange : ${data.child("name").value}",
                                )
                                val resArticle = data.child("article_number").value as Long
                                val resName = data.child("name").value as String
                                val resPhoto = data.child("photo").value as String
                                val resRating = data.child("rating").value as String
                                val resid = data.child("restaurant_id").value as Long
                                val resTagLoc = data.child("tag_location").value as String
                                val resTagType = data.child("tag_type").value as String
                                val resLoclatitude = data.child("latitude").value as String
                                val resLoclongitude = data.child("longitude").value as String
                                for (like in likeArr) {
                                    if (resid==like) {
                                        Log.e(
                                            TAG,
                                            "ValueEventListener-onDataChange : ${resid}&$like",
                                        )
                                        Log.e(TAG, "${resName}")
                                        restaurantArr.add(Restaurant(resArticle,resLoclatitude,resLoclongitude,resName,resPhoto,resRating,resid,resTagLoc,resTagType))
                                    }
                                }
                            }
                        }
                    }.await()
                }
            }
            withContext(Dispatchers.Main) {
                hideProgressBar()
                likeAdapter.notifyDataSetChanged()
                if(likeAdapter.itemCount!=0){
                    binding.emptyLayout.visibility = View.GONE
                    binding.textTitle.visibility = View.VISIBLE
                }
                else {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.textTitle.visibility = View.GONE
                }
                Log.d(TAG, "number" + likeAdapter.itemCount + "/ ${restaurantArr.size}")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            likeAdapter.stopLocationUpdate()
        }catch (e:UninitializedPropertyAccessException){
            Log.d(TAG,"lateinit exception")
        }
        Log.d(TAG,"stop")
    }
}