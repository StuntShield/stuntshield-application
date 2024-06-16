package com.geby.stuntshield.ui.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.geby.stuntshield.databinding.FragmentProfileBinding
import com.geby.stuntshield.ui.MainViewModel
import com.geby.stuntshield.ui.ViewModelFactory
import com.geby.stuntshield.ui.welcome.WelcomeActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

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

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            profileViewModel.updateProfilePicture(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        profileViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        profileViewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding.btnLogout.visibility = if (isError) View.VISIBLE else View.GONE
        }
        profileViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        // logout
        profileViewModel.isLoggedOut.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                navigateToWelcome()
            }
        }
        profileViewModel.navigateToWelcome.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                navigateToWelcome()
            }
        }
        binding.btnLogout.setOnClickListener {
            mainViewModel.logout()
            profileViewModel.logout()
        }

        //Edit Photo
        binding.floatingActionButton.setOnClickListener {
            if (allPermissionsGranted()) {
                pickImageFromGallery()
            } else {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            }
        }

        //userdata
        profileViewModel.username.observe(viewLifecycleOwner) { username ->
            binding.tvUsername.text = username
        }
        profileViewModel.email.observe(viewLifecycleOwner) { email ->
            binding.tvEmail.text = email
        }
        profileViewModel.createdAt.observe(viewLifecycleOwner) { createdAt ->
            binding.tvDate.text = createdAt
        }
        profileViewModel.profilePictureUri.observe(viewLifecycleOwner, Observer { uri ->
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.ivUser)
            Log.d("ProfileFragment", "Profile picture URI: $uri")
        })
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

    private fun navigateToWelcome() {
        val intent = Intent(requireContext(), WelcomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PERMISSION_GRANTED

    private fun pickImageFromGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.READ_MEDIA_IMAGES
    }
}