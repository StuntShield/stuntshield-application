package com.geby.stuntshield.ui.analyze

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.geby.stuntshield.data.ResultState
import com.geby.stuntshield.data.response.AnalyzeResponse
import com.geby.stuntshield.databinding.FragmentAnalyzeBinding
import com.geby.stuntshield.ui.ViewModelFactory
import com.geby.stuntshield.ui.result.ResultActivity

class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private lateinit var fragmentManager: FragmentManager
    private var selectedGender: String? = null
    private val binding get() = _binding!!
    private val viewModel: AnalyzeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setupGenderSelection()
        fragmentManager = requireActivity().supportFragmentManager
        binding.analyzeButton.setOnClickListener {
            analyzeData()
        }
        return root
    }

    private fun analyzeData() {
        val inputYear = binding.etYears.text.toString()
        val inputMonth = binding.etMonths.text.toString()
        val inputDay = binding.etDays.text.toString()
        val inputWeight = binding.etWeight.text.toString()
        val inputHeight = binding.etHeight.text.toString()

        viewModel.analyzeData(selectedGender.toString(), inputYear, inputMonth, inputDay, inputWeight, inputHeight)
            .observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }
                        is ResultState.Success -> {
                            Log.d("Hasil Prediksi:", result.data.data?.recommendation.toString())
                            showToast(result.data.status?.message.toString())
                            goToResult(selectedGender.toString(), "$inputYear tahun, $inputMonth bulan, $inputDay hari", "$inputWeight kg", "$inputHeight kg", result.data)
                        }
                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupGenderSelection() {
        binding.boyCv.setOnClickListener {
            selectedGender = "laki-laki"
            it.isSelected = true
            binding.girlCv.isSelected = false
            Toast.makeText(requireContext(), "Boy selected", Toast.LENGTH_SHORT).show()
        }

        binding.girlCv.setOnClickListener {
            selectedGender = "perempuan"
            it.isSelected = true
            binding.boyCv.isSelected = false
            Toast.makeText(requireContext(), "Girl selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToResult(gender: String, age: String, weight: String, height: String, result: AnalyzeResponse) {
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra("result", result)
            putExtra("gender", gender)
            putExtra("age", age)
            putExtra("weight", weight)
            putExtra("height", height)
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
//        requireActivity().finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}