package com.geby.stuntshield.ui.auth
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.geby.stuntshield.R
import com.geby.stuntshield.data.local.pref.UserPreference
import com.geby.stuntshield.data.local.pref.dataStore
import com.geby.stuntshield.databinding.ActivityLoginBinding
import com.geby.stuntshield.ui.MainActivity
import com.geby.stuntshield.ui.MainViewModel
import com.geby.stuntshield.ui.ViewModelFactory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = Firebase.auth

        val pref = UserPreference.getInstance(application.dataStore)

        // Mendapatkan instance ViewModelFactory dengan UserPreference
        val viewModelFactory = ViewModelFactory(pref)

        // Mendapatkan instance MainViewModel dengan ViewModelFactory yang sudah memiliki UserPreference
        val preferenceViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        binding.buttonGoogle.setOnClickListener {
            signIn()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            loginWithEmail(email, password)
        }

//        authViewModel.authResult.observe(this) { result ->
//            result.onSuccess { user ->
//                if (user != null) {
//                    preferenceViewModel.saveUserId(user.uid)
//                }
//                Log.d("Login", "Login dengan email ${user?.email} berhasil")
//                updateUI(user)
//            }.onFailure {
//                AlertDialog.Builder(this).apply {
//                    setTitle(getString(R.string.login_failed_title))
//                    setMessage(getString(R.string.login_failed_msg))
//                    setPositiveButton("OK") { _, _ -> }
//                    create()
//                    show()
//                }
//            }
//        }

//        loginUser()
//
//        authViewModel.isLoading.observe(this) {
//            showLoading(it)
//        }
    }

    private fun signIn() {
        val credentialManager = CredentialManager.create(this) //import from androidx.CredentialManager

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id)) //from https://console.firebase.google.com/project/firebaseProjectName/authentication/providers
            .build()

        val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential( //import from androidx.CredentialManager
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) { //import from androidx.CredentialManager
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        } else {
            Toast.makeText(baseContext, "Email and Password cannot be empty.",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
//                    val pref = UserPreference.getInstance(application.dataStore)
//                    val viewModelFactory = ViewModelFactory(pref)
//                    val preferenceViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

                    val user: FirebaseUser? = auth.currentUser

//                    preferenceViewModel.saveUserId(user?.uid.toString())
//                    preferenceViewModel.saveUserData(user?.displayName.toString(), user?.email.toString(), user?.photoUrl.toString())

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    companion object {
        private const val TAG = "LoginActivity"
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

//    private fun updateUI(user: FirebaseUser?) {
//        if (user != null) {
//            try {
//                val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()
//            } catch (e: Exception) {
//                Log.e("LoginActivity", "Error in updateUI", e)
//            }
//        }
//    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}