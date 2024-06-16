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
import com.geby.stuntshield.ui.MainActivity
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.buttonGoogle.setOnClickListener {
            authViewModel.googleSignIn(this, getString(R.string.your_web_client_id))
        }

        register()
        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.user.observe(this) { result ->
            result?.let {
                it.fold(
                    onSuccess = { user ->
                        Log.d("Register", "${user?.displayName} berhasil registrasi.")
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.register_success_title))
                            setMessage(getString(R.string.register_success_msg))
                            setPositiveButton("OK") { _, _ ->
                                updateUi(user)
                            }
                            create()
                            show()
                        }
                    },
                    onFailure = { exception ->
                        Log.w("Register", "Register gagal")
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.register_failed_title))
                            setMessage(exception.message)
                            setPositiveButton("OK") { _, _ -> }
                            create()
                            show()
                        }
                    }
                )
            }
        }
        authViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
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
            val intent = if (authViewModel.isGoogleSignIn) {
                Intent(this@RegisterActivity, MainActivity::class.java)
            } else {
                Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                    action = "com.example.action.REGISTRATION_FLOW"
                }
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}