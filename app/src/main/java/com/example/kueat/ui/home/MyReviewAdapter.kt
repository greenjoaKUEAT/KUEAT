package com.example.kueat.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.ReviewrowBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MyReviewAdapter(options:FirebaseRecyclerOptions<review>)
    : FirebaseRecyclerAdapter<review, MyReviewAdapter.ViewHolder>(options){
    interface OnItemClickListener {
        fun OnItemClick(pos: Int)
    }
    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: ReviewrowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener!!.OnItemClick(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ReviewrowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: review) {
        holder.binding.apply {
            // 초기화 작업
            reviewTitle.text = model.restName.toString()
            reviewContent.text = model.content.toString()
            likeNum.text = model.likenum.toString()
            commentNum.text = model.commentnum.toString()
        }
    }

}