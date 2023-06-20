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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.kueat.*
import com.example.kueat.databinding.FragmentAccountBinding
import com.example.kueat.`object`.Article
import com.example.kueat.`object`.Restaurant
import com.example.kueat.`object`.User
import com.example.kueat.ui.home.HomeFragment
import com.example.kueat.ui.home.RestaurantFragment
import com.example.kueat.viewmodel.MyUserModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {
    lateinit var binding: FragmentAccountBinding
    private lateinit var auth: FirebaseAuth
    var user = Firebase.auth.currentUser!!
    lateinit var kueatDB: DatabaseReference
    val userModel: MyUserModel by activityViewModels()
    private val TAG = "accountFragment"
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

    private fun initBtn() {
        binding.apply{
            textSchool.setOnClickListener {
                sendEmail()
            }
            textPassword.setOnClickListener{
                editPassword()
            }
            textNickname.setOnClickListener {
                editNickname()
            }
            textLogout.setOnClickListener{
                auth.signOut()
                Toast.makeText(context,"로그아웃되었습니다.",Toast.LENGTH_SHORT).show()
                changeStartActivity()
            }
            textOut.setOnClickListener{
                outAlertDlg()
            }
        }
    }

    private fun editNickname() {
        val fragment = requireActivity().supportFragmentManager
        fragment.beginTransaction().replace(R.id.main_frm, EditNicknameFragment()).commit()
    }

    private fun editPassword() {
        val fragment = requireActivity().supportFragmentManager
        fragment.beginTransaction().replace(R.id.main_frm, EditPasswordFragment()).commit()
    }

    private fun sendEmail() {
        user.reload().addOnSuccessListener {
            user = Firebase.auth.currentUser!!
            if(user.isEmailVerified){
                Toast.makeText(context,"인증된 계정입니다.",Toast.LENGTH_SHORT).show()
            }else {
                user.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "이메일이 전송됨.", Toast.LENGTH_SHORT).show()
                            Log.d("registerFragment", "Email sent.")
                        } else {
                            if (task.exception is FirebaseTooManyRequestsException) {
                                Toast.makeText(context, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
            }
        }
    }


    private fun initUserInfo() {
        userModel.selectedUser.observe(viewLifecycleOwner) {
            binding.apply {
                infoName.text = it.name + " / " + it.nickname
                infoEmail.text = it.email
                infoId.text = it.id.substring(2, 4) + "학번"
            }
        }
    }

    private fun outAlertDlg(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("정말로 회원 탈퇴를 하시겠습니까?")
            .setTitle("회원 탈퇴")
            .setPositiveButton("아니요"){
                    dlg,_->dlg.dismiss()

            }.setNegativeButton("네"){
                    _, _->
                run {
                    deleteUser()
                }
            }
        val dlg = builder.create()
        dlg.show()
    }

    private fun deleteUser() {
        val uid = user.uid
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")
                    kueatDB = Firebase.database.getReference("KueatDB/User")
                    kueatDB.child(uid).removeValue()
                    Toast.makeText(context,"회원 탈퇴 성공",Toast.LENGTH_SHORT).show()
                    changeStartActivity()
                }
                else{
                    Toast.makeText(context,"다시 로그인 후 이용해주세요.",Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    changeStartActivity()
                }
            }
    }
    private fun changeStartActivity() {
        val intent = Intent(requireActivity(),StartActivity::class.java)
        startActivity(intent)
    }

    private fun initText() {
        var textAppeal = binding.countAppeal
        var textReview = binding.countReview
        var textComment = binding.countComment
        val colorPinkSpan = ForegroundColorSpan(Color.rgb(255, 156, 156))

        var reviewDB = Firebase.database.getReference("KueatDB/Article/0")
        reviewDB.get().addOnSuccessListener {
            val map = it.getValue() as HashMap<String, Any>
            Log.d("qwerty123", map.toString())
            var size = 0
            for (mutableEntry in map) {
                val e = mutableEntry.value as HashMap<String, Any>
                if(e.get("user_id").toString() == user.uid)
                    size = size + 1
            }
            Log.d("qwerty123", size.toString())
            if(size >= 10)
                textReview.text = "내 리뷰 " + size.toString() + "개"
            else
                textReview.text = "내 리뷰  " + size.toString() + "개"

            val dataReview = textReview.text.toString()
            Log.d("qwerty123", dataReview)
            val builderReview = SpannableStringBuilder(dataReview)
            if (size >= 10)
                builderReview.setSpan(
                    colorPinkSpan,
                    4,
                    dataReview.length - 1,
                    SPAN_EXCLUSIVE_INCLUSIVE
                )
            else
                builderReview.setSpan(
                    colorPinkSpan,
                    5,
                    dataReview.length - 1,
                    SPAN_EXCLUSIVE_INCLUSIVE
                )
            textReview.text = builderReview
        }

        var articleDB = Firebase.database.getReference("KueatDB/Article/1")
        articleDB.get().addOnSuccessListener {
            val map = it.getValue() as HashMap<String, Any>
            Log.d("qwerty123", map.toString())
            var size = 0
            for (mutableEntry in map) {
                val e = mutableEntry.value as HashMap<String, Any>
                if(e.get("user_id").toString() == user.uid)
                    size = size + 1
            }
            Log.d("qwerty123", size.toString())
            if(size >= 10)
                textAppeal.text = "내 추천 " + size.toString() + "개"
            else
                textAppeal.text = "내 추천  " + size.toString() + "개"

            val dataAppeal = textAppeal.text.toString()
            Log.d("qwerty123", dataAppeal)
            val builderAppeal = SpannableStringBuilder(dataAppeal)
            if (size >= 10)
                builderAppeal.setSpan(
                    colorPinkSpan,
                    4,
                    dataAppeal.length - 1,
                    SPAN_EXCLUSIVE_INCLUSIVE
                )
            else
                builderAppeal.setSpan(
                    colorPinkSpan,
                    5,
                    dataAppeal.length - 1,
                    SPAN_EXCLUSIVE_INCLUSIVE
                )
            textAppeal.text = builderAppeal
        }

        var commentDB = Firebase.database.getReference("KueatDB/Comment")
        commentDB.get().addOnSuccessListener {
            val map = it.getValue() as HashMap<String, Any>
            Log.d("qwerty123", map.toString())
            var size = 0
            for (mutableEntry in map) {
                val e = mutableEntry.value as HashMap<String, Any>
                if(e.get("user_id").toString() == user.uid)
                    size = size + 1
            }
            Log.d("qwerty123", size.toString())
            if(size >= 10)
                textComment.text = "내 댓글 " + size.toString() + "개"
            else
                textComment.text = "내 댓글  " + size.toString() + "개"

            val dataComment = textComment.text.toString()
            Log.d("qwerty123", dataComment)
            val builderComment = SpannableStringBuilder(dataComment)
            if (size >= 10)
                builderComment.setSpan(
                    colorPinkSpan,
                    4,
                    dataComment.length - 1,
                    SPAN_EXCLUSIVE_INCLUSIVE
                )
            else
                builderComment.setSpan(
                    colorPinkSpan,
                    5,
                    dataComment.length - 1,
                    SPAN_EXCLUSIVE_INCLUSIVE
                )
            textComment.text = builderComment
        }
    }
}