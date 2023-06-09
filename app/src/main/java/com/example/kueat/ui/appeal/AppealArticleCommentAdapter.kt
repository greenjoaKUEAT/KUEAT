package com.example.kueat.ui.appeal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.ItemAppealArticleCommentBinding
import com.example.kueat.`object`.Comment

class AppealArticleCommentAdapter(var items: ArrayList<Comment>) : RecyclerView.Adapter<AppealArticleCommentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAppealArticleCommentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            val item = items[position]
            binding.tvAppealArticleCommentUser.text = "익명" + item.user_id.toString()
            binding.tvAppealArticleCommentConetent.text = item.content
            binding.tvAppealArticleCommentDate.text = item.date
            binding.tvAppealArticleCommentLikedUser.text = item.liked_user_number.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppealArticleCommentAdapter.ViewHolder {
        val binding = ItemAppealArticleCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AppealArticleCommentAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }
}