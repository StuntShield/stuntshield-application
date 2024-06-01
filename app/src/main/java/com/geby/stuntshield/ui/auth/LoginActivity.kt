package com.geby.stuntshield.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.geby.stuntshield.MainActivity
import com.geby.stuntshield.R
import com.geby.stuntshield.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = FirebaseOptions.Builder()
            .setApiKey("AIzaSyAs_cmapQSa7T9Ovu5rbqScJCDtRxh12F4")
            .setApplicationId("stunshield-application")
            .build()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this, options)
        }

        firebaseAuth = Firebase.auth
        loginUser()
    }

    private fun loginUser() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                setupAction(email, password)
            } else {
                Log.d("LoginActivity", "Email or Password is empty")
            }
        }
    }

    private fun setupAction(email: String, password: String) {
       try {
           firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
               if (task.isSuccessful) {
                   Log.d("LoginActivity", "LogInWithEmail: Success")
                   val user = firebaseAuth.currentUser
                   updateUI(user)
               } else {
                   Log.w("LoginActivity", "LogInWithEmail: Failure", task.exception)
                   AlertDialog.Builder(this).apply {
                       setTitle(getString(R.string.login_failed_title))
                       setMessage(getString(R.string.login_failed_msg))
                       setPositiveButton("OK") { _, _ ->
                       }
                       create()
                       show()
                   }
               }
           }
       } catch (e: Exception) {
           Log.e("LoginActivity", "Error in setupAction", e)
       }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            try {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error in updateUI", e)
            }
        }
    }
}