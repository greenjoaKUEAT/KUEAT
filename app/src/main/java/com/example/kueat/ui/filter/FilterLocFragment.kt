package com.example.kueat.ui.filter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.kueat.databinding.FragmentFilterLocBinding
import com.example.kueat.sharedpreference.MyTag


class FilterLocFragment : Fragment() {

    lateinit var binding:FragmentFilterLocBinding
    var Loc=""
    lateinit var sharedPref: MyTag

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterLocBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { MyTag(it) }!!
        initBtn()
    }

    private fun initBtn() {
        binding.apply {
            front.setOnClickListener{
                Loc="정문"
                changeFragment()
            }
            mid.setOnClickListener {
                Loc="중문"
                changeFragment()
            }
            back.setOnClickListener {
                Loc="후문"
                changeFragment()
            }
            side.setOnClickListener {
                Loc="쪽문"
                changeFragment()
            }
            sejong.setOnClickListener {
                Loc="세종대"
                changeFragment()
            }
        }
    }
    fun changeFragment(){
        val fragment = requireActivity().supportFragmentManager
        context?.let { sharedPref.saveMyLoc(it,Loc) }
//        val bundle = Bundle() // 번들을 통해 값 전달
//        bundle.putString("Loc", Loc) //번들에 넘길 값 저장
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val fragment2 = FilterMenuFragment() //프래그먼트2 선언
//        fragment2.setArguments(bundle) //번들을 프래그먼트2로 보낼 준비
        transaction.replace(com.example.kueat.R.id.init_frm, fragment2)
        transaction.commit()

        Log.d("Loc", Loc)




        //fragment.beginTransaction().replace(com.example.kueat.R.id.init_frm, FilterMenuFragment()).commit()

    }
}