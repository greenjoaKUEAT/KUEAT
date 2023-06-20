package com.example.kueat.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.ItemAppealArticleCommentBinding
import com.example.kueat.`object`.Comment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyCommentAdapter(options: FirebaseRecyclerOptions<Comment>)
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
                holder.binding.tvAppealArticleCommentUser.text = it.result.child("nickname").getValue().toString()
            }else{
                holder.binding.tvAppealArticleCommentUser.text = "(알수없음)"
            }
        }

        holder.binding.apply {
            // 초기화 작업
            tvAppealArticleCommentConetent.text = model.content.toString()
            tvAppealArticleCommentDate.text = model.date
            tvAppealArticleCommentLikedUser.text = model.liked_user_number.toString()
        }
    }
}