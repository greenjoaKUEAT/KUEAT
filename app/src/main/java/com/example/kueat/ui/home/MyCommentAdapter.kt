package com.example.kueat.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.R
import com.example.kueat.databinding.ItemAppealArticleCommentBinding
import com.example.kueat.`object`.Comment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyCommentAdapter(options: FirebaseRecyclerOptions<Comment>, var uid: String, var context: Context)
    : FirebaseRecyclerAdapter<Comment, MyCommentAdapter.ViewHolder>(options) {
    interface OnItemClickListener {
        fun OnItemClick(pos: Int)
    }
    var itemClickListener: OnItemClickListener? = null


    inner class ViewHolder(val binding: ItemAppealArticleCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivAppealArticleCommentLikedUser.setOnClickListener {
                itemClickListener!!.OnItemClick(bindingAdapterPosition)
            }
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
        val ref = Firebase.database.getReference("KueatDB/User")
        val nickname = ref.child(model.user_id).get().addOnCompleteListener {
            if(it.isSuccessful){
                var usr_nickname = it.result.child("nickname").getValue().toString()
                Log.d("check",usr_nickname)
                if(usr_nickname!="null")
                    holder.binding.tvAppealArticleCommentUser.text = usr_nickname
                else
                    holder.binding.tvAppealArticleCommentUser.text = "(알 수 없음)"
            }
        }

        holder.binding.apply {
            // 초기화 작업
            tvAppealArticleCommentConetent.text = model.content.toString()
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