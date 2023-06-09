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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.databinding.FragmentHomeBinding
import com.example.kueat.ui.filter.FilterMenuFragment
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.play.integrity.internal.m
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: MyRestaurantAdapter
    lateinit var rdb: DatabaseReference
    var Loc = ""
    var Menu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            Loc = it.getString("Loc").toString()
            Menu = it.getString("Menu")!!
        }

        //Toast.makeText(requireContext(), Loc +"나오나나오나??"+Menu,Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.fragmentHomeMenu.text = Menu
        binding.fragmentHomeLocation.text = Loc

        init()

        return binding.root
    }


    private fun init() {

        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rdb = Firebase.database.getReference("KueatDB/Restaurant")
        Log.d("HI",rdb.database.toString())
        val query = rdb.limitToLast(50)
//      val query=rdb.orderByChild("tag").equalTo("${Loc}${Menu}")
        val option = FirebaseRecyclerOptions.Builder<Restaurant>()
            .setQuery(query, Restaurant::class.java)
            .build()
        adapter = MyRestaurantAdapter(option)
        adapter.itemClickListener = object : MyRestaurantAdapter.OnItemClickListener {
            override fun OnItemClick(position: Int, model: Restaurant) {

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
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}












