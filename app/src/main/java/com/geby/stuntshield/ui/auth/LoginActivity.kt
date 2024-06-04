package com.geby.stuntshield.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.geby.stuntshield.R
import com.geby.stuntshield.databinding.ActivityLoginBinding
import com.geby.stuntshield.ui.MainActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = FirebaseOptions.Builder()
            .setApiKey("AIzaSyAs_cmapQSa7T9Ovu5rbqScJCDtRxh12F4")
            .setApplicationId("stunshield")
            .build()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this, options)
        }

        authViewModel.authResult.observe(this) { result ->
            result.onSuccess { user ->
                Log.d("Login", "Login dengan email ${user?.email} berhasil")
                updateUI(user)
            }.onFailure {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.login_failed_title))
                    setMessage(getString(R.string.login_failed_msg))
                    setPositiveButton("OK") { _, _ -> }
                    create()
                    show()
                }
            }
        }

        loginUser()

        authViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun loginUser() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.loginUser(email, password)
            } else {
                Toast.makeText(this, "Email atau Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}