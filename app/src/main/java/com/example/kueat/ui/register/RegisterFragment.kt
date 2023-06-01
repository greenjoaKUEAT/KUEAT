package com.example.kueat.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kueat.MainActivity
import com.example.kueat.R
import com.example.kueat.StartActivity
import com.example.kueat.databinding.FragmentRegisterBinding
import com.example.kueat.ui.filter.FilterMenuFragment
import com.example.kueat.ui.login.LoginFragment


class RegisterFragment : Fragment() {
    lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerBtn.setOnClickListener {
            changeFragment()
        }
        binding.backBtn.setOnClickListener {
            changeActivity()
        }
    }
    fun changeFragment(){
        val fragment = requireActivity().supportFragmentManager
        fragment.beginTransaction().replace(R.id.init_frm, LoginFragment()).commit()
    }
    fun changeActivity(){
        val intent = Intent(requireActivity(), StartActivity::class.java)
        requireActivity().startActivity(intent)
    }
}