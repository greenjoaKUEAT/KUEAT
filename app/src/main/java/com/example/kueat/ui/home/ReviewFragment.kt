package com.example.kueat.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.databinding.FragmentReviewBinding
import com.example.kueat.`object`.Comment
import com.example.kueat.viewmodel.MyUserModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar

class ReviewFragment : Fragment() {
    var binding:FragmentReviewBinding ?= null
    lateinit var dbReview : DatabaseReference
    lateinit var dbComment : DatabaseReference
    lateinit var commentAdapter: MyCommentAdapter
    lateinit var layoutManager: LinearLayoutManager
    var review_id = ""
    val user = Firebase.auth.currentUser
    var num = 0
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
        dbReview = Firebase.database.getReference("KueatDB/Article")
        dbReview.get().addOnSuccessListener {
            val map = it.child(review_id).getValue() as HashMap<String,Any>
            binding!!.apply {
                tvAppealArticleTitle.text = map.get("title").toString()
                tvAppealArticleDescription.text = map.get("content").toString()
                tvAppealArticleLike.text = map.get("liked_user_number").toString()
                tvAppealArticleComment.text = map.get("comment_number").toString()
            }
        }
        dbComment = Firebase.database.getReference("KueatDB/Comment")
        var query = dbComment.orderByChild("article_id").equalTo(review_id)
        val option = FirebaseRecyclerOptions.Builder<Comment>()
            .setQuery(query, Comment::class.java).build()
        commentAdapter = MyCommentAdapter(option)

        layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        binding!!.enter.setOnClickListener {
            commentAdapter.stopListening()
            val currentTime = Calendar.getInstance().time
            val dataFormat = SimpleDateFormat("MM/dd HH:MM")
            val current = dataFormat.format(currentTime)
            val key = dbComment.push().key
            val item = Comment(key!!,user!!.uid,review_id,binding!!.commentEdit.text.toString(),
            0,0,current)
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}