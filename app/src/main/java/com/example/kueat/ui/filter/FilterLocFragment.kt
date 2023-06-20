package com.example.kueat.ui.filter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.kueat.MainActivity
import com.example.kueat.R
import com.example.kueat.databinding.FragmentFilterLocBinding
import com.example.kueat.sharedpreference.MyTag


class FilterLocFragment : Fragment() {

    lateinit var binding:FragmentFilterLocBinding
    var Loc=""
    lateinit var sharedPref: MyTag
    var isChangeOnce = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterLocBinding.inflate(inflater, container, false)
        arguments?.let {
            isChangeOnce = it.getBoolean("check")
        }
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
                if(isChangeOnce)
                    changeActivity()
                else
                    changeFragment()
            }
            mid.setOnClickListener {
                Loc="중문"
                if(isChangeOnce)
                    changeActivity()
                else
                    changeFragment()
            }
            back.setOnClickListener {
                Loc="후문"
                if(isChangeOnce)
                    changeActivity()
                else
                    changeFragment()
            }
            side.setOnClickListener {
                Loc="쪽문"
                if(isChangeOnce)
                    changeActivity()
                else
                    changeFragment()
            }
            sejong.setOnClickListener {
                Loc="세종대"
                if(isChangeOnce)
                    changeActivity()
                else
                    changeFragment()
            }
        }
    }
    fun changeFragment(){
        context?.let { sharedPref.saveMyLoc(it,Loc) }
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.init_frm, FilterMenuFragment()).commit()
    }
    fun changeActivity(){
        context?.let { sharedPref.saveMyLoc(it,Loc) }
        val intent = Intent(activity,MainActivity::class.java)
        startActivity(intent)
    }
}