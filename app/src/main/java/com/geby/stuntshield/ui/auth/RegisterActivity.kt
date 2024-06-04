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
import com.geby.stuntshield.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

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

        register()

        authViewModel.authResult.observe(this) { result ->
            result.onSuccess { user ->
                Log.d("Register", "Register berhasil")
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.register_success_title))
                    setMessage(getString(R.string.register_success_msg))
                    setPositiveButton("OK") { _, _ ->
                        updateUi(user)
                    }
                    create()
                    show()
                }
            }
            result.onFailure { exception ->
                Log.w("Register", "Register gagal")
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.register_failed_title))
                    setMessage(exception.message)
                    setPositiveButton("OK") { _, _ -> }
                    create()
                    show()
                }
            }

            authViewModel.isLoading.observe(this) {
                showLoading(it)
            }
        }
    }

    private fun register() {
        with(binding) {
            registerButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    authViewModel.registerUser(username, email, password)
                } else {
                    Toast.makeText(this@RegisterActivity, "Semua data tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            try {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("Register", "Error saat updateUi", e)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}