package com.example.kueat.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kueat.R
import com.example.kueat.StartActivity
import com.example.kueat.databinding.FragmentRegisterBinding
import com.example.kueat.`object`.User
import com.example.kueat.ui.filter.FilterLocFragment
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class RegisterFragment : Fragment() {
    lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    lateinit var inputManager: InputMethodManager
    lateinit var kueatDB_user: DatabaseReference
    private val mainscope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDB()
        initBtn()
        inputManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        auth = Firebase.auth
    }

    private fun initDB() {
        kueatDB_user = Firebase.database.getReference("KueatDB/User")


    }

    private fun initBtn() {
        binding.registerBtn.setOnClickListener {
            binding.apply{
                val email = emailInputText.text.toString()
                val password = passwordInputText.text.toString()
                val id = idInputText.text.toString()
                val name = nameInputText.text.toString()
                val nickname = nicknameInputText.text.toString()

                var view = activity?.currentFocus
                if (view == null) {view = View(activity)}
                inputManager.hideSoftInputFromWindow(view.windowToken, 0);
                mainscope.launch {
                    createAccount(email, password,id,name,nickname)
                }
            }
        }
        binding.backBtn.setOnClickListener {
            changeStartActivity()
        }
    }

    private suspend fun createAccount(email: String, password: String, id:String, name:String, nickname:String) {
        showProgressBar()
        Log.d("registerFragment", "createAccount:$email")

        if (!(validateForm().await())) {
            hideProgressBar()
            return
        }
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("registerFragment", "createUserWithEmail:success")
                        Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT,).show()
                        val user = auth.currentUser
                        updateUI(user, email, name, nickname, id)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("registerFragment", "createUserWithEmail:failure", task.exception)
                        if(task.exception is FirebaseAuthUserCollisionException)
                            binding.emailInputText.hint = "이미 등록된 email입니다."
                        when(task.exception){
                            is FirebaseAuthUserCollisionException -> binding.emailInputText.hint = "이미 등록된 email입니다."
                            is FirebaseTooManyRequestsException -> Toast.makeText(context,"잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT,).show()
                            else -> clearText()
                        }
                        updateUI(null, email, name, nickname, id)
                    }

                }
        }catch (e: FirebaseAuthUserCollisionException){
            Toast.makeText(context,e.errorCode,Toast.LENGTH_SHORT).show()
        }
    }
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
    private fun updateUI(user: FirebaseUser?,email: String,name:String,nickname:String,id:String) {
        hideProgressBar()
        if (user != null) {
            updateUserInfo(email,name,nickname,id)
            changeFragment()
        }else {
            clearText()
        }
    }

    private fun updateUserInfo(email: String,name:String,nickname:String,id:String) {
        // firebase에 기본 유저 정보는 이메일, 비번, 프로필 사진, 이메일 유효 정보, 정도만 저장되기에 따로 db에 정보 저장해 나머지 학번,이름,닉네임을 저장해야 함.
        val user = Firebase.auth.currentUser
        val userInfo = user?.let { User(it.uid,email,name,nickname,id) }

        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("registerFragment", "User profile updated.")
                    user?.uid?.let { kueatDB_user.child(it).setValue(userInfo) }
                }
            }
    }

    private fun clearText() {
        binding.apply{
            emailInputText.text.clear()
            passwordInputText.text?.clear()
            idInputText.text.clear()
            nameInputText.text.clear()
            nicknameInputText.text.clear()
        }
    }


    private fun validateForm(): Deferred<Boolean> {
        var valid = true
        val emailPattern = Regex("""^[a-zA-Z0-9]+@konkuk.ac.kr$""")
        val pwPattern = Regex("""^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}${'$'}""")
        val namePattern = Regex("""^.{1,20}${'$'}""")
        val nicknamePattern = Regex("""^.{1,15}${'$'}""")
        val idPattern = Regex("""^\d{9}${'$'}""")

        binding.emailInputText.hint = ""

        val email = binding.emailInputText.text.toString()
        if (TextUtils.isEmpty(email)||!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(activity,"유효한 email이 아닙니다.",Toast.LENGTH_SHORT).show()
            binding.emailInputText.hint = "유효한 email이 아닙니다."
            valid = false
        } else if(!emailPattern.matches(email)) {
//            Toast.makeText(activity,"건국대학교 email이 아닙니다.",Toast.LENGTH_SHORT).show()
            binding.emailInputText.hint = "건국대학교 email이 아닙니다."
            valid = false
        }

        val password = binding.passwordInputText.text.toString()
        if (TextUtils.isEmpty(password)||!pwPattern.matches(password)) {
            Toast.makeText(activity,"유효한 password가 아닙니다.",Toast.LENGTH_SHORT).show()
            valid = false
        }

        val name = binding.nameInputText.text.toString()
        if (TextUtils.isEmpty(name)||!namePattern.matches(name)) {
            binding.nameInputText.hint = "1~20자 사이로 입력해주세요."
            valid = false
        }

        val nickname = binding.nicknameInputText.text.toString()
        if (TextUtils.isEmpty(nickname)||!nicknamePattern.matches(nickname)) {
            binding.nicknameInputText.hint = "1~15자 사이로 입력해주세요."
            valid = false
        }else{
            binding.nicknameInputText.hint = ""
        }

        val id = binding.idInputText.text.toString()
        if (TextUtils.isEmpty(id)||!idPattern.matches(id)) {
            binding.idInputText.hint = "9자리 숫자로 입력해주세요."
            valid = false
        }else{
            binding.idInputText.hint = ""
        }

        return mainscope.async {
            Log.e("registerFragment", "딜레이전",)
            withContext(Dispatchers.IO) {
                kueatDB_user.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (data in it.result.children) {
                            Log.e(
                                "registerFragment",
                                "ValueEventListener-onDataChange : ${data.value}",
                            )
                            val datas = data.value as Map<String, String>
                            if (datas["nickname"].equals(nickname)) {
                                Log.e(
                                    "registerFragment",
                                    "ValueEventListener-onDataChange : ${datas["nickname"]}&$nickname",
                                )
                                Log.e("registerFragment", "닉네임존재")
                                binding.nicknameInputText.hint = "이미 존재하는 닉네임입니다."
                                valid = false
                                break
                            }
                        }
                    }
                }.await()
                kueatDB_user.get().addOnCompleteListener {
                    if(it.isSuccessful){
                        for (data in it.result.children) {
                            Log.e(
                                "registerFragment",
                                "ValueEventListener-onDataChange : ${data.value}",
                            )
                            val datas = data.value as Map<String, String>
                            if (datas["id"].equals(id)) {
                                Log.e(
                                    "registerFragment",
                                    "ValueEventListener-onDataChange : ${datas["id"]}&$id",
                                )
                                Log.e("registerFragment", "학번존재",)
                                binding.idInputText.hint = "이미 존재하는 학번입니다."
                                valid = false
                                break
                            }
                        }
                    }
                }.await()
            }
            Log.e("registerFragment", "딜레이후 : $valid",)
            if(!valid)
                clearText()
            return@async valid
        }
    }

    fun changeStartActivity(){
        val intent = Intent(requireActivity(), StartActivity::class.java)
        requireActivity().startActivity(intent)
    }
    fun changeFragment(){
        val fragment = requireActivity().supportFragmentManager
        fragment.beginTransaction().replace(R.id.init_frm, FilterLocFragment()).commit()
    }
}