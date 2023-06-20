package com.example.kueat.ui.appeal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.databinding.FragmentAppealBinding
import com.example.kueat.`object`.Article
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AppealFragment : Fragment() {
    lateinit var binding: FragmentAppealBinding
    lateinit var adapter: AppealArticleAdapter
    lateinit var articleDbReference: DatabaseReference
    var user = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppealBinding.inflate(inflater, container, false)
        initLayout()
        return binding.root
    }

    fun initLayout(){
        articleDbReference = Firebase.database.getReference("KueatDB/Article/1")

        binding.rvAppealArticle.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        var query = articleDbReference.orderByChild("date").limitToFirst(50)
        val articleOptions = FirebaseRecyclerOptions.Builder<Article>()
            .setQuery(query, Article::class.java).build()
        adapter = AppealArticleAdapter(articleOptions)
        adapter.startListening()
        adapter.OnItemClickListener = object: AppealArticleAdapter.onItemClickListener {
            override fun onItemClicked(position: Int) {
                val i =  Intent(requireContext(), AppealArticleDetailActivity::class.java)
                i.putExtra("article_key", adapter.getItem(position).article_id)
                requireActivity().startActivity(i)
            }
        }
        binding.rvAppealArticle.adapter = adapter

        binding.llAddAppealArticleButton.setOnClickListener {
            if(!user!!.isEmailVerified){
                Toast.makeText(requireContext(), "학교인증을 하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val i =  Intent(requireContext(), EditAppealArticleActivity::class.java)
            requireActivity().startActivity(i)
        }
    }
}