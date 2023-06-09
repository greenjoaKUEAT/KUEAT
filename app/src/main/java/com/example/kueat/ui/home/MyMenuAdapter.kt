package com.example.kueat.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.MenurowBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MyMenuAdapter(options: FirebaseRecyclerOptions<Menu>)
    : FirebaseRecyclerAdapter<Menu, MyMenuAdapter.ViewHolder>(options){
    interface OnItemClickListener {
        fun OnItemClick(pos: Int)
    }
    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: MenurowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
//            binding.root.setOnClickListener {
//                itemClickListener!!.OnItemClick(bindingAdapterPosition)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MenurowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Menu) {
        holder.binding.apply {
            // 초기화 작업
            name.text= model.name.toString()
            context.text = model.content.toString()
            price.text = model.price.toString()
        }
    }

}