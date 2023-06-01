package com.example.kueat.ui.appeal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kueat.databinding.FragmentAppealDetailBinding

class AppealDetailFragment : Fragment() {

    lateinit var binding: FragmentAppealDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppealDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

}