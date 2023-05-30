package com.example.kueat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.kueat.databinding.ActivityMainBinding
import com.example.kueat.ui.account.AccountFragment
import com.example.kueat.ui.appeal.AppealFragment
import com.example.kueat.ui.home.HomeFragment
import com.example.kueat.ui.like.LikeFragment
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    lateinit var binding: ActivityMainBinding
//    var nowFragment:Int=R.id.navigation_home
//    var oldFragment:Int=R.id.navigation_home
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val callback = onBackPressedDispatcher.addCallback(this) {
            backPressed()
        }
        init()
    }
    private fun init(){
        binding.navView.run{
            setOnItemSelectedListener {
//                oldFragment = nowFragment
//                nowFragment = selectedItemId
                onNavigationItemSelected(it)
            }
            //초기 화면 홈으로 설정
            selectedItemId = R.id.navigation_home
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
//            R.id.navigation_like -> {
//                val fragment = supportFragmentManager.beginTransaction()
//                if(oldFragment==R.id.navigation_home)
//                    fragment.addToBackStack(null)
//                fragment.replace(R.id.main_frm, LikeFragment()).commit()
//            }
            R.id.navigation_like -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, LikeFragment()).commit()
            R.id.navigation_home -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commit()
            R.id.navigation_appeal -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, AppealFragment()).commit()
            R.id.navigation_account -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, AccountFragment()).commit()
        }
        return true
    }

    fun backPressed() {
        for(fragment in supportFragmentManager.fragments){
            if(fragment.isVisible){
                Log.i("fragment",fragment.toString())
//                Log.i("fragment",HomeFragment.toString())
                if(fragment is HomeFragment) {
                    val initIntent = Intent(this, InitActivity::class.java)
                    startActivity(initIntent)
                }else{
                    binding.navView.selectedItemId = R.id.navigation_home
                }
            }
        }

    }
}