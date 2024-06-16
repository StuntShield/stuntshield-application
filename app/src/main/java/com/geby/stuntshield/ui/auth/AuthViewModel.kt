package com.geby.stuntshield.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<Result<FirebaseUser?>>()
    val user: LiveData<Result<FirebaseUser?>> get() = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var isGoogleSignIn: Boolean = false

    fun googleSignIn(context: Context, serverClientId: String) {
        _isLoading.value = true
        isGoogleSignIn = true // Set flag to true for Google Sign-In

        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                _user.value = Result.failure(e)
                isGoogleSignIn = false // Reset flag on failure
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        _user.value = Result.failure(e)
                    }
                } else {
                    _user.value = Result.failure(Exception("Unexpected type of credential"))
                }
            }
            else -> {
                _user.value = Result.failure(Exception("Unexpected type of credential"))
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _user.value = Result.success(auth.currentUser)
                } else {
                    _user.value = Result.failure(task.exception ?: Exception("Unknown Error"))
                }
            }
    }

    fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _user.value = Result.success(currentUser)
        } else {
            _user.value = Result.failure(Exception("User not logged in"))
        }
    }

    fun loginUser(email: String, password: String) {
        _isLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _user.value = Result.success(auth.currentUser)
                } else {
                    _user.value = Result.failure(task.exception ?: Exception("Unknown Error"))
                }
            }
    }

    fun registerUser(username: String, email: String, password: String) {
        _isLoading.value = true
        isGoogleSignIn = false // Set flag to false for email/password registration

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val newUser = auth.currentUser
                    updateProfile(newUser, username)
                } else {
                    _user.value = Result.failure(task.exception ?: Exception("Unknown Error"))
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
                    _user.value = Result.success(user)
                } else {
                    _user.value = Result.failure(task.exception ?: Exception("Unknown Error"))
                }
            }
        }
    }
}