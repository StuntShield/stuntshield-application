package com.geby.stuntshield.ui.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geby.stuntshield.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val _authResult = MutableLiveData<Result<FirebaseUser?>>()
    val authResult: LiveData<Result<FirebaseUser?>> get() = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun configureGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.your_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleSignInClient
    }

    fun signInWithGoogle(googleSignInAccount: GoogleSignInAccount) {
        val idToken = googleSignInAccount.idToken ?: return
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        signInWithGoogleCredential(credential)
    }

    private fun signInWithGoogleCredential(credential: AuthCredential) {
        _isLoading.value = true
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _authResult.value = Result.success(firebaseAuth.currentUser)
                } else {
                    _authResult.value = Result.failure(task.exception ?: Exception("Unknown Error"))
                }
            }
    }

    fun loginUser(email: String, password: String) {
        _isLoading.value = true
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _authResult.value = Result.success(firebaseAuth.currentUser)
                } else {
                    _authResult.value = Result.failure(task.exception ?: Exception("Unknown Error"))
                }
            }
    }

    fun registerUser(username: String, email: String, password: String) {
        _isLoading.value = true
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val newUser = firebaseAuth.currentUser
                    updateProfile(newUser, username)
                } else {
                    _authResult.value = Result.failure(task.exception ?: Exception("Unknown Error"))
                }
            }
    }

    private fun updateProfile(user: FirebaseUser?, username: String) {
        user?.let {
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()

            it.updateProfile(profileUpdate).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authResult.value = Result.success(user)
                } else {
                    _authResult.value = Result.failure(task.exception ?: Exception("Unknown Error"))
                }
            }
        }
    }
}