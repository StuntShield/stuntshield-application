package com.geby.stuntshield.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.geby.stuntshield.R
import com.geby.stuntshield.databinding.ActivityLoginBinding
import com.geby.stuntshield.ui.MainActivity
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonGoogle.setOnClickListener {
            authViewModel.googleSignIn(this, getString(R.string.your_web_client_id))
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.loginUser(email, password)
            } else {
                Toast.makeText(this, "Email atau Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()

        authViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun observeViewModel() {
        authViewModel.user.observe(this) { result ->
            result?.let {
                it.fold(
                    onSuccess = { user -> updateUI(user) },
                    onFailure = {
                        if (!authViewModel.initialCheck) {
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.login_failed_title))
                                setMessage(getString(R.string.login_failed_msg))
                                setPositiveButton("OK") { _, _ -> }
                                create()
                                show()
                            }
                        }
                    }
                )
            }
        }
        authViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (intent.action == "com.example.action.REGISTRATION_FLOW") {
            Toast.makeText(this, "Please log in with your new account.", Toast.LENGTH_SHORT).show()
        } else if (currentUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (intent.action != "com.example.action.REGISTRATION_FLOW") {
            authViewModel.checkCurrentUser()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}