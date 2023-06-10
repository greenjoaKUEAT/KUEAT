package com.example.kueat.ui.appeal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.ItemAppealArticleBinding
import com.example.kueat.`object`.Article

class AppealArticleAdapter(val items: ArrayList<Article>) : RecyclerView.Adapter<AppealArticleAdapter.ViewHolder>() {

    var OnItemClickListener: onItemClickListener? = null

    interface onItemClickListener{
        fun onItemClicked(position: Int)
    }

    inner class ViewHolder(val binding: ItemAppealArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            val article = items[position]
            binding.tvAppealArticleTitle.text = article.title
            binding.tvAppealArticleDescription.text = article.content
            binding.tvAppealArticleLike.text = article.liked_user_number.toString()
            binding.tvAppealArticleComment.text = article.comment_number.toString()
            binding.llAppealArticle.setOnClickListener {
                OnItemClickListener?.onItemClicked(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppealArticleAdapter.ViewHolder {
        val binding = ItemAppealArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppealArticleAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}