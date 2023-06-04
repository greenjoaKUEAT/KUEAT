package com.example.kueat.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.kueat.MainActivity
import com.example.kueat.R
import com.example.kueat.StartActivity
import com.example.kueat.databinding.FragmentRegisterBinding
import com.example.kueat.ui.filter.FilterLocFragment
import com.example.kueat.ui.filter.FilterMenuFragment
import com.example.kueat.ui.login.LoginFragment
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class RegisterFragment : Fragment() {
    lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    lateinit var inputManager: InputMethodManager

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
        initBtn()
        inputManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        auth = Firebase.auth
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

                createAccount(email, password,id,name,nickname)
            }
        }
        binding.backBtn.setOnClickListener {
            changeStartActivity()
        }
    }

    private fun createAccount(email: String, password: String,id:String,name:String,nickname:String) {
        showProgressBar()
        Log.d("registerFragment", "createAccount:$email")
        if (!validateForm()) {
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
                        updateUI(user, email, password, id, name, nickname)
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
                        updateUI(null, email, password, id, name, nickname)
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
    private fun updateUI(user: FirebaseUser?,email: String, password: String,id:String,name:String,nickname:String) {
        hideProgressBar()
        if (user != null) {
            updateUserInfo(email, password,id,name,nickname)
            changeFragment()
        }else {
            clearText()
        }
    }

    private fun updateUserInfo(email: String, password: String,id:String,name:String,nickname:String) {
        // firebase에 기본 유저 정보는 이메일, 비번, 프로필 사진, 이메일 유효 정보, 정도만 저장되기에 따로 db에 정보 저장해 나머지 학번,이름,닉네임을 저장해야 함.
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


    private fun validateForm(): Boolean {
        var valid = true
        val emailPattern = Regex("""^[a-zA-Z0-9]+@konkuk.ac.kr$""")
        val pwPattern = Regex("""^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}${'$'}""")

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
        if(!valid)
            clearText()

        return valid
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