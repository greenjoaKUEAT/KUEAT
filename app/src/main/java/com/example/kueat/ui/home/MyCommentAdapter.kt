package com.example.kueat.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.ItemAppealArticleCommentBinding
import com.example.kueat.`object`.Comment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MyCommentAdapter(options: FirebaseRecyclerOptions<Comment>)
    : FirebaseRecyclerAdapter<Comment, MyCommentAdapter.ViewHolder>(options) {
//    interface OnItemClickListener {
//        fun OnItemClick(pos: Int)
//    }
   // var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: ItemAppealArticleCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
//            binding.root.setOnClickListener {
//                itemClickListener!!.OnItemClick(bindingAdapterPosition)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemAppealArticleCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Comment) {
        holder.binding.apply {
            // 초기화 작업
            tvAppealArticleCommentConetent.text = model.content.toString()
            tvAppealArticleCommentDate.text = model.date
            tvAppealArticleCommentUser.text = model.user_id.toString()
            tvAppealArticleCommentLikedUser.text = model.liked_user_number.toString()
        }
    }
}