package com.example.kueat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import com.example.kueat.databinding.ActivityInitBinding
import com.example.kueat.ui.filter.FilterLocFragment
import com.example.kueat.ui.filter.FilterMenuFragment
import com.example.kueat.ui.home.RestaurantFragment
import com.example.kueat.ui.like.LikeFragment
import com.example.kueat.ui.login.LoginFragment
import com.example.kueat.ui.register.RegisterFragment
import kotlinx.coroutines.NonCancellable.start

class InitActivity : AppCompatActivity() {
    lateinit var binding:ActivityInitBinding
    var isChangeOnce:Boolean = false
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
        val currentFragment = supportFragmentManager.findFragmentById(R.id.init_frm)

        when(currentFragment){
            is FilterLocFragment-> finishAffinity()
            is FilterMenuFragment -> supportFragmentManager.beginTransaction().replace(R.id.init_frm, FilterLocFragment()).commit()
            is LoginFragment -> changeStartActivity()
            is RegisterFragment -> changeStartActivity()
        }
    }

    fun changeStartActivity(){
        val startIntent = Intent(this@InitActivity,StartActivity::class.java)
        startActivity(startIntent)
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
                }"menu"->{
                    supportFragmentManager.beginTransaction().replace(R.id.init_frm, FilterMenuFragment()).commit()
                }"loc"->{
                    isChangeOnce = true
                    val bundle = Bundle()
                    bundle.putBoolean("check",isChangeOnce)
                    val filterLocFragment = FilterLocFragment()
                    filterLocFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.init_frm, filterLocFragment).commit()
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