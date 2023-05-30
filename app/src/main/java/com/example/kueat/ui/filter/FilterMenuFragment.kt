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
import com.example.kueat.databinding.FragmentFilterLocBinding
import com.example.kueat.databinding.FragmentFilterMenuBinding
import com.example.kueat.ui.like.LikeFragment


class FilterMenuFragment : Fragment() {
    lateinit var binding:FragmentFilterMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterMenuBinding.inflate(inflater, container, false)
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

            all.setOnClickListener{
                changeActivity()
            }
            korea.setOnClickListener {
                changeActivity()
            }
            china.setOnClickListener {
                changeActivity()
            }
            japan.setOnClickListener {
                changeActivity()
            }
            usa.setOnClickListener {
                changeActivity()
            }
            cafe.setOnClickListener {
                changeActivity()
            }

        }
    }

    fun changeActivity(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        requireActivity().startActivity(intent)
    }
    fun backPressed(){
        val fragment = requireActivity().supportFragmentManager
        fragment.beginTransaction().replace(R.id.init_frm, FilterLocFragment()).commit()
    }
}