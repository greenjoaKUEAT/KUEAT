package com.example.kueat

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide.init
import com.example.kueat.databinding.ActivityMainBinding
import com.example.kueat.`object`.Article
import com.example.kueat.`object`.User
import com.example.kueat.`object`.location
import com.example.kueat.ui.account.AccountFragment
import com.example.kueat.ui.account.EditNicknameFragment
import com.example.kueat.ui.account.EditPasswordFragment
import com.example.kueat.ui.appeal.AppealFragment
import com.example.kueat.ui.filter.FilterMenuFragment
import com.example.kueat.ui.home.HomeFragment
import com.example.kueat.ui.home.RestaurantFragment
import com.example.kueat.ui.home.ReviewFragment
import com.example.kueat.ui.home.WriteReviewFragment
import com.example.kueat.ui.like.LikeFragment
import com.example.kueat.viewmodel.MyUserModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    var Loc=""
    var Menu=""
    lateinit var kueatDB: DatabaseReference
    val userModel: MyUserModel by viewModels()
    val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //뒤로가기 버튼 클릭시 호출되는 callBack함수
        val callback = onBackPressedDispatcher.addCallback(this) {
            backPressed()
        }

        Loc= intent.getStringExtra("Loc").toString()
        Menu= intent.getStringExtra("Menu").toString()

        initUser()
        init()

    }




    private fun initUser() {
        val name = user?.displayName
        val email = user?.email
        val uid = user?.uid
        kueatDB = Firebase.database.getReference("KueatDB/User")
        val currentUser = kueatDB.child(user!!.uid).get().addOnSuccessListener {
            val nickname = it.getValue<User>()?.nickname
            val id = it.getValue<User>()?.id

            userModel.setLiveData(User(uid!!,email!!,name!!,nickname!!,id!!))
        }
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
            R.id.navigation_like -> {
                val bundle = Bundle()
                bundle.putString("Loc", Loc)
                bundle.putString("Menu", Menu)
                val likeFragment = LikeFragment()
                likeFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_frm, likeFragment).commit()
            }
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
                }else if(fragment is EditPasswordFragment||fragment is EditNicknameFragment){
                    supportFragmentManager.beginTransaction().replace(R.id.main_frm, AccountFragment()).commit()
                }else if(fragment is ReviewFragment){
                    supportFragmentManager.beginTransaction().replace(R.id.main_frm, RestaurantFragment()).commit()
                }else if(fragment is WriteReviewFragment){
                    supportFragmentManager.beginTransaction().replace(R.id.main_frm, RestaurantFragment()).commit()
                }
                else{
                    binding.navView.selectedItemId = R.id.navigation_home

                }
            }
        }
    }
    fun changeBottomIconHome(){
        binding.navView.selectedItemId = R.id.navigation_home
    }
}