package com.example.kueat.ui.appeal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.databinding.FragmentAppealBinding
import com.example.kueat.`object`.Article
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AppealFragment : Fragment() {
    lateinit var binding: FragmentAppealBinding
    lateinit var adapter: AppealArticleAdapter
    lateinit var articleDbReference: DatabaseReference

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
        articleDbReference = Firebase.database.getReference("KueatDB/Article/0")

        binding.rvAppealArticle.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        var query = articleDbReference.orderByChild("type").equalTo(1.0)
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
            val i =  Intent(requireContext(), EditAppealArticleActivity::class.java)
            requireActivity().startActivity(i)
        }
    }
}