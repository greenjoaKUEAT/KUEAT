package com.example.kueat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import com.example.kueat.databinding.ActivityStartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class StartActivity : AppCompatActivity() {
    lateinit var binding:ActivityStartBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        // Initialize Firebase Auth
        auth = Firebase.auth
        setContentView(binding.root)
        val callback = onBackPressedDispatcher.addCallback(this) {
            backPressed()
        }
        initBtn()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    //로그인 된 유저이면 InitActivity의 FilterLocFragment로 화면전환
    private fun reload() {
        val initIntent = Intent(this@StartActivity,InitActivity::class.java)
        startActivity(initIntent)
    }

    private fun backPressed() {
        finishAffinity()
    }

    private fun initBtn() {
        binding.apply{
            loginBtn.setOnClickListener{
                val initIntent = Intent(this@StartActivity,InitActivity::class.java)
                initIntent.putExtra("mode","login")
                startActivity(initIntent)
            }
            registerBtn.setOnClickListener{
                val initIntent = Intent(this@StartActivity,InitActivity::class.java)
                initIntent.putExtra("mode","register")
                startActivity(initIntent)
            }
        }
    }
}