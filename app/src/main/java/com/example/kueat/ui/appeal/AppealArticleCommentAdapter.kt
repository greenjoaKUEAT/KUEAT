package com.example.kueat.ui.appeal

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.R
import com.example.kueat.databinding.ItemAppealArticleCommentBinding
import com.example.kueat.`object`.Comment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AppealArticleCommentAdapter(
    var options: FirebaseRecyclerOptions<Comment>,
    var uid: String,
    var context: Context
) :
    FirebaseRecyclerAdapter<Comment, AppealArticleCommentAdapter.ViewHolder>(options) {

    var OnItemClickListener: onItemClickListener? = null

    interface onItemClickListener {
        fun onItemClicked(position: Int)
    }

    inner class ViewHolder(val binding: ItemAppealArticleCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvAppealArticleCommentLikedUserButton.setOnClickListener {
                OnItemClickListener?.onItemClicked(bindingAdapterPosition)
            }
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
        val userDBReference = Firebase.database.getReference("KueatDB/User")
        userDBReference.child(model.user_id).get().addOnSuccessListener {
            val map = it.getValue() as HashMap<String, Any>
            holder.binding.tvAppealArticleCommentUser.text = map.get("nickname").toString()
        }

        holder.binding.apply {
            tvAppealArticleCommentConetent.text = model.content
            tvAppealArticleCommentDate.text = model.date
            if (model.liked_user.containsKey(uid)) {
                tvAppealArticleCommentLikedUser.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.pink
                    )
                )
            } else {
                tvAppealArticleCommentLikedUser.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
            }
            tvAppealArticleCommentLikedUser.text = model.liked_user_number.toString()
        }
    }
}