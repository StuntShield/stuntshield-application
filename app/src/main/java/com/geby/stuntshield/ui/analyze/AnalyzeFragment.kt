package com.geby.stuntshield.ui.analyze

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.geby.stuntshield.data.remote.ApiConfig
import com.geby.stuntshield.databinding.FragmentAnalyzeBinding
import com.geby.stuntshield.ui.result.ResultActivity
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private lateinit var fragmentManager: FragmentManager
    private var selectedGender: String? = null


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

        setupGenderSelection()

        fragmentManager = requireActivity().supportFragmentManager
        val analyzeButton = binding.analyzeButton
        analyzeButton.setOnClickListener {
            uploadData()
        }
        return root
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

    private fun selectGender(selectedGender: String?) {
        binding.boyCv.isSelected = selectedGender == "laki-laki"
        binding.girlCv.isSelected = selectedGender == "perempuan"
    }

    private fun uploadData() {
        val inputYear = binding.etYears.text.toString()
        val inputMonth = binding.etMonths.text.toString()
        val inputDay = binding.etDays.text.toString()
        val inputWeight = binding.etWeight.text.toString()
        val inputHeight = binding.etHeight.text.toString()

        val yearBody = inputYear.toRequestBody("text/plain".toMediaType())
        val monthBody = inputMonth.toRequestBody("text/plain".toMediaType())
        val dayBody = inputDay.toRequestBody("text/plain".toMediaType())
        val genderBody = selectedGender?.toRequestBody("text/plain".toMediaType())
        val heightBody = inputHeight.toRequestBody("text/plain".toMediaType())

        lifecycleScope.launch {
            try {
                val apiService = ApiConfig().getApiService()
                val successResponse = apiService.analyzeData(yearBody, monthBody, dayBody, genderBody!!, heightBody)

                Log.d("AnalyzeFragment", "Server response: $successResponse")
                val predict = successResponse.data?.jsonMemberClass.toString()
                val confidenceScore = successResponse.data?.presentase.toString()
                goToResult(selectedGender.toString(), "$inputYear tahun, $inputMonth bulan, $inputDay hari", inputWeight, inputHeight, predict, "$confidenceScore%")
            } catch (e: JsonSyntaxException) {
                Log.e("AnalyzeFragment", "JSON parsing error", e)
                showToast("Invalid response format")
            } catch (e: Exception) {
                Log.e("AnalyzeFragment", "Error in API call", e)
                showToast(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    private fun goToResult(gender: String, age: String, weight: String, height: String, predict: String, confidenceScore: String) {
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra("gender", gender)
            putExtra("age", age)
            putExtra("weight", weight)
            putExtra("height", height)
            putExtra("predict", predict)
            putExtra("confidenceScore", confidenceScore)
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}