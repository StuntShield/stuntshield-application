package com.geby.stuntshield.ui.result

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.geby.stuntshield.R
import com.geby.stuntshield.data.response.AnalyzeResponse
import com.geby.stuntshield.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.hasil_analisis)

        displayResult()

        val drawable = ContextCompat.getDrawable(this, R.drawable.status_circle)
        binding.stuntDetect.background = drawable
    }

    private fun displayResult() {
        val result: AnalyzeResponse? = intent.getParcelableExtra("result")
        result.let {
            val stuntResult = result?.data?.stunting?.jsonMemberClass
            val stuntPercentage = result?.data?.stunting?.presentase
            val recommendations = result?.data?.recommendation
            val inputGender = intent.getStringExtra("gender") ?: "No result"
            val inputAge = intent.getStringExtra("age") ?: "No result"
            val inputHeight = intent.getStringExtra("height") ?: "No result"
            val inputWeight = intent.getStringExtra("weight") ?: "No result"

            with(binding) {
                resultGender.text = inputGender
                resultAge.text = inputAge
                resultHeight.text = inputHeight
                resultWeight.text = inputWeight
                stuntedStatus.text = stuntResult
                conScore.text = "${(stuntPercentage?.toFloat() ?: 0.0f).toInt()}%"
                recommendation.text = recommendations
            }
            stuntResult?.let { setTextColor(it) }
        }
    }

    private fun setTextColor(result: String) {
        if (result == "Normal") {
            binding.stuntedStatus.setTextColor(Color.GREEN)
        }
    }
}