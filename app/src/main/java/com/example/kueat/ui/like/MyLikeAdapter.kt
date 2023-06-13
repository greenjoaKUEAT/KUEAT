package com.example.kueat.ui.like

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kueat.databinding.LikerowBinding
import com.example.kueat.`object`.Restaurant
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyLikeAdapter(val items:ArrayList<Restaurant>)
    : RecyclerView.Adapter<MyLikeAdapter.ViewHolder>(){
    private val mainscope = CoroutineScope(Dispatchers.Main)
    lateinit var dbMenu: DatabaseReference
    private val TAG="likeFragment"

    interface OnItemClickListener{
        fun onItemClick(holder: ViewHolder,position:Int)
    }
    var itemClickListener:OnItemClickListener?=null
    inner class ViewHolder(val binding:LikerowBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener{
                itemClickListener?.onItemClick(this,bindingAdapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyLikeAdapter.ViewHolder {
        val view = LikerowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getRepMenu(restaurant_id: Long):Deferred<String>{
        val restaurantId = restaurant_id
        dbMenu = Firebase.database.getReference("KueatDB/Menu")
        var signatureArr:ArrayList<String> = arrayListOf()
        var signatureStr = ""

        return mainscope.async{
            withContext(Dispatchers.IO){
                dbMenu.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (data in it.result.children) {
                            Log.e(
                                TAG,
                                "ValueEventListener-onDataChange : ${data.value}",
                            )
                            val datas = data.value as Map<String, String>
                            var resId = datas["restaurant_id"]!!.toLong()
                            Log.e(
                                TAG,
                                "menu : ${datas["restaurant_id"]}&$restaurantId / ${datas["signature"]}",
                            )

                            if ((resId==restaurantId)&& datas["signature"]!!.equals("1")) {
                                Log.e(
                                    TAG,
                                    "ValueEventListener-onDataChange : ${datas["restaurant_id"]}&$restaurantId",
                                )
                                datas["name"]?.let { it1 -> signatureArr.add(it1) }
                            }
                        }
                    }
                }.await()
            }

            for (i in signatureArr) {
                signatureStr += i
                if (i != signatureArr[signatureArr.size - 1])
                    signatureStr += ", "
            }
            if(signatureArr.size==0)
                signatureStr = "다"
            return@async signatureStr
        }
    }

    override fun onBindViewHolder(
        holder: MyLikeAdapter.ViewHolder,
        position: Int
    ) {
        holder.binding.apply{
            textRestaurantName.text = items[position].name
            textTag.text = "#${items[position].tag_location} #${items[position].tag_type}"
            textRate.text = items[position].rating
            mainscope.launch {
                textSignature.text = "${getRepMenu(items[position].restaurant_id).await()}"
                textEnd.visibility = View.VISIBLE
            }
        }
    }


}