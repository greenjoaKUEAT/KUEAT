package com.example.kueat.ui.filter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.kueat.MainActivity
import com.example.kueat.R
import com.example.kueat.databinding.FragmentFilterLocBinding
import com.example.kueat.databinding.FragmentFilterMenuBinding
import com.example.kueat.sharedpreference.MyTag
import com.example.kueat.ui.like.LikeFragment


class FilterMenuFragment : Fragment() {
    lateinit var binding: FragmentFilterMenuBinding
    var Menu = ""
    lateinit var sharedPref:MyTag
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { MyTag(it) }!!
        initBtn()
    }

    private fun initBtn() {
        binding.apply {
            all.setOnClickListener {
                Menu="다 좋아"
                changeActivity()
            }
            korea.setOnClickListener {
                Menu="한식"
                changeActivity()
            }
            china.setOnClickListener {
                Menu="중식"
                changeActivity()
            }
            japan.setOnClickListener {
                Menu="일식"
                changeActivity()
            }
            usa.setOnClickListener {
                Menu="양식"
                changeActivity()
            }
            cafe.setOnClickListener {
                Menu="카페"
                changeActivity()
            }
        }
    }

    fun changeActivity() {
        context?.let { sharedPref.saveMyMenu(it,Menu) }

        val intent = Intent(requireActivity(), MainActivity::class.java)
        requireActivity().startActivity(intent)

    }
}