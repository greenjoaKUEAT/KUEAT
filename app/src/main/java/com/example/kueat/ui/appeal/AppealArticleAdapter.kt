package com.example.kueat.ui.appeal

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.ItemAppealArticleBinding
import com.example.kueat.`object`.Article
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class AppealArticleAdapter(val options: FirebaseRecyclerOptions<Article>) :
    FirebaseRecyclerAdapter<Article, AppealArticleAdapter.ViewHolder>(options) {

    var OnItemClickListener: onItemClickListener? = null

    interface onItemClickListener {
        fun onItemClicked(position: Int)
    }

    inner class ViewHolder(val binding: ItemAppealArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                OnItemClickListener?.onItemClicked(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppealArticleAdapter.ViewHolder {
        val binding =
            ItemAppealArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Article) {
        holder.binding.apply {
            tvAppealArticleTitle.text = model.title
            tvAppealArticleDescription.text = model.content
            tvAppealArticleLike.text = model.liked_user_number.toString()
            tvAppealArticleComment.text = model.comment_number.toString()
            llAppealArticle.setOnClickListener {
                OnItemClickListener?.onItemClicked(position)
            }
        }
    }
}