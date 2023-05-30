package com.example.kueat.ui.filter

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import com.example.kueat.MainActivity
import com.example.kueat.R
import com.example.kueat.StartActivity
import com.example.kueat.databinding.FragmentFilterLocBinding
import com.example.kueat.ui.login.LoginFragment


class FilterLocFragment : Fragment() {

    lateinit var binding:FragmentFilterLocBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterLocBinding.inflate(inflater, container, false)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            backPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtn()
    }

    private fun initBtn() {
        binding.apply {
            front.setOnClickListener{
                changeFragment()
            }
            mid.setOnClickListener {
                changeFragment()
            }
            back.setOnClickListener {
                changeFragment()
            }
            side.setOnClickListener {
                changeFragment()
            }
            sejong.setOnClickListener {
                changeFragment()
            }
        }
    }
    fun changeFragment(){
        val fragment = requireActivity().supportFragmentManager
        fragment.beginTransaction().replace(R.id.init_frm, FilterMenuFragment()).commit()
    }
    fun backPressed(){
        val intent = Intent(requireActivity(), StartActivity::class.java)
        requireActivity().startActivity(intent)
    }
}