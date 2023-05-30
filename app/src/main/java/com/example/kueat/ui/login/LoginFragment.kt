package com.example.kueat.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kueat.R
import com.example.kueat.StartActivity
import com.example.kueat.databinding.FragmentLoginBinding
import com.example.kueat.ui.filter.FilterLocFragment
import com.example.kueat.ui.filter.FilterMenuFragment


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginBtn.setOnClickListener {
            changeFragment()
        }
        binding.backBtn.setOnClickListener {
            changeActivity()
        }
    }
    fun changeFragment(){
        val fragment = requireActivity().supportFragmentManager
        fragment.beginTransaction().replace(R.id.init_frm, FilterLocFragment()).commit()
    }
    fun changeActivity(){
        val intent = Intent(requireActivity(), StartActivity::class.java)
        requireActivity().startActivity(intent)
    }
}