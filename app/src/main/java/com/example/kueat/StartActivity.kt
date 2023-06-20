package com.example.kueat

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.kueat.databinding.ActivityStartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class StartActivity : AppCompatActivity() {
    lateinit var binding:ActivityStartBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        // Initialize Firebase Auth
        auth = Firebase.auth
        setContentView(binding.root)
        val callback = onBackPressedDispatcher.addCallback(this) {
            backPressed()
        }
        initBtn()
        checkLocationPermission()
    }

//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        if(allPermissionGranted()) {
//
//        }else{
//            checkLocationPermission()
//        }
//    }

    //로그인 된 유저이면 InitActivity의 FilterLocFragment로 화면전환
    private fun reload() {
        val initIntent = Intent(this@StartActivity,InitActivity::class.java)
        startActivity(initIntent)
    }

    private fun backPressed() {
        finishAffinity()
    }

    private fun initBtn() {
        binding.apply{
            loginBtn.setOnClickListener{
                if(allPermissionGranted()) {
                    val initIntent = Intent(this@StartActivity, InitActivity::class.java)
                    initIntent.putExtra("mode", "login")
                    startActivity(initIntent)
                }else{
                    checkLocationPermission()
                }
            }
            registerBtn.setOnClickListener{
                if(allPermissionGranted()) {
                    val initIntent = Intent(this@StartActivity, InitActivity::class.java)
                    initIntent.putExtra("mode", "register")
                    startActivity(initIntent)
                }else{
                    checkLocationPermission()
                }
            }
        }
    }

    val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun allPermissionGranted() = permissions.all{
        ActivityCompat.checkSelfPermission(this,it)==PackageManager.PERMISSION_GRANTED }
    @SuppressLint("MissingPermission")
    private fun checkLocationPermission() {
        when {
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            -> {
//                Toast.makeText(this,"권한이 승인되었습니다.",Toast.LENGTH_SHORT).show()
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    reload()
                }
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)||
                ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)->{
            permissionCheckAlertDlg()
        }

            else -> {

                multiplePermissionLauncher.launch(permissions)
            }
        }
    }


    val multiplePermissionLauncher
            = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        val resultPermission = it.all{map->
            map.value
        }
        if(!resultPermission) {
            Toast.makeText(this, "모든 권한 승인되어야 함.", Toast.LENGTH_SHORT).show()
//            finishAffinity()
        }else{
            checkLocationPermission()
        }
    }
    private fun permissionCheckAlertDlg() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("반드시 모든 권한이 허용되어야 합니다.")
            .setTitle("권한체크")
            .setPositiveButton("OK"){
                    _,_->
                multiplePermissionLauncher.launch(permissions)
            }.setNegativeButton("CANCEL"){
                    dlg,_-> dlg.dismiss()
            }
        val dlg = builder.create()
        dlg.show()
    }
}