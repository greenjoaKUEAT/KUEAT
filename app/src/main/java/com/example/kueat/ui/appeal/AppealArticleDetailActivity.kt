package com.example.kueat.ui.appeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.R
import com.example.kueat.databinding.ActivityAppealArticleDetailBinding
import com.example.kueat.`object`.Article
import com.example.kueat.`object`.Comment
import com.example.kueat.ui.home.MyCommentAdapter
import com.firebase.ui.database.FirebaseArray
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.internal.artificialFrame
import java.text.SimpleDateFormat
import java.util.Calendar

class AppealArticleDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityAppealArticleDetailBinding
    lateinit var adapter: AppealArticleCommentAdapter
    lateinit var commentDBReference: DatabaseReference
    lateinit var commentLikeDBReference: DatabaseReference
    lateinit var articleDBReference: DatabaseReference
    lateinit var restaurantLikeDBReference: DatabaseReference
    lateinit var userDBReference: DatabaseReference
    var user = Firebase.auth.currentUser
    lateinit var article: Article
    var isLikedArticle = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppealArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    fun initLayout() {
        //게시글
        val articleKey = intent.getStringExtra("article_key")
        articleDBReference = Firebase.database.getReference("KueatDB/Article/0")
        articleDBReference.child(articleKey!!).get().addOnSuccessListener {
            val map = it.getValue() as HashMap<String, Any>
            article = Article(
                article_id = map.get("article_id").toString(),
                title = map.get("title").toString(),
                date = map.get("date").toString(),
                content = map.get("content").toString(),
                comment_number = map.get("comment_number").toString().toInt(),
                liked_user_number = map.get("liked_user_number").toString().toInt(),
                restaurant_id = map.get("restaurant_id").toString().toInt(),
                type = map.get("type").toString().toInt(),
                user_id = map.get("user_id").toString(),
            )
            binding.apply {
                tvAppealArticleDetailProfileDate.text = article.date
                tvAppealArticleDetailTitle.text = article.title
                tvAppealArticleDetailContent.text = article.content
                tvAppealArticleDetailLikedUser.text = article.liked_user_number.toString()
                tvAppealArticleDetailComments.text = article.comment_number.toString()
            }

            userDBReference = Firebase.database.getReference("KueatDB/User")
            userDBReference.child(article.user_id).get().addOnSuccessListener {
                val map = it.getValue() as HashMap<String, Any>
                binding.tvAppealArticleDetailProfileName.text = map.get("nickname").toString()
            }
        }

        //게시글 좋아요
        restaurantLikeDBReference = Firebase.database.getReference("KueatDB/LikedArticle")
        restaurantLikeDBReference.child(articleKey + user!!.uid).get().addOnSuccessListener {
            isLikedArticle = it.exists()
            if(isLikedArticle)
                binding.tvAppealArticleDetailLikedUser.setTextColor(getColor(R.color.pink))
        }

        binding.ivAppealArticleDetailLikeButton.setOnClickListener {
            if (isLikedArticle) {
                restaurantLikeDBReference.child(article.article_id + user!!.uid).removeValue()
                article.liked_user_number = article.liked_user_number - 1
                binding.tvAppealArticleDetailLikedUser.setTextColor(getColor(R.color.black))
            } else {
                restaurantLikeDBReference.child(article.article_id + user!!.uid).setValue(
                    LikedArticle(
                        article.article_id,
                        user!!.uid,
                        article.article_id + user!!.uid
                    )
                )
                article.liked_user_number = article.liked_user_number + 1
                binding.tvAppealArticleDetailLikedUser.setTextColor(getColor(R.color.pink))
            }
            isLikedArticle = !isLikedArticle
            articleDBReference.child(article.article_id).setValue(article)
            binding.tvAppealArticleDetailLikedUser.text =
                article.liked_user_number.toString()
        }

        //댓글
        binding.ivAppealArticleAddCommentButton.setOnClickListener {
            val key = commentDBReference.push().key
            val currentTime = System.currentTimeMillis()
            val dataFormat = SimpleDateFormat("MM/dd HH:mm")
            val current = dataFormat.format(currentTime)
            val comment = Comment(
                key!!,
                user!!.uid,
                article.article_id,
                binding.etAppealArticleAddCommentText.text.toString(),
                article.liked_user_number,
                0,
                current
            )
            binding.etAppealArticleAddCommentText.text.clear()

            commentDBReference.child(key!!).setValue(comment).addOnSuccessListener {
                article.comment_number = article.comment_number + 1
                articleDBReference.child(article.article_id).setValue(article)
                binding.tvAppealArticleDetailComments.text = article.comment_number.toString()
            }
        }

        commentDBReference = Firebase.database.getReference("KueatDB/Comment")
        binding.rvAppealArticleComment.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var query = commentDBReference.orderByChild("article_id").equalTo(articleKey)
        val commentOptions = FirebaseRecyclerOptions.Builder<Comment>()
            .setQuery(query, Comment::class.java).build()
        adapter = AppealArticleCommentAdapter(commentOptions, user!!.uid, this)
        binding.rvAppealArticleComment.adapter = adapter
        adapter.startListening()

        //댓글 좋아요
        commentLikeDBReference = Firebase.database.getReference("KueatDB/LikedComment")
        adapter.OnItemClickListener = object : AppealArticleCommentAdapter.onItemClickListener {
            override fun onItemClicked(position: Int) {
                val comment = adapter.getItem(position)
                commentLikeDBReference.child(comment.comment_id+user!!.uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        commentLikeDBReference.child(comment.comment_id+user!!.uid).removeValue()
                        comment.liked_user_number = comment.liked_user_number - 1
                    } else {
                        commentLikeDBReference.child(comment.comment_id+user!!.uid).setValue(
                            LikedComment(
                                comment.comment_id,
                                user!!.uid,
                                comment.comment_id + user!!.uid
                            )
                        )
                        comment.liked_user_number = comment.liked_user_number + 1
                    }
                    commentDBReference.child(comment.comment_id).setValue(comment)
                }
            }
        }

        binding.ivAppealArticleBack.setOnClickListener {
            finish()
        }
    }
}