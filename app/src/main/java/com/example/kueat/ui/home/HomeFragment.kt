package com.example.kueat.ui.home

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.MainActivity
import com.example.kueat.R
import com.example.kueat.databinding.FragmentHomeBinding
import com.example.kueat.`object`.Restaurant
import com.example.kueat.ui.filter.FilterMenuFragment
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.play.integrity.internal.m
import com.google.common.base.Predicates.equalTo
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: MyRestaurantAdapter
    lateinit var rdb: DatabaseReference
    var Loc = ""
    var Menu = ""
    private val mainscope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            Loc = it.getString("Loc").toString().replace("\"","")
            Menu = it.getString("Menu")!!.replace("\"","")
        }

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
        binding.fragmentHomeMenu.text = Menu
        binding.fragmentHomeLocation.text = Loc
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
        adapter = MyRestaurantAdapter(option)
        adapter.itemClickListener = object : MyRestaurantAdapter.OnItemClickListener {
            override fun OnItemClick(position: Int, model: Restaurant) {
                val bundle = Bundle()
                bundle.putLong("restaurant",model.restaurant_id)
                val restaurantFragment = RestaurantFragment()
                restaurantFragment.arguments = bundle
//                (activity as MainActivity).changeBottomIconHome()
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main_frm, restaurantFragment).commit()
                /* 송현이형 프래그먼트 이름 알아낸 후 전환해주기
                val bundle = Bundle() // 번들을 통해 값 전달
                bundle.putInt("RestaurantID", model.restaurant_id) //번들에 넘길 값 저장
                val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                val fragment2 = RestaurantDetailFragment() //프래그먼트2 선언
                fragment2.setArguments(bundle) //번들을 프래그먼트2로 보낼 준비
                transaction.replace(com.example.kueat.R.id.init_frm, fragment2)
                transaction.commit()
                 */
            }
        }

        binding.fragmentHomeRecyclerView.layoutManager=layoutManager
        binding.fragmentHomeRecyclerView.adapter=adapter
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
        Log.d("homeFragment","start")
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        Log.d("homeFragment","stop")
    }
}












