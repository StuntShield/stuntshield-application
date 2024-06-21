package com.geby.stuntshield.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.geby.stuntshield.R
import com.geby.stuntshield.databinding.FragmentProfileBinding
import com.geby.stuntshield.ui.auth.AuthViewModel
import com.geby.stuntshield.ui.welcome.WelcomeActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel by viewModels<ProfileViewModel>()
    private lateinit var storageRef: StorageReference
    private val authViewModel: AuthViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                pickImageFromGallery()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        storageRef = FirebaseStorage.getInstance().reference

        profileViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        profileViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                profileViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    errorMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Logout dari akun?")
                setPositiveButton("logout") { _, _ ->
                    authViewModel.signOut(requireActivity()) {
                        startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
                        showLoading(true)
                        requireActivity().finish()
                    }
                }

                setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                create()
                show()
            }
        }

        binding.floatingActionButton.setOnClickListener {
            if (allPermissionsGranted()) {
                pickImageFromGallery()
            } else {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            }
        }

        profileViewModel.username.observe(viewLifecycleOwner) { username ->
            binding.tvUsername.text = username
        }
        profileViewModel.email.observe(viewLifecycleOwner) { email ->
            binding.tvEmail.text = email
        }

        profileViewModel.profilePictureUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(binding.ivUser)
                Log.d("ProfileFragment", "Profile picture URI: $uri")
            } else {
                Glide.with(this)
                    .load(R.drawable.profile)
                    .circleCrop()
                    .into(binding.ivUser)
                Log.d("ProfileManagement", "Default profile picture loaded")
            }


        }
        profileViewModel.profilePictureUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                Glide.with(requireContext()).load(it).into(binding.ivUser)
            }
        }

        return root
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PERMISSION_GRANTED

    private fun pickImageFromGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            profileViewModel.uploadFile(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.READ_MEDIA_IMAGES
    }
}
