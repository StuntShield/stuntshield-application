package com.geby.stuntshield.ui.analyze

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.geby.stuntshield.databinding.FragmentAnalyzeBinding
import com.geby.stuntshield.ui.result.ResultActivity

class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private lateinit var fragmentManager: FragmentManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fragmentManager = requireActivity().supportFragmentManager
        val analyzeButton = binding.analyzeButton
        analyzeButton.setOnClickListener {
            val intent = Intent(requireContext(), ResultActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}