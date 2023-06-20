package com.example.kueat.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kueat.R
import com.example.kueat.databinding.FragmentReviewBinding
import com.example.kueat.`object`.Comment
import com.example.kueat.ui.appeal.AppealArticleCommentAdapter
import com.example.kueat.ui.appeal.LikedArticle
import com.example.kueat.viewmodel.MyUserModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar

class ReviewFragment : Fragment() {
    var binding:FragmentReviewBinding ?= null
    lateinit var dbReview : DatabaseReference
    lateinit var dbComment : DatabaseReference
    lateinit var commentAdapter: MyCommentAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var restaurantLikeDBReference: DatabaseReference
    lateinit var commentLikeDBReference : DatabaseReference
    var review_id = ""
    val user = Firebase.auth.currentUser
    var num = 0
    var isLikedArticle = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        review_id = arguments?.getString("review_id").toString()
        binding = FragmentReviewBinding.inflate(inflater,container,false)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        dbReview = Firebase.database.getReference("KueatDB/Article/0")
        dbReview.get().addOnSuccessListener {
            val map = it.child(review_id).getValue() as HashMap<String,Any>
            binding!!.apply {
                tvAppealArticleTitle.text = map.get("title").toString()
                tvAppealArticleDescription.text = map.get("content").toString()
                tvAppealArticleLike.text = map.get("liked_user_number").toString()
                tvAppealArticleComment.text = map.get("comment_number").toString()
                val userDBReference = Firebase.database.getReference("KueatDB/User")
                userDBReference.child(map.get("user_id").toString()).get().addOnSuccessListener {
                    val tmp = it.getValue() as HashMap<String, Any>
                    userName.text = tmp.get("nickname").toString()
                }
                reviewDate.text = map.get("date").toString()
            }
        }
        dbComment = Firebase.database.getReference("KueatDB/Comment")
        var query = dbComment.orderByChild("article_id").equalTo(review_id)
        val option = FirebaseRecyclerOptions.Builder<Comment>()
            .setQuery(query, Comment::class.java).build()
        commentAdapter = MyCommentAdapter(option)
        //게시글 좋아요
        restaurantLikeDBReference = Firebase.database.getReference("KueatDB/LikedArticle")
        restaurantLikeDBReference.child(review_id + user!!.uid).get().addOnSuccessListener {
            isLikedArticle = it.exists()
            if(isLikedArticle)
                binding!!.tvAppealArticleLike.setTextColor(getColor(requireContext(),R.color.pink))
        }
        binding!!.likeImage.setOnClickListener {
            if (isLikedArticle) {
                restaurantLikeDBReference.child(review_id + user!!.uid).removeValue()
                binding!!.tvAppealArticleLike.text =  (binding!!.tvAppealArticleLike.text.toString().toInt()- 1).toString()
                binding!!.tvAppealArticleLike.setTextColor(getColor(requireContext(),R.color.black))
            } else {
                restaurantLikeDBReference.child(review_id + user!!.uid).setValue(
                    LikedArticle(
                        review_id,
                        user!!.uid,
                        review_id + user!!.uid
                    )
                )
                binding!!.tvAppealArticleLike.text =  (binding!!.tvAppealArticleLike.text.toString().toInt() + 1).toString()
                binding!!.tvAppealArticleLike.setTextColor(getColor(requireContext(),R.color.pink))
            }
            isLikedArticle = !isLikedArticle
            dbReview.child(review_id).child("liked_user_number").setValue(binding!!.tvAppealArticleLike.text.toString().toInt())
        }

        layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        binding!!.enter.setOnClickListener {
            commentAdapter.stopListening()
            Log.d("reviewFragment","ff")

            val currentTime = Calendar.getInstance().time
            val dataFormat = SimpleDateFormat("MM/dd HH:mm")
            val current = dataFormat.format(currentTime)
            val key = dbComment.push().key
            val item = Comment(key!!,user!!.uid,review_id,binding!!.commentEdit.text.toString(),
            0,0,current, mutableMapOf())
            dbComment.child(key!!).setValue(item)

            dbReview.child(review_id.toString()).get().addOnSuccessListener {
                val map = it.getValue() as HashMap<String,Any>
                num = map.get("comment_number").toString().toInt()
                num +=1
                dbReview.child(review_id.toString()).child("comment_number").setValue(num)
                binding!!.tvAppealArticleComment.text = num.toString()
                commentAdapter.startListening()
            }

            binding!!.commentEdit.text.clear()
        }
        // 댓글 좋아요
        commentLikeDBReference = Firebase.database.getReference("KueatDB/LikedComment")
        commentAdapter.itemClickListener= object : MyCommentAdapter.OnItemClickListener{
            override fun OnItemClick(pos: Int) {
                val c = commentAdapter.getItem(pos)
                dbComment.child(c.comment_id).runTransaction(object : Transaction.Handler {
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



        binding!!.commentrecyclerView.layoutManager = layoutManager
        binding!!.commentrecyclerView.adapter = commentAdapter
    }
    override fun onStart() {
        super.onStart()
        commentAdapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        commentAdapter.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}