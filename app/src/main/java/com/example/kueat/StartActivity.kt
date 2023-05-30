package com.example.kueat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import com.example.kueat.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    lateinit var binding:ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val callback = onBackPressedDispatcher.addCallback(this) {
            backPressed()
        }
        initBtn()
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