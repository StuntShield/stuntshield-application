package com.geby.stuntshield.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authResult = MutableLiveData<Result<FirebaseUser?>>()
    val authResult: LiveData<Result<FirebaseUser?>> get() = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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