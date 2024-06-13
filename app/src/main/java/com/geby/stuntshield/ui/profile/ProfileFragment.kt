package com.geby.stuntshield.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.geby.stuntshield.data.local.pref.UserPreference
import com.geby.stuntshield.data.local.pref.dataStore
import com.geby.stuntshield.databinding.FragmentProfileBinding
import com.geby.stuntshield.ui.MainViewModel
import com.geby.stuntshield.ui.ViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val pref = UserPreference.getInstance(requireActivity().application.dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        setupAction()

        return root
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            mainViewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}