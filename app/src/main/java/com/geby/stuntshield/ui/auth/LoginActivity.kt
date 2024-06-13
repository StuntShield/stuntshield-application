package com.geby.stuntshield.ui.auth
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.geby.stuntshield.R
import com.geby.stuntshield.data.local.pref.UserPreference
import com.geby.stuntshield.data.local.pref.dataStore
import com.geby.stuntshield.databinding.ActivityLoginBinding
import com.geby.stuntshield.ui.MainActivity
import com.geby.stuntshield.ui.MainViewModel
import com.geby.stuntshield.ui.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
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

        val pref = UserPreference.getInstance(application.dataStore)

        // Mendapatkan instance ViewModelFactory dengan UserPreference
        val viewModelFactory = ViewModelFactory(pref)

        // Mendapatkan instance MainViewModel dengan ViewModelFactory yang sudah memiliki UserPreference
        val preferenceViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        val options = FirebaseOptions.Builder()
            .setApiKey("AIzaSyAs_cmapQSa7T9Ovu5rbqScJCDtRxh12F4")
            .setApplicationId("stuntshield")
            .build()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this, options)
        }

        authViewModel.configureGoogleSignIn(this)

        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("RegisterActivity", "firebaseAuthWithGoogle:" + account.id)
                    authViewModel.signInWithGoogle(account)
                } catch (e: ApiException) {
                    Log.w("RegisterActivity", "Google sign in failed", e)
                    Toast.makeText(this, "Login dengan Google gagal: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.buttonGoogle.setOnClickListener {
            val signInIntent = authViewModel.getGoogleSignInClient().signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        authViewModel.authResult.observe(this) { result ->
            result.onSuccess { user ->
                if (user != null) {
                    preferenceViewModel.saveUserId(user.uid)
                }
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