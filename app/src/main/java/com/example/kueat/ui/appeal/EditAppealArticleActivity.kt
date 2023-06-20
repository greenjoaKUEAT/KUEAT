package com.example.kueat.ui.appeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kueat.R
import com.example.kueat.databinding.ActivityEditAppealArticleBinding
import com.example.kueat.`object`.Article
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar

class EditAppealArticleActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditAppealArticleBinding
    lateinit var articleDBReference: DatabaseReference
    val user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAppealArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    fun initLayout() {
        articleDBReference = Firebase.database.getReference("KueatDB/Article/0")

        binding.ivEditAppealArticleBackButton.setOnClickListener {
            finish()
        }

        binding.tvEditAppealArticleConfirmButton.setOnClickListener {
            val currentTime = Calendar.getInstance().time
            val dataFormat = SimpleDateFormat("MM/dd HH:MM")
            val current = dataFormat.format(currentTime)
            val title = binding.tvEditAppealArticleTitle
            val content = binding.tvEditAppealArticleContent
            val key = articleDBReference.push().key
            val article = Article(
                key!!,
                user!!.uid,
                0,
                1,
                title.text.toString(),
                content.text.toString(),
                0,
                0,
                current
            )
            articleDBReference.child(key!!).setValue(article)
            finish()
        }
    }
}