package com.geby.stuntshield.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geby.stuntshield.data.local.pref.UserPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class ProfileViewModel (private val userPreference: UserPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = firebaseAuth.currentUser

    private val _navigateToWelcome = MutableLiveData<Boolean>()
    val navigateToWelcome: LiveData<Boolean> = _navigateToWelcome

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _createdAt = MutableLiveData<String>()
    val createdAt: LiveData<String> = _createdAt

    private val _profilePictureUri = MutableLiveData<Uri?>()
    val profilePictureUri: LiveData<Uri?>
        get() = _profilePictureUri

    private val storageRef = FirebaseStorage.getInstance().reference

    private val _isLoggedOut = MutableLiveData<Boolean>()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut


    init {
        getUser()
    }

     fun uploadFile(fileUri: Uri) {
        val fileRef = storageRef.child("images/" + UUID.randomUUID().toString())

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                // File uploaded successfully
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    // Update the profile picture URI in ViewModel
                    updateProfilePicture(uri.toString())
                }.addOnFailureListener { e ->
                    // Handle failure to get download URL
                    Log.e(TAG, "Failed to get download URL: ${e.message}")
                    _isError.value = true
                    _errorMessage.value = "Failed to upload file: ${e.message}"
                }
            }
            .addOnFailureListener { e ->
                // Handle unsuccessful uploads
                Log.e(TAG, "Upload failed: ${e.message}")
                _isError.value = true
                _errorMessage.value = "Upload failed: ${e.message}"

            }
    }

    private fun updateProfilePicture(uri: String) {
        _isLoading.value = true
        _isError.value = false
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(uri))
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _profilePictureUri.value = Uri.parse(uri)
                    _isLoading.value = false
                } else {
                    _isLoading.value = false
                    _isError.value = true
                    _errorMessage.value =
                        "Failed to update profile picture: ${task.exception?.message}"
                }
            }
    }

    //    get user with firebase
    private fun getUser() {
        _isLoading.value = true
        _isError.value = false
        try {
            _username.value = currentUser?.displayName
            _email.value = currentUser?.email
            _createdAt.value = currentUser?.metadata?.creationTimestamp?.let { Date(it).toString() }
            _profilePictureUri.value = currentUser?.photoUrl
            _isLoading.value = false
        } catch (e: Exception) {
            _isLoading.value = false
            _isError.value = true
            Log.e(TAG, "Failed to load user data: ${e.message}")
        }
    }

//    logout with firebase
//    fun logout() {
//        _isLoading.value = true
//        _isError.value = false
//
//        try {
//            firebaseAuth.signOut()
//            _isLoggedOut.value = true
//            _isLoading.value = false
//        } catch (e: Exception) {
//            _isLoading.value = false
//            _isError.value = true
//            _errorMessage.value = "Failed to log out: ${e.message}"
//            Log.e(TAG, "Failed to load user data: ${e.message}")
//        }
//    }

        fun logout() {
            _isLoading.value = true
            _isError.value = false

            viewModelScope.launch {
                try {
                    userPreference.logout()
                    _isLoggedOut.value = true
                    _isLoading.value = false
                } catch (e: Exception) {
                    _isLoading.value = false
                    _isError.value = true
                    _errorMessage.value = "Failed to log out: ${e.message}"
                    Log.e(TAG, "Failed to log out: ${e.message}")
                }
            }
        }

        companion object {
            private const val TAG = "ProfileViewModel"
        }

}