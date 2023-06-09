package com.example.kueat.ui.home

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.kueat.R
import com.example.kueat.databinding.FragmentReviewBinding
import com.example.kueat.databinding.FragmentWriteReviewBinding
import com.example.kueat.`object`.Article
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar

class WriteReviewFragment : Fragment() {
    var binding:FragmentWriteReviewBinding ?=null
    lateinit var dbReview : DatabaseReference
    var rest_id = 0
    lateinit var dbRest :DatabaseReference
    val user = Firebase.auth.currentUser
    var write = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rest_id = arguments?.getString("rest_id")!!.toInt()
        binding = FragmentWriteReviewBinding.inflate(inflater,container,false)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    fun initLayout(){
        dbRest = Firebase.database.getReference("KueatDB/Restaurant")
        dbReview = Firebase.database.getReference("KueatDB/Article/0")
        binding!!.apply {
            tvConfirmEditArticle.setOnClickListener {
                if(write == 1) {
                    write = 0
                    val currentTime = Calendar.getInstance().time
                    val dataFormat = SimpleDateFormat("MM/dd HH:mm")
                    val current = dataFormat.format(currentTime)
                    val key = dbReview.push().key

                    if(TextUtils.isEmpty(context.text.toString().trim()) || TextUtils.isEmpty(title.text.toString().trim())){
                        Toast.makeText(requireContext(), "공백이 아닌 제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                        context.text.clear()
                        title.text.clear()
                        write=1
                        return@setOnClickListener
                    }

                    val item = Article(key!!, user!!.uid, rest_id, 0, title.text.toString()
                        , context.text.toString(), 0, 0, current)
                    dbReview.child(key!!).setValue(item).addOnSuccessListener {
                        write = 1
                        @Suppress("DEPRECATION")
                        activity?.onBackPressed()
                        dbRest.child(rest_id.toString())
                            .get().addOnSuccessListener {
                                val map = it.getValue() as HashMap<String,Any>
                                val size = map.get("article_number").toString().toInt()
                                dbRest.child(rest_id.toString()).child("article_number").setValue(size+1)
                            }

                    }
                    dbRest.child(rest_id.toString()).child("article_number")
                }

            }
            ivEditAppealArticleBack.setOnClickListener {
                @Suppress("DEPRECATION")
                activity?.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}