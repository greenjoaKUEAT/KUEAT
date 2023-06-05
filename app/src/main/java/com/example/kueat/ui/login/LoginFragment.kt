package com.example.kueat.ui.login

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
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
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.kueat.R
import com.example.kueat.StartActivity
import com.example.kueat.databinding.FragmentLoginBinding
import com.example.kueat.ui.filter.FilterLocFragment
import com.example.kueat.ui.filter.FilterMenuFragment
import com.google.android.play.integrity.internal.w
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    lateinit var inputManager:InputMethodManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtn()
        inputManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        auth = Firebase.auth
    }

    private fun initBtn() {
        binding.apply {

            loginBtn.setOnClickListener {
                val email = emailInputText.text.toString()
                val password = passwordInputText.text!!.toString()

                var view = activity?.currentFocus
                if (view == null) {view = View(activity)}
                inputManager.hideSoftInputFromWindow(view.windowToken, 0);

                signIn(email,password)
            }
            backBtn.setOnClickListener {
                changeActivity()
            }
        }
    }
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
    private fun signIn(email: String, password: String) {
        showProgressBar()
        Log.d("loginFragment", "signIn:$email")
        if (!validateForm()) {
            hideProgressBar()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("loginFragment", "signInWithEmail:success")
                    Toast.makeText(context,"로그인 성공", Toast.LENGTH_SHORT,).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("loginFragment", "signInWithEmail:failure", task.exception)
                    when(task.exception){
                        is FirebaseAuthInvalidCredentialsException -> Toast.makeText(context,"잘못된 비밀번호", Toast.LENGTH_SHORT,).show()
                        is FirebaseAuthInvalidUserException -> Toast.makeText(context,"존재하지 않는 email입니다.", Toast.LENGTH_SHORT,).show()
                        is FirebaseTooManyRequestsException -> Toast.makeText(context,"잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT,).show()
                        else -> clearText()
                    }

                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressBar()
        if (user != null) {
            changeFragment()
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
            clearText()
            binding.emailInputText.hint = "유효한 email이 아닙니다."
            valid = false
        } else if(!emailPattern.matches(email)) {
//            Toast.makeText(activity,"건국대학교 email이 아닙니다.",Toast.LENGTH_SHORT).show()
            clearText()
            binding.emailInputText.hint = "건국대학교 email이 아닙니다."
            valid = false
        }

        val password = binding.passwordInputText.text.toString()
        if (TextUtils.isEmpty(password)||!pwPattern.matches(password)) {
            Toast.makeText(activity,"유효한 password가 아닙니다.",Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }

    private fun clearText() {
        binding.apply{
            emailInputText.text.clear()
            passwordInputText.text?.clear()
        }
    }

    fun changeFragment(){
        val fragment = requireActivity().supportFragmentManager
        fragment.beginTransaction().replace(R.id.init_frm, FilterLocFragment()).commit()
    }
    fun changeActivity(){
        val intent = Intent(requireActivity(), StartActivity::class.java)
        requireActivity().startActivity(intent)
    }
}