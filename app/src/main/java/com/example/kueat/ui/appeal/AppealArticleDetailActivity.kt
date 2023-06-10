package com.example.kueat.ui.appeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.R
import com.example.kueat.databinding.ActivityAppealArticleDetailBinding
import com.example.kueat.`object`.Comment

class AppealArticleDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityAppealArticleDetailBinding
    lateinit var adapter: AppealArticleCommentAdapter
    lateinit var dataList: ArrayList<Comment>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppealArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initLayout()
    }

    fun initLayout() {
        adapter = AppealArticleCommentAdapter(dataList)
        binding.rvAppealArticleComment.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAppealArticleComment.adapter = adapter
        binding.ivAppealArticleBack.setOnClickListener {
            finish()
        }
    }

    fun initData() {
        dataList = arrayListOf(
            Comment(
                0,
                0,
                0,
                "인정 가격대가 좀 있긴 한대 맛있음",
                1,
                0,
                "05/12 20:12"
            ),

            Comment(
                0,
                0,
                0,
                "인정 가격대가 좀 있긴 한대 맛있음",
                1,
                0,
                "05/12 20:12"
            ),
            Comment(
                0,
                0,
                0,
                "인정 가격대가 좀 있긴 한대 맛있음",
                1,
                0,
                "05/12 20:12"
            ),
            Comment(
                0,
                0,
                0,
                "인정 가격대가 좀 있긴 한대 맛있음",
                1,
                0,
                "05/12 20:12"
            )

        )
    }
}