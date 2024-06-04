package com.geby.stuntshield.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.geby.stuntshield.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val options = FirebaseOptions.Builder()
            .setApiKey("AIzaSyAs_cmapQSa7T9Ovu5rbqScJCDtRxh12F4")
            .setApplicationId("stunshield")
            .build()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this, options)
        }

        with(binding) {
            registerButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    setupAction(username, email, password)
                } else {
                    Log.d("Register Activity", "Semua data harus diisi")
                }
            }

        }
    }

    private fun setupAction(username: String, email: String, password: String) {
        try {
            auth = Firebase.auth
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Register Activity", "Account Created Susscessfully")
                    AlertDialog.Builder(this).apply {
                        setTitle("Register Berhasil")
                        setMessage("Akun Anda berhasil dibuat. Silahkan login")
                        setPositiveButton("OK") { _, _ ->
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        create()
                        show()
                    }
                    val user = auth.currentUser
                    updateProfile(user, username)
                } else {
                    Log.w("Register Activity", "Failed created account", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Register gagal",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Log.e("Register Activity", "Error in setupAction", e)
        }
    }

    private fun updateProfile(user: FirebaseUser?, username: String) {
        user?.let {
            val profileUpdate = userProfileChangeRequest {
                displayName = username
                Log.d("Register Activity", "Username $username")
            }
            it.updateProfile(profileUpdate).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterActivity", "User profile updated.")
//                    updateUi(user)
                } else {
                    Log.w("RegisterActivity", "updateProfile:failure", task.exception)
                }
            }
        }
    }

//    private fun updateUi(user: FirebaseUser?) {
//        if (user != null) {
//            try {
//                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()
//            } catch (e: Exception) {
//                Log.e("Register Activity", "Error in updateUi")
//            }
//        }
//    }
}