package com.geby.stuntshield.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.geby.stuntshield.databinding.FragmentProfileBinding
import com.geby.stuntshield.ui.MainViewModel
import com.geby.stuntshield.ui.welcome.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val pref = UserPreference.getInstance(requireActivity().application.dataStore)
//        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
//        mainViewModel.getUserData().observe(viewLifecycleOwner) { userData ->
//            with(binding) {
//                tvUsername.text = userData.userName
//                tvEmail.text = userData.email
//                Glide.with(requireActivity())
//                    .load(userData.photoUrl)
//                    .into(ivUser)
//            }
//        }
        // Initialize Firebase Auth
        auth = Firebase.auth

        setupAction()

        return root
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
        requireActivity().finish()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}