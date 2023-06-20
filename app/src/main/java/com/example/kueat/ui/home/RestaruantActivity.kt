package com.example.kueat.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import com.example.kueat.InitActivity
import com.example.kueat.R
import com.example.kueat.databinding.ActivityRestaruantBinding
import com.example.kueat.ui.account.AccountFragment
import com.example.kueat.ui.account.EditNicknameFragment
import com.example.kueat.ui.account.EditPasswordFragment
import com.example.kueat.ui.like.LikeFragment

class RestaruantActivity : AppCompatActivity() {
    lateinit var binding : ActivityRestaruantBinding
    var rest_id = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaruantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rest_id = intent.getLongExtra("rest_id",0L)
        initLayout()
//        val callback = onBackPressedDispatcher.addCallback(this) {
//            backPressed()
//        }
    }

    private fun backPressed() {
        for(fragment in supportFragmentManager.fragments){
            if(fragment.isVisible){
                if(fragment is RestaurantFragment) {
                    finish()
                }
                else{
                    /*
                    val bundle = Bundle()
                    val restFragment = RestaurantFragment()
                    bundle.putString("rest_id",rest_id.toString())
                    restFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.rest_frm, restFragment).commit()

                     */
                }
            }
        }
    }

    fun initLayout(){
        val bundle = Bundle()
        val restFragment = RestaurantFragment()
        bundle.putString("rest_id",rest_id.toString())
        restFragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.rest_frm, restFragment).commit()
    }
}