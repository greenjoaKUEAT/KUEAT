package com.example.kueat

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.kueat.databinding.ActivityMainBinding
import com.example.kueat.ui.account.AccountFragment
import com.example.kueat.ui.appeal.AppealFragment
import com.example.kueat.ui.home.HomeFragment
import com.example.kueat.ui.like.LikeFragment
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    fun init(){
        binding.navView.setOnItemSelectedListener {
            onNavigationItemSelected(it)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navigation_like -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, LikeFragment()).commit()
            R.id.navigation_home -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commit()
            R.id.navigation_appeal -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, AppealFragment()).commit()
            R.id.navigation_account -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, AccountFragment()).commit()
        }
        return true
    }


}