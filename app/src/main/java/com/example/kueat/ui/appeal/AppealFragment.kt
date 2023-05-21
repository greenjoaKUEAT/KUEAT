package com.example.kueat.ui.appeal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kueat.databinding.FragmentAppealBinding

class AppealFragment : Fragment() {

    lateinit var binding: FragmentAppealBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppealBinding.inflate(inflater, container, false)
        return binding.root
    }
}