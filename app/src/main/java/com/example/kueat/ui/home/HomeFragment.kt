package com.example.kueat.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kueat.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    var Loc=""
    var Menu=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            Loc= it.getString("Loc").toString()
            Menu= it.getString("Menu")!!
        }

        //Toast.makeText(requireContext(), Loc +"나오나나오나??"+Menu,Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.fragmentHomeMenu.text=Menu
        binding.fragmentHomeLocation.text=Loc

        return binding.root
    }
}