package com.example.kueat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.kueat.databinding.ActivityMainBinding
import com.example.kueat.ui.account.AccountFragment
import com.example.kueat.ui.appeal.AppealFragment
import com.example.kueat.ui.filter.FilterMenuFragment
import com.example.kueat.ui.home.HomeFragment
import com.example.kueat.ui.like.LikeFragment
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    lateinit var binding: ActivityMainBinding
    var Loc=""
    var Menu=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //뒤로가기 버튼 클릭시 호출되는 callBack함수
        val callback = onBackPressedDispatcher.addCallback(this) {
            backPressed()
        }

        Loc= getIntent().getStringExtra("Loc").toString()
        Menu= getIntent().getStringExtra("Menu").toString()


        init()
    }
    private fun init(){
        binding.navView.run{
            setOnItemSelectedListener {
                onNavigationItemSelected(it)
            }
            //초기 화면 홈으로 설정
            selectedItemId = R.id.navigation_home
            val currentFragment = supportFragmentManager.findFragmentById(R.id.main_frm)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when(item.itemId){
            R.id.navigation_like -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, LikeFragment()).commit()
            R.id.navigation_home -> {
                    val bundle = Bundle()
                    bundle.putString("Loc", Loc)
                    bundle.putString("Menu", Menu)
                    val homeFragment = HomeFragment()
                    homeFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.main_frm, homeFragment).commit()

                //supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commit()
            }
            R.id.navigation_appeal -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, AppealFragment()).commit()
            R.id.navigation_account -> supportFragmentManager.beginTransaction().replace(R.id.main_frm, AccountFragment()).commit()
        }
        return true
    }

    fun backPressed() {
        for(fragment in supportFragmentManager.fragments){
            if(fragment.isVisible){
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