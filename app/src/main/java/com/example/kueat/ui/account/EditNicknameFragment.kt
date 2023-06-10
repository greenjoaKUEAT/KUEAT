package com.example.kueat.ui.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.activityViewModels
import com.example.kueat.R
import com.example.kueat.StartActivity
import com.example.kueat.databinding.FragmentEditNicknameBinding
import com.example.kueat.`object`.User
import com.example.kueat.viewmodel.MyUserModel
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class EditNicknameFragment : Fragment() {
    lateinit var binding: FragmentEditNicknameBinding
    private val TAG = "editNickname"
    lateinit var user:FirebaseUser
    private lateinit var auth: FirebaseAuth
    lateinit var inputManager: InputMethodManager
    lateinit var kueatDB: DatabaseReference
    val userModel: MyUserModel by activityViewModels()
    private val mainscope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtn()
        inputManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        auth = Firebase.auth
        kueatDB = Firebase.database.getReference("KueatDB/User")
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun initBtn() {
        binding.apply{
            editBtn.setOnClickListener {
                showProgressBar()

                var view = activity?.currentFocus
                if (view == null) {view = View(activity)}
                inputManager.hideSoftInputFromWindow(view.windowToken, 0);

                user = Firebase.auth.currentUser!!
                val newNickname = nicknameInputText.text.toString()
                val nicknamePattern = Regex("""^.{1,15}${'$'}""")

                if (TextUtils.isEmpty(newNickname)||!nicknamePattern.matches(newNickname)) {
                    hideProgressBar()
                    binding.nicknameInputText.hint = "1~15자 사이로 입력해주세요."
                }else {
                    var valid = true
                    mainscope.launch {
                        withContext(Dispatchers.IO){
                            kueatDB.get().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    for (data in it.result.children) {
                                        Log.e(
                                            TAG,
                                            "ValueEventListener-onDataChange : ${data.value}",
                                        )
                                        val datas = data.value as Map<String, String>
                                        if (datas["nickname"].equals(newNickname)) {
                                            Log.e(
                                                TAG,
                                                "ValueEventListener-onDataChange : ${datas["nickname"]}&$newNickname",
                                            )
                                            Log.e(TAG, "닉네임존재")
                                            binding.nicknameInputText.text.clear()
                                            binding.nicknameInputText.hint = "이미 존재하는 닉네임입니다."
                                            valid = false
                                            break
                                        }
                                    }
                                }
                            }.await()
                        }
                        if(valid)
                            changeNickname(newNickname)
                        hideProgressBar()
                    }
                }

            }
            backBtn.setOnClickListener {
                changeAccountFragment()
            }
        }
    }

    fun changeNickname(newNickname:String){
        val currentUser = kueatDB.child(user!!.uid)
            .child("nickname")
            .setValue(newNickname)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    initUser(newNickname)
                    Log.d(TAG, "User nickname updated.")
                    Toast.makeText(context, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    changeAccountFragment()
                } else {
                    if (task.exception is FirebaseTooManyRequestsException) {
                        Toast.makeText(context, "잠시후 재시도해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    } else if (task.exception is FirebaseAuthRecentLoginRequiredException) {
                        Toast.makeText(context, "다시 로그인 후 이용해주세요.", Toast.LENGTH_SHORT)
                            .show()
                        auth.signOut()
                        changeStartActivity()
                    } else {
                        Toast.makeText(context, "닉네임 변경을 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun initUser(nickname:String) {
        val name = user?.displayName
        val email = user?.email
        val uid = user?.uid
        val id = userModel.selectedUser.value!!.id
        userModel.setLiveData(User(uid!!,email!!,name!!,nickname,id!!))
    }
    private fun changeAccountFragment(){
        val fragment = requireActivity().supportFragmentManager
        fragment.beginTransaction().replace(R.id.main_frm, AccountFragment()).commit()
    }
    private fun changeStartActivity() {
        val intent = Intent(requireActivity(), StartActivity::class.java)
        startActivity(intent)
    }
}