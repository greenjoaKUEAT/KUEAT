package com.example.kueat.ui.account

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kueat.*
import com.example.kueat.databinding.FragmentAccountBinding
import com.example.kueat.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {

    lateinit var binding: FragmentAccountBinding
    private lateinit var auth: FirebaseAuth
    val user = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initText()
        initBtn()
        initUserInfo()

        auth = Firebase.auth
    }

    private fun initUserInfo() {
        val name = user?.displayName
        val email = user?.email

        binding.apply {
            infoEmail.text = email
        }
    }

    private fun initBtn() {
        binding.apply{
            textLogout.setOnClickListener{
                auth.signOut()
                Toast.makeText(context,"로그아웃되었습니다.",Toast.LENGTH_SHORT).show()
                val intent = Intent(requireActivity(),StartActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initText() {
        var textAppeal = binding.countAppeal
        var textReview = binding.countReview
        var textComment = binding.countComment
        val dataAppeal = textAppeal.text.toString()
        val dataReview = textReview.text.toString()
        val dataComment = textComment.text.toString()
        val builderAppeal = SpannableStringBuilder(dataAppeal)
        val builderReview = SpannableStringBuilder(dataReview)
        val builderComment = SpannableStringBuilder(dataComment)

        val colorPinkSpan = ForegroundColorSpan(Color.rgb(255, 156, 156))
        builderAppeal.setSpan(colorPinkSpan,5,dataAppeal.length-1,SPAN_EXCLUSIVE_INCLUSIVE)
//        builderAppeal.setSpan(UnderlineSpan(),5,dataAppeal.length-1,SPAN_EXCLUSIVE_INCLUSIVE)
        builderReview.setSpan(colorPinkSpan,5,dataReview.length-1,SPAN_EXCLUSIVE_INCLUSIVE)
//        builderReview.setSpan(UnderlineSpan(),5,dataReview.length-1,SPAN_EXCLUSIVE_INCLUSIVE)
        builderComment.setSpan(colorPinkSpan,5,dataComment.length-1,SPAN_EXCLUSIVE_INCLUSIVE)
//        builderComment.setSpan(UnderlineSpan(),5,dataComment.length-1,SPAN_EXCLUSIVE_INCLUSIVE)
        textAppeal.text = builderAppeal
        textReview.text = builderReview
        textComment.text = builderComment
    }
}