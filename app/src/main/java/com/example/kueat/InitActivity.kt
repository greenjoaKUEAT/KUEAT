package com.example.kueat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import com.example.kueat.databinding.ActivityInitBinding
import com.example.kueat.ui.filter.FilterLocFragment
import com.example.kueat.ui.like.LikeFragment
import com.example.kueat.ui.login.LoginFragment
import com.example.kueat.ui.register.RegisterFragment

class InitActivity : AppCompatActivity() {
    lateinit var binding:ActivityInitBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val callback = onBackPressedDispatcher.addCallback(this) {
            backPressed()
        }
        initLayout()
    }

    private fun backPressed() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
    }

    private fun initLayout() {
        val initIntent = intent
        val mode = initIntent.getStringExtra("mode")
        if(mode!=null){
            when(mode){
                "login"->{
                    supportFragmentManager.beginTransaction().replace(R.id.init_frm, LoginFragment()).commit()
                }
                "register"->{
                    supportFragmentManager.beginTransaction().replace(R.id.init_frm, RegisterFragment()).commit()
                }
                else->{
                    supportFragmentManager.beginTransaction().replace(R.id.init_frm, FilterLocFragment()).commit()
                }
            }
        }else{
            supportFragmentManager.beginTransaction().replace(R.id.init_frm, FilterLocFragment()).commit()
        }
    }
}