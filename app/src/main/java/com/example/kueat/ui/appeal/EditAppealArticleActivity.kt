package com.example.kueat.ui.appeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kueat.R
import com.example.kueat.databinding.ActivityEditAppealArticleBinding

class EditAppealArticleActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditAppealArticleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAppealArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    fun initLayout(){
        binding.ivEditAppealArticleBack.setOnClickListener {
            finish()
        }
        binding.tvConfirmEditArticle.setOnClickListener {
            finish()
        }
    }
}