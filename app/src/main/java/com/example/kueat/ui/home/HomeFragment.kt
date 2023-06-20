package com.example.kueat.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.MainActivity
import com.example.kueat.R
import com.example.kueat.databinding.FragmentHomeBinding
import com.example.kueat.`object`.Restaurant
import com.example.kueat.`object`.location
import com.example.kueat.sharedpreference.MyTag
import com.example.kueat.ui.filter.FilterMenuFragment
import com.example.kueat.viewmodel.MyUserModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.play.integrity.internal.m
import com.google.common.base.Predicates.equalTo
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: MyRestaurantAdapter
    lateinit var rdb: DatabaseReference
    lateinit var sharedPref: MyTag
    lateinit var Loc:String
    lateinit var Menu:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Toast.makeText(requireContext(), Loc +"나오나나오나??"+Menu,Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { MyTag(it) }!!
        Loc = sharedPref.getMyLoc(requireContext())
        Menu = sharedPref.getMyMenu(requireContext())
        binding.fragmentHomeLocation.text = "\"$Loc\""
        binding.fragmentHomeMenu.text = "\"$Menu\""
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());


//        showProgressBar()
        init()

    }
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }


    private fun init() {

        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rdb = Firebase.database.getReference("KueatDB/Restaurant")
        Log.d("homeFragment",Loc)

        var query=rdb.orderByChild("tag").equalTo(Loc+Menu)
        if(Menu == "다 좋아"){
            query=rdb.orderByChild("tag_location").equalTo(Loc)
        }
        val option = FirebaseRecyclerOptions.Builder<Restaurant>()
            .setQuery(query, Restaurant::class.java)
            .build()
        adapter = MyRestaurantAdapter(option,requireActivity())
        adapter.itemClickListener = object : MyRestaurantAdapter.OnItemClickListener {
            override fun OnItemClick(position: Int, model: Restaurant) {
                val intent = Intent(activity,RestaruantActivity::class.java)
                intent.putExtra("rest_id",model.restaurant_id)
                startActivity(intent)
                /*val bundle = Bundle()
                bundle.putLong("restaurant", model.restaurant_id)
                val restaurantFragment = RestaurantFragment()
                restaurantFragment.arguments = bundle
//            (activity as MainActivity).changeBottomIconHome()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, restaurantFragment).commit()*/
            }
        }
        binding.fragmentHomeRecyclerView.layoutManager = layoutManager
        binding.fragmentHomeRecyclerView.itemAnimator = null
        binding.fragmentHomeRecyclerView.adapter = adapter
    }


    override fun onStart() {
        super.onStart()
        init()
        adapter.startListening()
        Log.d("homeFragment","start")
    }

    override fun onStop() {
        super.onStop()
        try {
            adapter.stopLocationUpdate()
        }catch (e:UninitializedPropertyAccessException){
            Log.d("homeFragment","lateinit exception")
        }
        adapter.stopListening()
        Log.d("homeFragment","stop")
    }
}












