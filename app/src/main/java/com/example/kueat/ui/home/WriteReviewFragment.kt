package com.example.kueat.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kueat.R
import com.example.kueat.databinding.FragmentReviewBinding
import com.example.kueat.databinding.FragmentWriteReviewBinding
import com.example.kueat.`object`.Article
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar

class WriteReviewFragment : Fragment() {
    var binding:FragmentWriteReviewBinding ?=null
    lateinit var dbReview : DatabaseReference
    var rest_id = 0
    val user = Firebase.auth.currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rest_id = arguments?.getString("rest_id")!!.toInt()
        binding = FragmentWriteReviewBinding.inflate(inflater,container,false)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    fun initLayout(){
        dbReview = Firebase.database.getReference("KueatDB/Article")
        binding!!.apply {
            tvConfirmEditArticle.setOnClickListener {

            }
            ivEditAppealArticleBack.setOnClickListener {
                val currentTime = Calendar.getInstance().time
                val dataFormat = SimpleDateFormat("MM/dd HH:MM")
                val current = dataFormat.format(currentTime)
                val key = dbReview.push().key
                val item = Article(0,0,rest_id,0,title.text.toString(),context.text.toString()
                    ,0,0,current)
                dbReview.child(key!!).setValue(item)
                dbReview.child(key!!).child("article_id").setValue(key)
                dbReview.child(key!!).child("user_id").setValue(user!!.uid)
                this@WriteReviewFragment.onDestroy()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}