package com.example.kueat.ui.appeal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.ItemAppealArticleCommentBinding
import com.example.kueat.`object`.Comment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AppealArticleCommentAdapter(var options: FirebaseRecyclerOptions<Comment>) :
    FirebaseRecyclerAdapter<Comment, AppealArticleCommentAdapter.ViewHolder>(options) {

    inner class ViewHolder(val binding: ItemAppealArticleCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
            init {

            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppealArticleCommentAdapter.ViewHolder {
        val binding = ItemAppealArticleCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Comment) {
        val db = Firebase.database.getReference("KueatDB/LikedComment")
        holder.binding.apply {
            tvAppealArticleCommentUser.text = "익명" + model.user_id.toString()
            tvAppealArticleCommentConetent.text = model.content
            tvAppealArticleCommentDate.text = model.date
            tvAppealArticleCommentLikedUser.text = model.liked_user_number.toString()
        }
    }
}