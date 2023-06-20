package com.example.kueat.ui.appeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.internal.artificialFrame
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    var scope = CoroutineScope(Dispatchers.Main)
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
        articleDBReference = Firebase.database.getReference("KueatDB/Article/1")
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
            userDBReference.child(article.user_id).get().addOnCompleteListener {
                if(it.isSuccessful){
                    var usr_nickname = it.result.child("nickname").getValue().toString()
                    Log.d("check",usr_nickname)
                    if(usr_nickname!="null")
                        binding.tvAppealArticleDetailProfileName.text = usr_nickname
                    else
                        binding.tvAppealArticleDetailProfileName.text = "(알 수 없음)"
                }
            }
        }

        //게시글 좋아요
        restaurantLikeDBReference = Firebase.database.getReference("KueatDB/LikedArticle")
        restaurantLikeDBReference.child(articleKey + user!!.uid).get().addOnSuccessListener {
            isLikedArticle = it.exists()
            if (isLikedArticle)
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
        binding.ivAppealArticleDetailCommentButton.setOnClickListener {
            if(!user!!.isEmailVerified){
                Toast.makeText(this, "댓글을 작성하려면 학교인증이 필요합니다", Toast.LENGTH_SHORT).show()
                binding.etAppealArticleDetailComment.text.clear()
                return@setOnClickListener
            }
            val key = commentDBReference.push().key
            val currentTime = System.currentTimeMillis()
            val dataFormat = SimpleDateFormat("MM/dd HH:mm")
            val current = dataFormat.format(currentTime)
            if(TextUtils.isEmpty(binding.etAppealArticleDetailComment.text.toString().trim())){
                Toast.makeText(this, "공백이 아닌 댓글 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                binding.etAppealArticleDetailComment.text.clear()
                return@setOnClickListener
            }
            val comment = Comment(
                key!!,
                user!!.uid,
                article.article_id,
                binding.etAppealArticleDetailComment.text.toString(),
                0,
                0,
                current,
                mutableMapOf()
            )
            binding.etAppealArticleDetailComment.text.clear()

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
                val c = adapter.getItem(position)
                commentDBReference.child(c.comment_id).runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val p = currentData.getValue(Comment::class.java)
                        Log.d("qwerty123", p.toString())

                        if (p!!.liked_user.containsKey(user!!.uid)) {
                            p!!.liked_user_number = p!!.liked_user_number - 1
                            p!!.liked_user.remove(user!!.uid)
                        } else {
                            p!!.liked_user_number = p!!.liked_user_number + 1
                            p!!.liked_user[user!!.uid] = true
                        }

                        currentData.value = p
                        Log.d("qwerty123", currentData.value.toString())
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        Log.d("qwerty123", error.toString())
                    }

                })
            }
        }


        binding.ivAppealArticleBack.setOnClickListener {
            finish()
        }
    }
}