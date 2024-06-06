package com.geby.stuntshield.ui.analyze

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.geby.stuntshield.data.remote.ApiConfig
import com.geby.stuntshield.data.response.AnalyzeResponse
import com.geby.stuntshield.databinding.FragmentAnalyzeBinding
import com.geby.stuntshield.ui.result.ResultActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

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
            selectedGender = "boy"
            Toast.makeText(requireContext(), "Boy selected", Toast.LENGTH_SHORT).show()
        }

        binding.girlCv.setOnClickListener {
            selectedGender = "girl"
            Toast.makeText(requireContext(), "Girl selected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun uploadData() {
        val inputMonth = binding.etMonths.text.toString()
        val inputDay = binding.etDays.text.toString()
        val inputHeight = binding.etHeight.text.toString()

        val monthBody = inputMonth.toRequestBody("text/plain".toMediaType())
        val dayBody = inputDay.toRequestBody("text/plain".toMediaType())
        val genderBody = selectedGender?.toRequestBody("text/plain".toMediaType())
        val heightBody = inputHeight.toRequestBody("text/plain".toMediaType())


        lifecycleScope.launch {
            try {
                val apiService = ApiConfig().getApiService()
                val successResponse = genderBody?.let { apiService.analyzeData(monthBody, dayBody, it, heightBody) }
                showToast(successResponse?.status?.message.toString())

                val intent = Intent(requireContext(), ResultActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, AnalyzeResponse::class.java)
                showToast(errorResponse.toString())
            }
        }
    }

//    private fun showLoading(isLoading: Boolean) {
//        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
//    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}